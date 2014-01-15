import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import tuwien.auto.calimero.datapoint.Datapoint;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.KNXMediumSettings;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

public class KNXCommunicator {
	public ProcessCommunicator pc;

	public KNXCommunicator() throws KNXException {
	}

	public void init() throws KNXException {
		final KNXNetworkLink lnk = createLink();

		pc = new ProcessCommunicatorImpl(lnk);

		registerShutdownHandler();
	}

	private KNXNetworkLink createLink() throws KNXException {
		KNXMediumSettings medium = TPSettings.TP1;
		InetSocketAddress local;
		InetSocketAddress host;
		int mode;

		try {
			local = new InetSocketAddress(InetAddress.getByName("128.130.56.132"), 0);
			host = new InetSocketAddress(InetAddress.getByName("128.130.56.129"), new Integer(KNXnetIPConnection.IP_PORT));
		} catch (UnknownHostException e) {
			throw new KNXException("Link not created!");
		}

		mode = KNXNetworkLinkIP.TUNNEL; // KNXNetworkLinkIP.ROUTER;

		try{
			return new KNXNetworkLinkIP(mode, local, host, false, medium);
		}catch(KNXException e){
			System.out.println("ERROR: Could not create Link. Wrong local / host address?");
			return null;
		}
	}

	public String getDPT(String dpt) {
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

	public String read(Datapoint dp) throws KNXException {
		return pc.read(dp);
	}

	public void write(Datapoint dp, String value) throws KNXException {
		pc.write(dp, value);
	}

	public void quit() {
		System.out.println("Shudown Handler called...");
		if (pc != null) {
			System.out.print("Detaching link...");
			final KNXNetworkLink lnk = pc.detach();

			if (lnk != null) {
				System.out.println("Closing link...");
				lnk.close();
			}
		}
	}

	private final class ShutdownHandler extends Thread {
		ShutdownHandler() {
		}

		public void run() {
			quit();
		}
	}

	private void registerShutdownHandler() {
		System.out.println("Registering Shutdown Handler...");
		Runtime.getRuntime().addShutdownHook(new ShutdownHandler());
	}
}