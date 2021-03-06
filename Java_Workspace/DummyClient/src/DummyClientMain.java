import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class DummyClientMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//1. creating a socket to connect to the server
		try {
			Socket socket = new Socket("127.0.0.1", 10000);
			BufferedReader remoteStreamReader;
			OutputStreamWriter remoteStreamWriter;
			remoteStreamReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			remoteStreamWriter = new OutputStreamWriter(socket.getOutputStream());
			Writer w = new Writer(remoteStreamWriter);
			w.start();
			String s = remoteStreamReader.readLine();
			while(!s.equals("quit")){
				System.out.println(s);
				s = remoteStreamReader.readLine();
			}
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Connected to 127.0.0.1 in port 10000");

	}

}
