import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.datapoint.StateDP;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.process.ProcessCommunicator;

public class KNXReadableDatapoint extends KNXDatapoint {
	private int updateInterval;
	
	public KNXReadableDatapoint(ProcessCommunicator pc, RemoteCommunicator rc, String name, String groupAddress, String dpt, int updateInterval) {
		try {
			this.pc = pc;
			this.rc = rc;
			this.name = name;
			GroupAddress gA = new GroupAddress(groupAddress);
			dp = new StateDP(gA, "", 0, getDPT(dpt));
			debugGAString = groupAddress;
			this.updateInterval = updateInterval;
		} catch (KNXFormatException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
//			System.out.println("Readable Datapoint with group address " + dp.getMainAddress() + " started");
			for(;;){
				Thread.sleep(updateInterval);
				String newValue = pc.read(dp);
				if(!newValue.equals(value)){
					value = newValue;
					rc.send(name, newValue);
				}
			}
		} catch (InterruptedException e) {
//			System.out.println("Thread terminating due to interrupt");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public String readValue(){
		return value;
	}
}
