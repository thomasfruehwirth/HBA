import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.KNXMediumSettings;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;


public class HBAMain {
	public final static boolean DEBUG = true;	// dummy process communicator is used when true

	public static void main(String[] args) throws Exception{
		// create process communicator
		ProcessCommunicator pc ;
		if(DEBUG){
			pc = new ProcessCommunicatorDummy(null);
		}
		else{
			final KNXNetworkLink lnk = createLink();
			pc = new ProcessCommunicatorImpl(lnk);
			registerShutdownHandler();
		}
		
		Hashtable<String, KNXDatapoint> datapoints = new Hashtable<String, KNXDatapoint>();
		// Create Remote Communicator
		RemoteCommunicator rc = new RemoteCommunicator(datapoints);
		// Create Datapoints
		datapoints.put("dpt1", new KNXReadableDatapoint(pc, rc, "dpt1", "1.1.1", "bool", 1000));
		datapoints.put("dpt2", new KNXWriteableDatapoint(pc, rc, "dpt2", "1.1.2", "bool"));
		datapoints.put("dpt3", new KNXWriteableDatapoint(pc, rc, "dpt3", "1.1.3", "bool"));
		// Start Remote Communicator
		rc.start();
		// create a Thread from each datapoint
		for(KNXDatapoint d : datapoints.values()){
			d.start();
		}
		Thread.sleep(10000);
		// interrupt all datapoints
		for(KNXDatapoint d : datapoints.values()){
			d.interrupt();
		}
	}

	private static void registerShutdownHandler() {
		// TODO Auto-generated method stub
		
	}

	private static KNXNetworkLink createLink() throws Exception{
		KNXMediumSettings medium = TPSettings.TP1;
		InetSocketAddress local;
		InetSocketAddress host;
		int mode;
		
		try
		{
			local = new InetSocketAddress(InetAddress.getByName("128.130.56.133"),0);
			host =  new InetSocketAddress(InetAddress.getByName("128.130.56.129"), new Integer(KNXnetIPConnection.IP_PORT));
		}catch (UnknownHostException e) 
		{
			throw new KNXException("Link not created!");
		}
		
		mode = KNXNetworkLinkIP.TUNNEL; // KNXNetworkLinkIP.ROUTER;
		
		return new KNXNetworkLinkIP(mode,local,host,false,medium);
	}

}
