import java.util.Hashtable;


public class RemoteCommunicator extends Thread{
	Hashtable<String, KNXDatapoint> datapoints;
	public RemoteCommunicator(Hashtable<String, KNXDatapoint> datapoints){
		this.datapoints = datapoints;
	}
	
	public void run(){
		// warte auf anfragen: read all bzw write
		// wenn write kann es sich um einen ReadWriteable datapoint oder einen Writeable datapoint handeln 
		try {
			// some tests for debugging:
			Thread.sleep(500);
			received("dpt2", "true");
			received("dpt3", "false");
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readAll(){
		for(String name : datapoints.keySet()){
			KNXDatapoint d = datapoints.get(name);
			if(d instanceof KNXReadableDatapoint){
				send(name, ((KNXReadableDatapoint)d).readValue());
			}
			else{
				System.out.println("RC: Error: Datapoint " + name + " is not readable");
			}
		}
	}
	
	public void received(String name, String value){
		KNXDatapoint d = (KNXDatapoint)datapoints.get(name);
		System.out.println("RC: Received value " + value + " for " + name);
		if(d == null){
			System.out.println("RC: error " + name + " is null");
		}
		if(d instanceof KNXWriteableDatapoint){
			((KNXWriteableDatapoint)d).writeValue(value);
		}
		else{
			System.out.println("RC: Error: Datapoint " + name + " is not writeable");
		}
	}
	
	public void send(String name, String value){
		System.out.println("RC: send: " + name + ": " + value);
	}
}
