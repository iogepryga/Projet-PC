package prodcons.v6;

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
			if (msg.isMulti() && msg.getN() > 0)
				msg.setN(msg.getN() - 1);
			while (msg.isMulti() && msg.getN() > 0) {
				acce.release();
				acce.acquire();
			}
			if (!msg.isMulti() || msg.getN() == 0) {
				msg.setN(-1);
				if (tete == queue)
					tete = -1;
				else
					tete = (tete + 1) % buffer.length;
			}
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
			int somme = 0;
//			if (tete <= queue)
//				for(int i = tete; i <= queue ; i++)
//					somme += buffer[i].getN();
//			else {
			for (int i = tete; i <= queue; i = (i + 1) % buffer.length)
				somme += buffer[i].getN();
			return somme;
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
				if (tmp[i].isMulti() && tmp[i].getN() > 0)
					tmp[i].setN(tmp[i].getN() - 1);

				while (tmp[i].isMulti() && tmp[i].getN() > 0 && i + 1 == k) {
					acce.release();
					acce.acquire();
				}

				if (!tmp[i].isMulti() || tmp[i].getN() == 0) {
					tmp[i].setN(-1);
					if (tete == queue)
						tete = -1;
					else
						tete = (tete + 1) % buffer.length;
				}
			}
		} finally {
			acce.release();
		}
		return tmp;
	}

	@Override
	public void put(Message m, int n) throws InterruptedException {
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

			m.setMulti(true);
			m.setN(n);
			buffer[queue] = m;
			cpt++;
			while (m.getN() > 0) {
				acce.release();
				acce.acquire();
			}

		} finally {
			acce.release();
		}
	}
}
