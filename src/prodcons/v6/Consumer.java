package prodcons.v6;

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
				if (rand.nextInt(2) == 0) {
					int k = 1 + rand.nextInt(4);
					System.out.println("-----------------------------------------C" + this.getId() + " veut lire " + k
							+ " messages.");
					Message[] msg = buffer.get(k);
					processing = true;
					String tmp = "";
					for (int m = 0; m < msg.length; m++)
						tmp += "-------------------------------------------C" + this.getId() + " a lu : "
								+ msg[m].toString() + "\n";
					System.out.print(tmp);
					sleep(consTime);
					processing = false;
				} else {
					System.out.println("-----------------------------------------C"+ this.getId() + " veut lire.");
					Message msg = buffer.get();
					processing = true;
					System.out.println("-------------------------------------------C" + this.getId() + " a lu : " + msg.toString());
					sleep(consTime);
					processing = false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("----------------------------------------C " + this.getId() + " arrété.");
				return;
			}
		}
//		System.out.println("Consumer " + this.getId() + " arrété.");
	}
	
	public boolean isProcessing() {
		return processing;
	}
}
