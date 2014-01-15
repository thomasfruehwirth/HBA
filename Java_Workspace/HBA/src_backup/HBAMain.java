import java.util.*;

import tuwien.auto.calimero.process.ProcessCommunicator;

public class HBAMain {
	public final static boolean DEBUG = true;	// dummy process communicator is used when true

	public static void main(String[] args) throws Exception{
		// create process communicator
		ProcessCommunicator pc;
		if(DEBUG){
			pc = new ProcessCommunicatorDummy(null);
		}
		else{
//			final KNXNetworkLink lnk = createLink();
//			pc = new ProcessCommunicatorImpl(lnk);		
//			registerShutdownHandler();
			KNXCommunicator KNXC = new KNXCommunicator();
			KNXC.init();
			pc = KNXC.pc;
		}
		
		Hashtable<String, KNXDatapoint> datapoints = new Hashtable<String, KNXDatapoint>();
		// Create Remote Communicator
		RemoteCommunicator rc = new RemoteCommunicator(datapoints);
		// Create Datapoints
		datapoints.put("OuterTemp", new KNXReadableDatapoint(pc, rc, "OuterTemp", "0.4.0", "float", 1000));
		datapoints.put("InnerTemp", new KNXReadableDatapoint(pc, rc, "InnerTemp", "0.4.1", "float", 1000));
		datapoints.put("Fan", new KNXReadWriteableDatapoint(pc, rc, "Fan", "0.1.0", "bool", 1000));
		datapoints.put("RightLight", new KNXReadWriteableDatapoint(pc, rc, "RightLight", "0.0.2", "bool", 1000));
//		datapoints.put("dpt2", new KNXWriteableDatapoint(pc, rc, "dpt2", "1.1.2", "bool"));
//		datapoints.put("dpt3", new KNXWriteableDatapoint(pc, rc, "dpt3", "1.1.3", "bool"));
//		datapoints.put("dpt4", new KNXReadWriteableDatapoint(pc, rc, "dpt4", "1.1.4", "bool", 1000));
		System.out.println("starting remote communicator");
		// Start Remote Communicator
		rc.start();
		System.out.println("starting datapoints");
		// Start all datapoint threads
		for(KNXDatapoint d : datapoints.values()){
			d.start();
		}
		
		System.out.println("waiting some time before terminating");
		Thread.sleep(10000000);
		// interrupt all datapoints
		for(KNXDatapoint d : datapoints.values()){
			d.interrupt();
		}
		// Interrupt Remote Communicator
		if(rc.s != null){
			rc.s.close();
		}
	}
}