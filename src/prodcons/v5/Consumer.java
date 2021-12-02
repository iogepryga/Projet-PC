package prodcons.v5;

import java.util.Properties;
import java.util.Random;

public class Consumer extends Thread {

	Properties properties;
	IProdConsBuffer buffer;
	int consTime;
	public boolean processing;
	Random rand;
	
	public Consumer(Properties properties, IProdConsBuffer buffer) {
		this.properties = properties;
		this.buffer = buffer;
		consTime = Integer.parseInt(properties.getProperty("consTime","100"));
		System.out.println("Consumer " + this.getId() + " crée");
		processing = false;
		rand = new Random();
	}
	
	@Override
	public void run() {
		System.out.println("Consumer " + this.getId() + " lancé.");
		while(true) {
			try {
				int k = 2;//1 + rand.nextInt(4);
				System.out.println("Consumer " + this.getId() + " veut lire " + k + " messages.");
				Message[] msg = buffer.get(k);
				processing = true;
				String tmp = "";
				for(int m = 0 ; m < msg.length ; m++)
					tmp += "Consumer " + this.getId() + " a lu : \"" + msg[m].toString() + "\".\n";
				System.out.print(tmp);
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
