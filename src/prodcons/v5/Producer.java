package prodcons.v5;

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
		nbaproduire =  min + rand.nextInt(1+max - min);
		prodTime = Integer.parseInt(properties.getProperty("prodTime","100"));
		System.out.println("Producer " + this.getId() + " crée, et produira " + nbaproduire + " messages.");
	}
	
	@Override
	public void run() {
		System.out.println("Producer " + this.getId() + " lancé.");
		for(int i = 0; i < nbaproduire; i++) {
			try {
				buffer.put(new Message(((int)getId()),"" + i));
				System.out.println("Producer " + getId() + " met \"" + i + "\" dans le buffer");
				sleep(prodTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Producer " + getId() + " a finit de produire.");
	}
}
