import java.net.Socket;
import java.io.*;

public class Listener extends Thread 
{
	private Communicator com;
	private BufferedReader in;
	private String s;

	boolean fPause = false;

	public Listener(Communicator com, BufferedReader in)
	{
		super();

		fPause = false;
		this.com = com;
		this.in = in;
	}

	@Override 
	public void run()
	{
		while(true)
		{
			synchronized (this) 
			{
				while (fPause) 
				{
					try 
					{
						wait();
					} 
					catch (Exception e) 
					{
						com.alert(s,"Listener");
					}
				}

				try
				{
					// blocking read
					s = in.readLine();
					com.newLine(s);		
				}
				catch(Exception e)
				{
					com.alert(s,"Listener");
				}
			}
		}
	}

	public void pause() 
	{
		fPause = true;
	}
 
	public void proceed() 
	{
		fPause = false;
		notify();
	}
}
