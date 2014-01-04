import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Hashtable;

public class RemoteCommunicator extends Thread {
	Hashtable<String, KNXDatapoint> datapoints;
	public Socket s;
	BufferedReader remoteStreamReader;
	OutputStreamWriter remoteStreamWriter;
	public static int port = 9876;

	public RemoteCommunicator(Hashtable<String, KNXDatapoint> datapoints) {
		this.datapoints = datapoints;
		try {
			// open remote connection
			s = new Socket("127.0.0.1", port);
			remoteStreamReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			remoteStreamWriter = new OutputStreamWriter(s.getOutputStream());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		// warte auf anfragen: read all bzw write
		// wenn write kann es sich um einen ReadWriteable datapoint oder einen Writeable datapoint handeln 
		try {
			// some tests for debugging:
			//			Thread.sleep(500);
			//			received("dpt2", "true");
			//			received("dpt3", "false");
			//			Thread.sleep(2000);
			while (true) {
				String line = remoteStreamReader.readLine();
				if (line.startsWith("read all")) {
					readAll();
				}
				if (line.startsWith("write")) {
					received(line.split(" ")[1], line.split(" ")[2]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	// called when a new value is received via remote connection (i.e. a write command is received)
	public void received(String name, String value) {
		KNXDatapoint d = (KNXDatapoint) datapoints.get(name);
		System.out.println("RC: Received write command: new value " + value + " for " + name);
		if (d == null) {
			System.out.println("RC: error " + name + " is null");
		} else if (d instanceof KNXWriteableDatapoint) {
			((KNXWriteableDatapoint) d).writeValue(value);
		} else if (d instanceof KNXReadWriteableDatapoint) {
			((KNXReadWriteableDatapoint) d).writeValue(value);
		} else {
			System.out.println("RC: Error: Datapoint " + name + " is not writeable");
		}
	}

	// used to send new values via the remote connection (called by readable and readwriteable datapoints)
	public void send(String name, String value) {
		//		System.out.println("RC: send: " + name + ": " + value);
		try {
			remoteStreamWriter.write(name + ":" + value + "\n");
			remoteStreamWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
