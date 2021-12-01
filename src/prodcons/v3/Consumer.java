package prodcons.v3;

import java.util.Properties;

public class Consumer extends Thread {

	Properties properties;
	IProdConsBuffer buffer;
	int consTime;
	public boolean processing;
	
	public Consumer(Properties properties, IProdConsBuffer buffer) {
		this.properties = properties;
		this.buffer = buffer;
		consTime = Integer.parseInt(properties.getProperty("consTime","100"));
		System.out.println("Consumer " + this.getId() + " crée");
		processing = false;
	}
	
	@Override
	public void run() {
		System.out.println("Consumer " + this.getId() + " lancé.");
		while(true) {
			try {
				Message msg = buffer.get();
				processing = true;
				System.out.println("Lu par " + this.getId() + " : " + msg.toString());
				sleep(consTime);
				processing = false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
//		System.out.println("Consumer " + this.getId() + " arrété.");
	}
	
	public boolean isProcessing() {
		return processing;
	}
}
