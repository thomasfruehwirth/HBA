import tuwien.auto.calimero.datapoint.Datapoint;
import tuwien.auto.calimero.process.ProcessCommunicator;


public abstract class KNXDatapoint extends Thread{
	protected String value;
	public String name;
	public String type;
	ProcessCommunicator pc;
	RemoteCommunicator rc;
	Datapoint dp; // KNX Datapoint
	
	protected String getDPT(String dpt)
	{
		if (dpt.equals("switch"))
			return "1.001";
		if (dpt.equals("bool"))
			return "1.002";
		if (dpt.equals("string"))
			return "16.001";
		if (dpt.equals("float"))
			return "9.002";
		if (dpt.equals("ucount"))
			return "5.010";
		if (dpt.equals("angle"))
			return "5.003";
		return dpt;
	}

}
