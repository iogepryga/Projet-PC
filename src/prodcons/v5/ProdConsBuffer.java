package prodcons.v5;

import java.util.Properties;
import java.util.concurrent.Semaphore;

public class ProdConsBuffer implements IProdConsBuffer {

	Properties properties;
	Message[] buffer;
	int tete;
	int queue;
	int cpt;
	Semaphore acce;

	public ProdConsBuffer(Properties properties) {
		this.properties = properties;
		buffer = new Message[Integer.parseInt(properties.getProperty("bufSz"))];
		tete = -1;
		queue = 0;
		cpt = 0;
		acce = new Semaphore(1);
	}

	@Override
	public void put(Message m) throws InterruptedException {
		acce.acquire();

		while (estpleine()) {
			acce.release();
			acce.acquire();
		}

		try {
			if (estvide())
				tete = queue;
			else
				queue = (queue + 1) % buffer.length;

			buffer[queue] = m;
			cpt++;
		} finally {
			acce.release();
		}
	}

	@Override
	public Message get() throws InterruptedException {
		acce.acquire();

		while (estvide()) {
			acce.release();
			acce.acquire();
		}
		Message msg = new Message(0, "Error");
		try {
			msg = buffer[tete];
			if (tete == queue)
				tete = -1;
			else
				tete = (tete + 1) % buffer.length;
		} finally {
			acce.release();
		}
		return msg;
	}

	@Override
	public int nmsg() {
		if (estvide())
			return 0;
		else {
			if (tete <= queue)
				return queue - tete + 1;
			else
				return buffer.length - (tete - queue);
		}
	}

	@Override
	public int totmsg() {
		return cpt;
	}

	private boolean estpleine() {
		return tete == (queue + 1) % buffer.length;
	}

	private boolean estvide() {
		return tete == -1;
	}

	@Override
	public Message[] get(int k) throws InterruptedException {
//		if (k <= 0) {
//			Message[] tmp = new Message[1];
//			tmp[0] = new Message(0, "k negatif");
//			return tmp;
//		}
		acce.acquire();
		
//		System.out.println(nmsg());

		while (nmsg() < k) {
			acce.release();
			acce.acquire();
		}
		
		System.out.println(nmsg());

		Message[] tmp = new Message[k];
		try {
			for (int i = 0; i < k; i++) {
				tmp[i] = buffer[tete];
				buffer[tete] = null;
				if (tete == queue)
					tete = -1;
				else
					tete = (tete + 1) % buffer.length;
			}
		} finally {
			acce.release();
		}
		return tmp;
	}
}
