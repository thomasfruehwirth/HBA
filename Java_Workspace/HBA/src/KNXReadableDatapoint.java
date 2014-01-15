import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.datapoint.StateDP;
import tuwien.auto.calimero.process.ProcessCommunicator;

/*
 * A local respresentation of a readable KNX Datpoint. 
 * The specified address is polled periodically with Period updateInterval. 
 * A read operation is performed on the locally stored value, not on the bus.
 */
public class KNXReadableDatapoint extends KNXDatapoint {
	private int updateInterval; // determines the time between two successive polls of the value
	
	public KNXReadableDatapoint(ProcessCommunicator pc, RemoteCommunicator rc, String name, String groupAddress, String dpt, int updateInterval) {
		try {
			this.pc = pc;
			this.rc = rc;
			this.name = name;
			this.type = dpt;
			GroupAddress gA = new GroupAddress(groupAddress); // create a Group Address Object
			dp = new StateDP(gA, "", 0, getDPT(dpt)); // create a Datapoint Object from the Group Address Object
			this.updateInterval = updateInterval;
			value = pc.read(dp); // perform initial read
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			for(;;){
				Thread.sleep(updateInterval); // might be interrupted
				String newValue = pc.read(dp);
				if(!newValue.equals(value)){  // on change, the new value is published using the remote communicator
					value = newValue;
					rc.send(name, newValue);
				}
			}
		} catch (InterruptedException e) {
			// Thread terminating due to interrupt. This is not an ERROR
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public String readValue(){
		return value;
	}
}
