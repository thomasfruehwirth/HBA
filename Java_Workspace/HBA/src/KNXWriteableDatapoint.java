import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.datapoint.StateDP;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.process.ProcessCommunicator;

/*
 * A local respresentation of a writeable KNX Datpoint.
 * A write operation performs a write on the specified address. 
 */
public class KNXWriteableDatapoint extends KNXDatapoint {
	public KNXWriteableDatapoint(ProcessCommunicator pc, RemoteCommunicator rc, String name, String groupAddress, String dpt) {
		try {
			this.pc = pc;
			this.rc = rc;
			this.name = name;
			this.type = dpt;
			GroupAddress gA = new GroupAddress(groupAddress); // create a Group Address Object
			dp = new StateDP(gA, "", 0, getDPT(dpt)); // create a Datapoint Object from the Group Address Object
		} catch (KNXFormatException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			for (;;) {
				synchronized (this) {
					this.wait(); // might be interrupted (Program ends) or notified (new value ready for writing)
				}
				pc.write(dp, value);
			}
		} catch (InterruptedException e) {
			// Thread terminating due to interrupt. This is not an ERROR
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeValue(String value) {
		this.value = value;
		synchronized (this) {
			this.notify();
		}
	}
}
