import java.util.Hashtable;

import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.process.ProcessCommunicator;

/*
 * The shutdownhandler is called by the Runtime Environment, when the application is stopped (e.b. by Ctrl+C)
 * It closes the Connection to the KNX Bus System, the TCP/IP Connection (Remote Communicator) and stopps
 * all Datapoint threads.
 */
public class ShutdownHandler extends Thread {
	public ProcessCommunicator pc;
	RemoteCommunicator rc;
	Hashtable<String, KNXDatapoint> datapoints;

	ShutdownHandler(ProcessCommunicator pc, RemoteCommunicator rc, Hashtable<String, KNXDatapoint> datapoints) {
		this.pc = pc;
		this.rc = rc;
		this.datapoints = datapoints;
	}

	public void run() {
		System.out.println("Shudown Handler called...");
		// Close Process Communicator
		if (pc != null) {
			System.out.print("Detaching link...");
			final KNXNetworkLink lnk = pc.detach();

			if (lnk != null) {
				System.out.println("Closing link...");
				lnk.close();
			}
		}
		// Interrupt all Datapoints
		for (KNXDatapoint d : datapoints.values()) {
			d.interrupt();
		}
		// Interrupt Remote Communicator
		System.out.println("trying to close remote connection");
	}
}
