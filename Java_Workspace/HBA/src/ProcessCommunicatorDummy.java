import java.text.DecimalFormat;
import java.util.Random;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.Priority;
import tuwien.auto.calimero.datapoint.Datapoint;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessListener;

public class ProcessCommunicatorDummy implements ProcessCommunicator {

	public ProcessCommunicatorDummy(KNXNetworkLink lnk) {

	}

	@Override
	public void addProcessListener(ProcessListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public KNXNetworkLink detach() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Priority getPriority() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getResponseTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String read(Datapoint arg0) throws KNXException {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		Random generator = new Random();
		System.out.println("KNX: reading address " + arg0.getMainAddress());
		float f = generator.nextFloat();
		return df.format(f);
	}

	@Override
	public boolean readBool(GroupAddress arg0) throws KNXException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte readControl(GroupAddress arg0) throws KNXException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float readFloat(GroupAddress arg0) throws KNXException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String readString(GroupAddress arg0) throws KNXException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short readUnsigned(GroupAddress arg0, String arg1) throws KNXException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeProcessListener(ProcessListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPriority(Priority arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setResponseTimeout(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(GroupAddress arg0, boolean arg1) throws KNXTimeoutException, KNXLinkClosedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(GroupAddress arg0, float arg1) throws KNXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(GroupAddress arg0, String arg1) throws KNXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(Datapoint arg0, String arg1) throws KNXException {
		// TODO Auto-generated method stub
		System.out.println("KNX: writing address " + arg0.getMainAddress() + " value: " + arg1);
		// Simulate writing by adding some delay
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(GroupAddress arg0, int arg1, String arg2) throws KNXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(GroupAddress arg0, boolean arg1, byte arg2) throws KNXException {
		// TODO Auto-generated method stub

	}

}
