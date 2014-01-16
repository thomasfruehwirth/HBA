import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.net.Socket;
import java.io.*;

public class Communicator extends JApplet implements KeyListener, ActionListener
{
	/*****************************************************/

	private final Point posFanL = new Point(20,20);
	private final Point posFanR = new Point(85,20);
	
	private final Point posLightL = new Point(15,75);
	private final Point posLightR = new Point(40,75);
	
	private final Point posTempI = new Point(92,47);
	private final Point posTempO = new Point(50,5);
	private final Point posTempS = new Point(62,26);
	
	private final int ICON_SIZE = 60;
	
	private final int port = 10000;

	/*****************************************************/

	private MediaTracker mt;

	private Image imgBk;
	
	private Image imgConnected;
	private Image imgDisconnected;

	private Image imgFanOn;
	private Image imgFanOff;
	
	private Image imgLightOn;
	private Image imgLightOff;
	
	private Image imgTemp;	
	
	private final Border raisedbevel = BorderFactory.createRaisedBevelBorder();
	
	private JPanel mainPanel;
	private JSplitPane mainSplitPane;
	private JPanel statusPane;
	private JPanel connectedPanel;
	private SwitchImageButton btnConnected;
	private JScrollPane scrollStatus;
	private JTextArea statusArea;
	private JPanel imgPane;
	private JScrollPane imgScrollPane;
	private ImagePane imgPanel;	
	private JPanel zoomPanel;
	private JSlider zoomSlide;
	
	private Rectangle imgRect;
	private Rectangle imgRectOrig;
	private float imgRatio;
	
	private float zoomFactor;
	private float actZoom;
	private float maxZoom;
	private boolean zoomW;	
	
	private SwitchImageButton btnFanL;
	private SwitchImageButton btnFanR;
	
	private SwitchImageButton btnLightL;
	private SwitchImageButton btnLightR;
	
	private SwitchImageButton btnTempI;
	private JTextField txtTempI;
	private SwitchImageButton btnTempO;
	private JTextField txtTempO;
	private SwitchImageButton btnTempS;
	private JTextField txtTempS;

	private int tempTxtWidth;
	private int tempTxtHeight;
	
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	
	private Listener listener;
	
	public Communicator()
	{
	}

