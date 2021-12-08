package prodcons.v2;

import java.util.Properties;
import java.util.Random;

public class Producer extends Thread {

	Properties properties;
	IProdConsBuffer buffer;
	int nbaproduire;
	int prodTime;
	
	public Producer(Properties properties, IProdConsBuffer buffer) {
		this.properties = properties;
		this.buffer = buffer;
		Random rand = new Random();
		int min = Integer.parseInt(properties.getProperty("minProd","10"));
		int max = Integer.parseInt(properties.getProperty("maxProd","100"));
		nbaproduire =  min + rand.nextInt(max - min);
		prodTime = Integer.parseInt(properties.getProperty("prodTime","100"));
		System.out.println("Producer " + this.getId() + " crée, et produira " + nbaproduire + " messages.");
	}
	
	@Override
	public void run() {
		System.out.println("+P" + this.getId() + " lancé.");
		for(int i = 0; i < nbaproduire; i++) {
			try {
				System.out.println("++P" + getId() + " veut placer \"" + i + "\"");
				buffer.put(new Message(((int)getId()),"" + i));
				sleep(prodTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Producer " + getId() + " a finit.");
	}
}
