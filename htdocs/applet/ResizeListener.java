import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class ResizeListener implements ComponentListener
{
	private Communicator c;

	public ResizeListener(Communicator c)
	{
		this.c = c;
	}
	
	public void componentHidden(ComponentEvent e) 
	{
	}

	public void componentMoved(ComponentEvent e) 
	{
	}

	public void componentResized(ComponentEvent e)
	{
		c.calcZoom();
	}

	public void componentShown(ComponentEvent e)
	{
	}
}