	public void init()
	{		
		mt = new MediaTracker(this);
		FontMetrics fontMetrics;
		
		imgBk = getImage(getCodeBase(),"bk.jpg");
		mt.addImage(imgBk,0);
		
		imgConnected = getImage(getCodeBase(),"icons/connected.png");
		mt.addImage(imgConnected,1);
		imgDisconnected = getImage(getCodeBase(),"icons/disconnected.png");
		mt.addImage(imgDisconnected,2);
		
		imgFanOn = getImage(getCodeBase(),"icons/fan_on.png");
		mt.addImage(imgFanOn,3);
		imgFanOff = getImage(getCodeBase(),"icons/fan_off.png");
		mt.addImage(imgFanOff,4);	
		
		imgLightOn = getImage(getCodeBase(),"icons/light_on.png");
		mt.addImage(imgLightOn,5);
		imgLightOff = getImage(getCodeBase(),"icons/light_off.png");
		mt.addImage(imgLightOff,6);
		
		imgTemp = getImage(getCodeBase(),"icons/temp.png");
		mt.addImage(imgTemp,7);
		
		Container pane = this.getContentPane();
		
		mainPanel = new JPanel();
		
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));
		mainPanel.setBorder(raisedbevel);
		mainPanel.add(Box.createRigidArea(new Dimension(0,2)));
		
		JLabel header = new JLabel("KNX & Calimero");
		header.setFont (header.getFont().deriveFont(24.0f));
		mainPanel.add(header);
		
		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		statusPane = new JPanel();
		statusPane.setMinimumSize(new Dimension(200,100));
		statusPane.setLayout(new BoxLayout(statusPane, BoxLayout.PAGE_AXIS));
		statusPane.add(Box.createRigidArea(new Dimension(0,2)));
		
		connectedPanel = new JPanel();
		connectedPanel.setMinimumSize(new Dimension(40,40));
		connectedPanel.setPreferredSize(new Dimension(40,40));
	
		connectedPanel.setBorder(raisedbevel);
		connectedPanel.setLayout(null);

		btnConnected = new SwitchImageButton(imgDisconnected,imgConnected,false,false);
		btnConnected.setBounds(0,0,40,40);
		btnConnected.setVisible(true);
		
		connectedPanel.add(btnConnected);
		statusPane.add(connectedPanel);
		
		statusArea = new JTextArea();
		statusArea.setBackground(Color.LIGHT_GRAY);
		statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
		
		scrollStatus = new JScrollPane(statusArea);
		scrollStatus.setMaximumSize(new Dimension(Short.MAX_VALUE,Short.MAX_VALUE));
		scrollStatus.setPreferredSize(new Dimension(Short.MAX_VALUE,Short.MAX_VALUE));
		statusPane.add(scrollStatus);
		
		mainSplitPane.setLeftComponent(statusPane);
		
		imgPane = new JPanel();
		imgPane.setLayout(new BoxLayout(imgPane,BoxLayout.PAGE_AXIS));
		imgPane.add(Box.createRigidArea(new Dimension(0,2)));
		
		imgPanel = new ImagePane(imgBk);
		imgPanel.setLayout(null);
		
		try
		{
			mt.waitForAll();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		while(!mt.checkAll(true)){}
		
		imgScrollPane = new JScrollPane(imgPanel);
		imgScrollPane.setMaximumSize(new Dimension(Short.MAX_VALUE,Short.MAX_VALUE));
		imgScrollPane.setPreferredSize(new Dimension(Short.MAX_VALUE,Short.MAX_VALUE));
		imgScrollPane.addComponentListener(new ResizeListener(this));
		imgPane.add(imgScrollPane);
		
		zoomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));		
		
		zoomSlide = new JSlider();
		zoomSlide.setPaintTicks(true);
		zoomSlide.setPaintLabels(true);
		zoomSlide.setMinimum(0);
		zoomSlide.setMaximum(100);
		zoomSlide.setValue(0);
		zoomSlide.addChangeListener(new ZoomListener(this));
		
		zoomPanel.add(zoomSlide);
		imgPane.add(zoomPanel);
		
		mainSplitPane.setRightComponent(imgPane);
		
		mainPanel.add(mainSplitPane);
		pane.add(mainPanel);
		
		imgRectOrig = new Rectangle(0,0,imgBk.getWidth(null),imgBk.getHeight(null));
		imgRect = new Rectangle();
		imgRatio = (float)imgRectOrig.width/(float)imgRectOrig.height;
		zoomFactor = 0;
		
		calcZoom();
		
		btnFanL = new SwitchImageButton(imgFanOff,imgFanOn,false);
		btnFanL.addActionListener(this);
		btnFanL.setBounds(getAbsPos(posFanL).x-ICON_SIZE/2,getAbsPos(posFanL).y-ICON_SIZE/2,ICON_SIZE,ICON_SIZE);
		imgPanel.add(btnFanL);
	
		btnFanR = new SwitchImageButton(imgFanOff,imgFanOn,false);
		btnFanR.addActionListener(this);
		btnFanR.setBounds(getAbsPos(posFanR).x-ICON_SIZE/2,getAbsPos(posFanR).y-ICON_SIZE/2,ICON_SIZE,ICON_SIZE);
		imgPanel.add(btnFanR);
	
		btnLightL = new SwitchImageButton(imgLightOff,imgLightOn,false);
		btnLightL.addActionListener(this);
		btnLightL.setBounds(getAbsPos(posLightL).x-ICON_SIZE/2,getAbsPos(posLightL).y-ICON_SIZE/2,ICON_SIZE,ICON_SIZE);
		imgPanel.add(btnLightL);
		
		btnLightR = new SwitchImageButton(imgLightOff,imgLightOn,false);
		btnLightR.addActionListener(this);
		btnLightR.setBounds(getAbsPos(posLightR).x-ICON_SIZE/2,getAbsPos(posLightR).y-ICON_SIZE/2,ICON_SIZE,ICON_SIZE);
		imgPanel.add(btnLightR);
		
		btnTempI = new SwitchImageButton(imgTemp,imgTemp,false);
		btnTempI.setBounds(getAbsPos(posTempI).x-ICON_SIZE/2,getAbsPos(posTempI).y-ICON_SIZE/2,ICON_SIZE,ICON_SIZE);
		imgPanel.add(btnTempI);
		
		txtTempI = new JTextField();
		txtTempI.setEditable(false);
		fontMetrics = txtTempI.getFontMetrics(txtTempI.getFont());
		tempTxtWidth = fontMetrics.stringWidth("-00.0K ")+4;
		tempTxtHeight = fontMetrics.getHeight()+4;
		txtTempI.setBounds(getAbsPos(posTempI).x-ICON_SIZE/2-tempTxtWidth,
		                   getAbsPos(posTempI).y-tempTxtHeight/2,tempTxtWidth,tempTxtHeight);
		imgPanel.add(txtTempI);
	
		btnTempO = new SwitchImageButton(imgTemp,imgTemp,false);
		btnTempO.setBounds(getAbsPos(posTempO).x-ICON_SIZE/2,getAbsPos(posTempO).y-ICON_SIZE/2,ICON_SIZE,ICON_SIZE);
		imgPanel.add(btnTempO);
		
		txtTempO = new JTextField();
		txtTempO.setEditable(false);
		txtTempO.setBounds(getAbsPos(posTempI).x-ICON_SIZE/2-tempTxtWidth,
		                   getAbsPos(posTempI).y-tempTxtHeight/2,tempTxtWidth,tempTxtHeight);
		imgPanel.add(txtTempO);
		
		btnTempS = new SwitchImageButton(imgTemp,imgTemp,false);
		btnTempS.setBounds(getAbsPos(posTempS).x-ICON_SIZE/2,getAbsPos(posTempS).y-ICON_SIZE/2,ICON_SIZE,ICON_SIZE);
		imgPanel.add(btnTempS);
		
		txtTempS = new JTextField();
		txtTempS.addKeyListener(this);
		txtTempS.setBounds(getAbsPos(posTempI).x-ICON_SIZE/2-tempTxtWidth,
		                   getAbsPos(posTempI).y-tempTxtHeight/2,tempTxtWidth,tempTxtHeight);
		imgPanel.add(txtTempS);
	}
	
	public void calcZoom()
	{
		float viewRatio;
	
		if ((imgScrollPane.getWidth() > 0)&&(imgScrollPane.getHeight() > 0))
		{
			viewRatio = (float)imgScrollPane.getWidth()/(float)imgScrollPane.getHeight();
			
			if (viewRatio >= 1)
			{
				if (imgRatio >= viewRatio)
				{
					maxZoom = (float)imgRectOrig.width/(float)imgScrollPane.getWidth();
					zoomW = true;
				}
				else
				{
					maxZoom = (float)imgRectOrig.height/(float)imgScrollPane.getHeight();
					zoomW = false;
				}
			}
			else
			{
				if (imgRatio < viewRatio)
				{
					maxZoom = (float)imgRectOrig.height/(float)imgScrollPane.getHeight();
					zoomW = false;
				}
				else
				{
					maxZoom = (float)imgRectOrig.width/(float)imgScrollPane.getWidth();
					zoomW = true;
				}
			}
			
			actZoom = 1+(maxZoom-1)*zoomFactor;
			zoomImg();
		}
		else
		{
			maxZoom = 1;
			actZoom = 1;
			zoomImg();
		}
	}
	
	public void setZoom (float factor)
	{		
		zoomFactor = factor;
		actZoom = 1+(maxZoom-1)*factor;
		zoomImg();
	}
	
	private void zoomImg()
	{
		Dimension dim;
	
		if ((imgScrollPane.getWidth() > 0)&&(imgScrollPane.getHeight() > 0))
		{
			if (zoomW == true)
			{
				imgRect.width = (int)((float)imgScrollPane.getWidth()*actZoom);
				imgRect.height = (int)((float)imgRect.width/imgRatio);
			}
			else
			{
				imgRect.height = (int)((float)imgScrollPane.getHeight()*actZoom);
				imgRect.width = (int)((float)imgRect.height*imgRatio);
			}
			
			dim = new Dimension(imgRect.width,imgRect.height);
			
			imgPanel.setInnerSize(dim);
			imgPanel.setPreferredSize(dim);
			imgPanel.setMaximumSize(dim);
			imgPanel.setMinimumSize(dim);
			imgPanel.revalidate();
			imgPanel.repaint();
			
			btnFanL.setBounds(getAbsPos(posFanL).x-ICON_SIZE/2,getAbsPos(posFanL).y-ICON_SIZE/2,ICON_SIZE,ICON_SIZE);
			btnFanR.setBounds(getAbsPos(posFanR).x-ICON_SIZE/2,getAbsPos(posFanR).y-ICON_SIZE/2,ICON_SIZE,ICON_SIZE);
			btnLightL.setBounds(getAbsPos(posLightL).x-ICON_SIZE/2,getAbsPos(posLightL).y-ICON_SIZE/2,ICON_SIZE,ICON_SIZE);
			btnLightR.setBounds(getAbsPos(posLightR).x-ICON_SIZE/2,getAbsPos(posLightR).y-ICON_SIZE/2,ICON_SIZE,ICON_SIZE);
			btnTempI.setBounds(getAbsPos(posTempI).x-ICON_SIZE/2,getAbsPos(posTempI).y-ICON_SIZE/2,ICON_SIZE,ICON_SIZE);
			txtTempI.setBounds(getAbsPos(posTempI).x-ICON_SIZE/2-tempTxtWidth,
		                       getAbsPos(posTempI).y-tempTxtHeight/2,tempTxtWidth,tempTxtHeight);
			btnTempO.setBounds(getAbsPos(posTempO).x-ICON_SIZE/2,getAbsPos(posTempO).y-ICON_SIZE/2,ICON_SIZE,ICON_SIZE);
			txtTempO.setBounds(getAbsPos(posTempO).x-ICON_SIZE/2-tempTxtWidth,
		                       getAbsPos(posTempO).y-tempTxtHeight/2,tempTxtWidth,tempTxtHeight);
			btnTempS.setBounds(getAbsPos(posTempS).x-ICON_SIZE/2,getAbsPos(posTempS).y-ICON_SIZE/2,ICON_SIZE,ICON_SIZE);
			txtTempS.setBounds(getAbsPos(posTempS).x-ICON_SIZE/2-tempTxtWidth,
		                       getAbsPos(posTempS).y-tempTxtHeight/2,tempTxtWidth,tempTxtHeight);

		}
	}
	
	public Point getAbsPos(Point rel)
	{
		Point ret = new Point(0,0);
		
		ret.x = (imgPanel.getInnerSize().width*rel.x)/100;
		if (imgScrollPane.getWidth() > imgPanel.getInnerSize().width)
		{
			ret.x += (imgScrollPane.getWidth()-imgPanel.getInnerSize().width)/2;
		}
		
		ret.y = (imgPanel.getInnerSize().height*rel.y)/100;
		if (imgScrollPane.getHeight() > imgPanel.getInnerSize().height)
		{
			ret.y += (imgScrollPane.getHeight()-imgPanel.getInnerSize().height)/2;
		}
		
		return ret;
	}

	public void start()
	{
		try
		{
			socket = new Socket(getDocumentBase().getHost(),port); 
			
			if (socket.isConnected())
			{
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				
				btnConnected.setStatus(true);
			
				listener = new Listener(this,in);
				listener.start();
			
				out.write("start\n");
				out.flush();
			}
		}
		catch(Exception e)
		{
			alert(e.toString(),"Starting socket...");
		}
	}
	
	public void newLine(String line)
	{
		if (line != null)
		{
			statusArea.insert(line+"\n",0);
			
			if (line.trim().startsWith("LeftLight"))
			{
				btnLightL.setStatus((line.trim().endsWith("true")?true:false));
			}
			else if (line.trim().startsWith("RightLight"))
			{
				btnLightR.setStatus((line.trim().endsWith("true")?true:false));
			}
			else if (line.trim().startsWith("Fan"))
			{
				btnFanL.setStatus((line.trim().endsWith("true")?true:false));
				btnFanR.setStatus((line.trim().endsWith("true")?true:false));
			}
			else if (line.trim().startsWith("InnerTemp"))
			{
				txtTempI.setText(line.trim().substring(("InnerTemp:").length()).trim());
			}
			else if (line.trim().startsWith("OuterTemp"))
			{
				txtTempO.setText(line.trim().substring(("OuterTemp:").length()).trim());
			}
			else if (line.trim().startsWith("TempSetpoint"))
			{
			
			}
		}
	}

	public void stop()
	{
		try
		{
			out.write("stop\n");
			out.flush();
			in.close();
			out.close();
			socket.close();
			btnConnected.setStatus(false);
		}
		catch(Exception e)
		{
			alert(e.toString(),"Closing socket...");
		}
	}
	
	public void destroy()
	{
	}
	
	public void alert(String msg, String title)
	{
		JOptionPane.showMessageDialog(this,msg,title,JOptionPane.ERROR_MESSAGE);
	}	
	
	public void keyPressed(KeyEvent e)
	{
	}

	public void keyTyped(KeyEvent e)
	{
	}

	public void keyReleased(KeyEvent e)
	{
		if (e.getSource() == txtTempS)
		{
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				try
				{
					setValue("write TempSetPoint ",txtTempS.getText());
				}
				catch(Exception ex)
				{
					alert(ex.toString(),"Writting Temperatur setpoint");
				}
			}
		}
	}
	
	public void actionPerformed(ActionEvent e)
	{	
		if (e.getSource() == btnLightL)
		{
			setValue("LeftLight",btnLightL.getStatus()?"true":"false");
		}
		else if (e.getSource() == btnLightR)
		{
			setValue("RightLight",btnLightR.getStatus()?"true":"false");
		}
		else if (e.getSource() == btnFanL)
		{
			boolean status = btnFanL.getStatus();
			btnFanR.setStatus(status);
			setValue("Fan",status?"true":"false");
		}
		else if (e.getSource() == btnFanR)
		{
			boolean status = btnFanR.getStatus();
			btnFanL.setStatus(status);
			setValue("Fan",status?"true":"false");
		}
	}
	
	public void setValue(String name, String value)
	{
		try
		{
			String line = "write "+name+" "+value+"\n";
			statusArea.insert(line,0);
			out.write(line);
			out.flush();
		}
		catch(Exception e)
		{
			alert(e.toString(),"Writing "+name);
		}
	}
}
