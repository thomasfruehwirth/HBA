import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class ZoomListener implements ChangeListener
{
	private Communicator c;

	public ZoomListener(Communicator c)
	{
		this.c = c;
	}
	
	@Override	
	public void stateChanged(ChangeEvent e) 
	{
		JSlider source = (JSlider)e.getSource();
		float range = 0;
		float d = 0;
		
		range = (float)(source.getMaximum()-source.getMinimum());
			
		if (range > 0)
		{
			d = (float)(source.getValue()-source.getMinimum());
			c.setZoom(d/range);
		}
	}
}
