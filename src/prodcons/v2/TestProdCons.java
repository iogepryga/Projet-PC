package prodcons.v2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

public class TestProdCons {

	public static void main(String args[]) throws InterruptedException {
		Properties properties = new Properties();
		try {
			properties.loadFromXML(new FileInputStream("src/prodcons/v1/options.xml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IProdConsBuffer buffer = new ProdConsBuffer(properties);
		int nProd = Integer.parseInt(properties.getProperty("nProd", "10"));
		int nCons = Integer.parseInt(properties.getProperty("nCons", "10"));
//		Producer[] producers = new Producer[Integer.parseInt(properties.getProperty("nProd","10"))];
//		Consumer[] consumers = new Consumer[Integer.parseInt(properties.getProperty("nCons","10"))];

		Consumer[] consumers = new Consumer[nCons];
		Producer[] producers = new Producer[nProd];

		ArrayList<Thread> list = new ArrayList<Thread>();
		for (int p = 0; p < nProd; p++) {
			producers[p] = new Producer(properties, buffer);
			list.add(producers[p]);
		}
		for (int c = 0; c < nCons; c++) {
			consumers[c] = new Consumer(properties, buffer);
			list.add(consumers[c]);
		}

		Collections.shuffle(list);

		for (Thread T : list)
			T.start();

		for (Producer P : producers)
			P.join();

		System.out.println("Producer tous arrété.");

		while (buffer.nmsg() > 0) {
		}

		System.out.println("Plus de message dans le buffer.");

		for (Consumer C : consumers) {
//			C.continuee = false;
			while (C.isProcessing()) {
			}
			C.interrupt();
			C.join();
		}

		System.out.println("Programme terminé.");
	}
}
