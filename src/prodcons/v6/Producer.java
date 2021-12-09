package prodcons.v6;

import java.util.Properties;
import java.util.Random;

public class Producer extends Thread {

	Properties properties;
	IProdConsBuffer buffer;
	int nbaproduire;
	int prodTime;
	Random rand;
	
	public Producer(Properties properties, IProdConsBuffer buffer) {
		this.properties = properties;
		this.buffer = buffer;
		rand = new Random();
		int min = Integer.parseInt(properties.getProperty("minProd","10"));
		int max = Integer.parseInt(properties.getProperty("maxProd","100"));
		nbaproduire =  min + rand.nextInt(1+max - min);
		prodTime = Integer.parseInt(properties.getProperty("prodTime","100"));
		System.out.println("Producer " + this.getId() + " crée, et produira " + nbaproduire + " messages.");
	}
	
	@Override
	public void run() {
		System.out.println("+P" + this.getId() + " lancé.");
		for(int i = 0; i < nbaproduire; i++) {
			try {
				if(rand.nextInt(2) == 0) {
					System.out.println("++P" + getId() + " veut placer \"" + i + "\"");
					buffer.put(new Message(((int)getId()),"" + i));
					sleep(prodTime);
				} else {
					int k = 2 + rand.nextInt(4);
					System.out.println("++P" + getId() + " veut placer \"" + i + "\" en " + k + " exemplaires");
					buffer.put(new Message(((int)getId()),"" + i),k);
					sleep(prodTime);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Producer " + getId() + " a finit.");
	}
}
