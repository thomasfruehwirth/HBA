import java.awt.*;
import java.applet.*;
import javax.swing.*;

public class ImagePane extends JPanel 
{ 
	private Image bk;
	private Dimension is;

	public ImagePane(Image bk)
	{
		super();
		this.bk = bk;
		is = new Dimension(0,0);
	}
	
	public void setInnerSize(Dimension is)
	{
		this.is = is;
	}
	
	public Dimension getInnerSize()
	{
		return is;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Rectangle r = new Rectangle(0,0,getWidth(),getHeight());
	
		super.paintComponent(g);
		
		if (is.width < getWidth())
		{
			r.width = is.width;
			r.x = (getWidth()-r.width)/2;
		}
		
		if (is.height < getHeight())
		{
			r.height = is.height;
			r.y = (getHeight()-r.height)/2;
		}
		
		g.drawImage(bk,r.x,r.y,r.width,r.height,this);
	}
}
