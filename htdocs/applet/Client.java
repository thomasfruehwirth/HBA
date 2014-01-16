import java.net.Socket;
import java.io.*;

public class Client
{
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;

	public void tcp_connect (String host, String port) throws Exception
	{
		socket = new Socket(host,Integer.parseInt(port)); 

		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	public boolean tcp_read_ready() throws Exception
	{
		return in.ready();
	}

	public String tcp_read() throws Exception
	{
		return in.readLine();
	}

	public void tcp_write(String msg) throws Exception
	{
		out.write(msg+"\n");
		out.flush();
	}

	public void tcp_close() throws Exception
	{
		in.close();
		out.close();
		socket.close();
	}

}
