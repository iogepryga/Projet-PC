package prodcons.v1;

import java.util.Properties;

public class Consumer extends Thread {

	Properties properties;
	IProdConsBuffer buffer;
	int consTime;
	
	public Consumer(Properties properties, IProdConsBuffer buffer) {
		this.properties = properties;
		this.buffer = buffer;
		consTime = Integer.parseInt(properties.getProperty("consTime","100"));
		System.out.println("Consumer " + this.getId() + " crée");
	}
	
	@Override
	public void run() {
		System.out.println("----------------------------------------C " + this.getId() + " lancé.");
		while(true) {
			try {
				System.out.println("-----------------------------------------C"+ this.getId() + " veut lire.");
				Message msg = buffer.get();
				System.out.println("-------------------------------------------C" + this.getId() + " a lu : " + msg.toString());
				sleep(consTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
