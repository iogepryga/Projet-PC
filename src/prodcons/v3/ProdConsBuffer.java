package prodcons.v3;

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
			System.out.println("+++P" + m.from + " place \"" + m.msg + "\". (" + (nmsg()-1) + "->" + nmsg() + ")");
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
			System.out.println("------------------------------------------M \"" + msg.msg + "\" de " + msg.from + " retiré. (" + (nmsg()+1) + "->" + nmsg() + ")");
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
}
