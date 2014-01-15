import java.io.*;
import java.net.*;


public class Client
{
	ServerSocket ss;
	Socket socket;
	BufferedWriter out;
 	BufferedReader in;
 	String message;
 	public static int port = 10000;

	Client()
	{
	}

	void run()
	{
		try
		{
			
			//1. open Server Socket
			ss = new ServerSocket(port);
			
			System.out.println("Waiting for remote connection");
			socket = ss.accept();

			//2. get Input and Output streams
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			//3: Communicating with the server
			message = in.readLine();
			System.out.println("server> " + message);
			out.write("test\n");
			out.flush();
			out.write("quit\n");
			out.flush();
		}
		catch(UnknownHostException unknownHost)
		{
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
		finally
		{
			//4: Closing connection
			try
			{
				in.close();
				out.close();
				socket.close();
				ss.close();
			}
			catch(IOException ioException)
			{
				ioException.printStackTrace();
			}
		}
	}

	public static void main(String args[])
	{
		Client client = new Client();
		client.run();
	}
}
  
