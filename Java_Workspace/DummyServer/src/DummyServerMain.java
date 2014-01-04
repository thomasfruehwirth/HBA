import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class DummyServerMain {

	/**
	 * @param args
	 */
	public static int port = 9876;

	public static void main(String[] args) {

		ServerSocket ss = null;
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (true) {
			try {
				System.out.println("waiting for connection");
				Socket s;
				s = ss.accept();
				System.out.println("Connection established");
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				OutputStreamWriter os = new OutputStreamWriter(s.getOutputStream());
				// schicke ein read all
				os.write("read all\n");
				os.flush();
				Thread.sleep(1000);
				os.write("write dpt4 1.11\n");
				os.flush();
				String line = br.readLine();
				while (!line.equals("quit")) {
					System.out.println("received: " + line);
					line = br.readLine();
					ss.close();
				}
			} catch (Exception e) {
				System.out.println("connection closed");
			}
		}

	}

}
