import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

class SwitchImageButton extends JLabel implements MouseListener
{
	private boolean editable = true;
	private boolean state = false;
	
	private Image active_img;
	private Image inactive_img;
	
	private ArrayList<ActionListener> al;

    SwitchImageButton (Image inactive, Image active, boolean editable, boolean init) 
    {
    	state = init;
    	
    	inactive_img = inactive;
    	active_img = active;
    	
	   	setOpaque(false);
	   	
	   	al = new ArrayList<ActionListener>();    	
    	addMouseListener(this); 		
    }
    
    SwitchImageButton (Image inactive, Image active, boolean editable) 
    {
    	this(inactive,active,editable,false);
    }
    
    void addActionListener(ActionListener al)
    {
    	this.al.add(al);
    }
    
	public void mouseClicked (MouseEvent mouseEvent)
	{
		if (editable)
		{
			state = !state;
			
			ActionEvent ae = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"stateChange");
			for(ActionListener al: this.al)
			{
				al.actionPerformed(ae);
			}
			
			this.repaint();
		}
	} 
	
	public void setStatus (boolean state)
	{
		this.state = state;
		this.repaint();
	}
	
	public boolean getStatus()
	{
		return state;
	}
	
	public void mouseEntered (MouseEvent mouseEvent) {} 
	public void mousePressed (MouseEvent mouseEvent) {} 
	public void mouseReleased (MouseEvent mouseEvent) {}  
	public void mouseExited (MouseEvent mouseEvent) {}
    
	@Override
	public void paintComponent(Graphics g) 
	{
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                    RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	
		if (state == true)
		{
			g2.drawImage(active_img,0,0,getWidth(),getHeight(),this);
		}
		else
		{
			g2.drawImage(inactive_img,0,0,getWidth(),getHeight(),this);
		}
	}
}
