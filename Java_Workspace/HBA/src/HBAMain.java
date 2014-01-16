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
	public final static boolean DEBUG = false;	// dummy process communicator is used when true
	public final static String localIP = "128.130.56.134"; // local address connected to KNX IP Router
	public final static String hostIP = "128.130.56.129"; // address of KNX IP Router
	public final static int remotePort = 10000;

	public static void main(String[] args) throws Exception{
		int updateInterval = 1000;
	
		// create process communicator
		ProcessCommunicator pc;
		if(DEBUG){
			pc = new ProcessCommunicatorDummy(null);
			updateInterval = 4000;
		}
		else{
			final KNXNetworkLink lnk = createLink();
			pc = new ProcessCommunicatorImpl(lnk);
		}
		
		Hashtable<String, KNXDatapoint> datapoints = new Hashtable<String, KNXDatapoint>();
		// Create Remote Communicator
		RemoteCommunicator rc = new RemoteCommunicator(datapoints, remotePort);
		// Create Datapoints
		datapoints.put("OuterTemp", new KNXReadableDatapoint(pc, rc, "OuterTemp", "0.4.0", "float", updateInterval));
		datapoints.put("InnerTemp", new KNXReadableDatapoint(pc, rc, "InnerTemp", "0.4.1", "float", updateInterval));
		datapoints.put("Fan", new KNXReadWriteableDatapoint(pc, rc, "Fan", "0.1.0", "bool", updateInterval));
		datapoints.put("RightLight", new KNXReadWriteableDatapoint(pc, rc, "RightLight", "0.0.2", "bool", updateInterval));
//		datapoints.put("dpt2", new KNXWriteableDatapoint(pc, rc, "dpt2", "1.1.2", "bool"));

		// Start Remote Communicator
		rc.start();

		// Start all datapoint threads
		for(KNXDatapoint d : datapoints.values()){
			d.start();
		}
		
		// Register Shutdown Handler
		Runtime.getRuntime().addShutdownHook(new ShutdownHandler(pc, rc, datapoints));
	}
	
	private static KNXNetworkLink createLink() throws KNXException {
		KNXMediumSettings medium = TPSettings.TP1;
		InetSocketAddress local;
		InetSocketAddress host;
		int mode;

		try {
			local = new InetSocketAddress(InetAddress.getByName(localIP), 0);
			host = new InetSocketAddress(InetAddress.getByName(hostIP), new Integer(KNXnetIPConnection.IP_PORT));
		} catch (UnknownHostException e) {
			throw new KNXException("Link not created!");
		}

		mode = KNXNetworkLinkIP.TUNNEL; // KNXNetworkLinkIP.ROUTER;

		try{
			return new KNXNetworkLinkIP(mode, local, host, false, medium);
		}catch(KNXException e){
			System.out.println("KNX: ERROR: Could not create Link. Wrong local / host address?");
			return null;
		}
	}


}
