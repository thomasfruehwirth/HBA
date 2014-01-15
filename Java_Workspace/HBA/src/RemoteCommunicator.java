import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

/*
 * Opens a Socket and waits for remote connections.
 * When a Connection is established, Readable and Readwriteable Datapoints use the Remote Communictator
 * to transmit the values of the Datapoints, when they change.
 * This behavior is activated and deactivated when "start" or "stop" is received via the TP/IP Connection. 
 * "list" lists all known Datapoints and their types
 * "read all" performs a read operation on all read- and readwriteable Datapoints.
 */
public class RemoteCommunicator extends Thread {
	private Hashtable<String, KNXDatapoint> datapoints;
	private ServerSocket ss;
	private Socket s;
	private BufferedReader remoteStreamReader;
	private OutputStreamWriter remoteStreamWriter;
	private boolean started = false;

	public RemoteCommunicator(Hashtable<String, KNXDatapoint> datapoints, int port) {
		this.datapoints = datapoints;
		try {
			// open remote connection
			ss = new ServerSocket(port);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("RC: ERROR: Error opening Server Socket on port " + port);
			// e.printStackTrace();
		}
	}

	public void run() {
		// warte auf anfragen: read all bzw write
		// wenn write kann es sich um einen ReadWriteable datapoint oder einen Writeable datapoint handeln 
		while (!ss.isClosed()) {
			try {
				System.out.println("RC: Waiting for remote connection");
				started = false;
				s = ss.accept();
				remoteStreamReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
				remoteStreamWriter = new OutputStreamWriter(s.getOutputStream());
				while (true) {
					String line = remoteStreamReader.readLine();
					System.out.println("received remote message: " + line);
					if (line.startsWith("start")) {
						started = true;
					} else if (line.startsWith("stop")) {
						started = false;
					} else if (line.startsWith("list")) {
						list();
					} else if (line.startsWith("read all")) {
						readAll();
					} else if (line.startsWith("write") && line.split(" ").length == 3) {
						received(line.split(" ")[1], line.split(" ")[2]);
					} else {
						this.send("ERROR", "invalid command " + line);
					}
				}
			} catch (IOException e) {
				if (!ss.isClosed()) { // ss.accept() might be interrupted by close(), then it's not an ERROR
					System.out.println("ERROR at remote connection");
					e.printStackTrace();
				}
			}
		}
	}

	// called when a readAll command was received via the remote connection
	// the values of all readable and readwriteable datapoints are sent
	public void readAll() {
		System.out.println("received read all command");
		for (String name : datapoints.keySet()) {
			KNXDatapoint d = datapoints.get(name);
			if (d instanceof KNXReadableDatapoint) {
				send(name, ((KNXReadableDatapoint) d).readValue());
			} else if (d instanceof KNXReadWriteableDatapoint)
				send(name, ((KNXReadWriteableDatapoint) d).readValue());
			else {
				//				System.out.println("RC: Error: Datapoint " + name + " is not readable");
			}
		}
	}

	// called when a list command was received via the remote connection
	// the name, the r/w type and the datatype of all datapoints are sent
	public void list() {
		System.out.println("received list command");
		String s = new String();
		for (String name : datapoints.keySet()) {
			KNXDatapoint d = datapoints.get(name);
			s += d.name;
			if (d instanceof KNXReadableDatapoint) {
				s += " r ";
			} else if (d instanceof KNXReadWriteableDatapoint) {
				s += " rw ";
			} else if (d instanceof KNXReadWriteableDatapoint) {
				s += " w ";
			}

			s += d.type + ";";
		}
		try {
			if (remoteStreamWriter != null) {
				remoteStreamWriter.write(s + "\n");
				remoteStreamWriter.flush();
			}
		} catch (IOException e) {
			System.out.println("RC: ERROR: Remote Connection down. Cannot send List.");
			// e.printStackTrace();
		}

	}

	// called when a new value is received via remote connection (i.e. a write command is received)
	public void received(String name, String value) {
		KNXDatapoint d = (KNXDatapoint) datapoints.get(name);
		System.out.println("RC: Received write command: new value " + value + " for " + name);
		if (d == null) {
			System.out.println("RC: ERROR: " + name + " is null");
		} else if (d instanceof KNXWriteableDatapoint) {
			((KNXWriteableDatapoint) d).writeValue(value);
		} else if (d instanceof KNXReadWriteableDatapoint) {
			((KNXReadWriteableDatapoint) d).writeValue(value);
		} else {
			System.out.println("RC: ERROR: Datapoint " + name + " is not writeable");
		}
	}

	// used to send new values via the remote connection (called by readable and readwriteable datapoints)
	public void send(String name, String value) {
		System.out.println("RC: send: " + name + ": " + value);
		try {
			if (remoteStreamWriter != null && started == true) {
				remoteStreamWriter.write(name + ":" + value + "\n");
				remoteStreamWriter.flush();
			}
		} catch (IOException e) {
			System.out.println("RC: ERROR: Remote Connection down. cannot send value.");
			//			e.printStackTrace();
		}
	}

	public void close() {
		System.out.println("RC: Closing remote connection...");
		if (remoteStreamReader != null) {
			try {
				remoteStreamReader.close();
			} catch (IOException e) {
				System.out.println("RC: Unable to close remote connection");
			}
		}
		if (remoteStreamWriter != null) {
			try {
				remoteStreamWriter.close();
			} catch (IOException e) {
				System.out.println("RC: Unable to close remote connection");
			}
		}
		if (s != null) {
			try {
				s.close();
			} catch (IOException e) {
				System.out.println("RC: Unable to close remote connection");
			}
		}
		if (ss != null) {
			try {
				ss.close();
			} catch (IOException e) {
				System.out.println("RC: Unable to close Server Socket");
			}
		}
	}
}
