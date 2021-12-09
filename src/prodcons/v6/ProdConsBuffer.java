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
//		System.out.println("check acquire put 1 " + m.from);
		acce.acquire();
//		System.out.println("check acquire put 1bis " + m.from);

		while (estpleine()) {
//			System.out.println("check release put 1 " + m.from);
			acce.release();
//			System.out.println("check acquire put 2 " + m.from);
			acce.acquire();
//			System.out.println("check acquire put 2bis " + m.from);
		}

		try {
			if (estvide())
				tete = queue;
			else
				queue = (queue + 1) % buffer.length;

			buffer[queue] = m;
			cpt++;
			System.out.println("+++P" + m.from + " place \"" + m.msg + "\". (" + (nmsg() - 1) + "->" + nmsg() + ")");
		} finally {
//			System.out.println("check release put 2 " + m.from);
			acce.release();
		}
	}

	@Override
	public Message get() throws InterruptedException {
//		System.out.println("check acquire get 1");
		acce.acquire();
//		System.out.println("check acquire get 1bis");
		while (estvide()) {
//			System.out.println("check release get 1");
			acce.release();
//			System.out.println("check acquire get 2");
			acce.acquire();
//			System.out.println("check acquire get 2bis");
		}
		Message msg = new Message(0, "Error");
		try {
			msg = buffer[tete];
			if (msg.isMulti() && msg.getN() > 0)
				msg.setN(msg.getN() - 1);
			while (msg.isMulti() && msg.getN() > 0) {
//				System.out.println("check release get 2");
				acce.release();
//				System.out.println("check acquire get 3");
				acce.acquire();
//				System.out.println("check acquire get 3bis");
			}
			if (!msg.isMulti() || msg.getN() == 0) {
				msg.setN(-1);
				if (tete == queue)
					tete = -1;
				else
					tete = (tete + 1) % buffer.length;
				System.out.println("------------------------------------------M \"" + msg.msg + "\" de " + msg.from
						+ " retiré. (" + (nmsg() + 1) + "->" + nmsg() + ")");
			}
		} finally {
//			System.out.println("check release get 3");
			acce.release();
		}
		return msg;
	}

	@Override
	public int nmsg() {
		int somme = 0;
		if (estvide())
			return 0;
//		if (estpleine()) {
//			for(int i = 0 ; i < buffer.length;i++)
//				somme += buffer[i].getN();
//		} 
		else {
//			int nbmsg;
//			if (tete <= queue)
//				nbmsg = queue - tete + 1;
//			else
//				nbmsg = buffer.length - (tete - queue);
//			for (int i = 0; i < nbmsg; i++)
//				somme += buffer[(tete + i) % buffer.length].getN();
			for (int i = tete; i != (queue + 1) % buffer.length; i = (i + 1) % buffer.length)
				somme += buffer[i].getN();
		}
		return somme;
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
//		System.out.println("check acquire getk 1");
		acce.acquire();
//		System.out.println("check acquire getk 1bis");

//		System.out.println(nmsg());
//		System.out.println("test");
		while (nmsg() < k) {
//			System.out.println("check release getk 1");
			acce.release();
//			System.out.println("check acquire getk 2");
			acce.acquire();
//			System.out.println("check acquire getk 2bis");
		}

//		System.out.println(nmsg());
//		System.out.println("test 2");
		Message[] tmp = new Message[k];
		try {
//			System.out.println("test 3");
			for (int i = 0; i < k; i++) {
//				System.out.println("test 4");
				tmp[i] = buffer[tete];
//				System.out.println("test 5");
				if (tmp[i].isMulti() && tmp[i].getN() > 0)
//					System.out.println("test 6");
					tmp[i].setN(tmp[i].getN() - 1);
//				System.out.println("test 7");
				while (tmp[i].isMulti() && tmp[i].getN() > 0 && i + 1 == k) {
//					System.out.println("check release getk 2");
					acce.release();
//					System.out.println("check acquire getk 3 ");
					acce.acquire();
//					System.out.println("check acquire getk 3bis ");
				}
//				System.out.println("test 8");
				if (!tmp[i].isMulti() || tmp[i].getN() == 0) {
//					System.out.println("test 9");
					tmp[i].setN(-1);
//					System.out.println("test 10");
					if (tete == queue)
						tete = -1;
					else
						tete = (tete + 1) % buffer.length;
//					System.out.println("test 11");
					System.out.println("------------------------------------------M \"" + tmp[i].msg + "\" de "
							+ tmp[i].from + " retiré. (" + (nmsg() + 1) + "->" + nmsg() + ")");
				}
			}
//			System.out.println("test 12");
		} finally {
//			System.out.println("check release getk 3");
			acce.release();
		}
//		System.out.println("test 13");
		return tmp;
	}

	@Override
	public void put(Message m, int n) throws InterruptedException {
//		System.out.println("check acquire putk 1 " + m.from);
		acce.acquire();
//		System.out.println("check acquire putk 1bis " + m.from);
		while (estpleine()) {
//			System.out.println("check release putk 1 " + m.from);
			acce.release();
//			System.out.println("check acquire putk 2 " + m.from);
			acce.acquire();
//			System.out.println("check acquire putk 2bis " + m.from);
		}
//		System.out.println("test " + m.from);
		try {
//			System.out.println("test2 " + m.from);
			if (estvide())
				tete = queue;
			else
				queue = (queue + 1) % buffer.length;
//			System.out.println("test3 " + m.from);
			m.setMulti(true);
//			System.out.println("test4 " + m.from);
			m.setN(n);
//			System.out.println("test5 " + m.from);
			buffer[queue] = m;
//			System.out.println("test6 " + m.from);
			cpt++;
//			System.out.println("test7 " + m.from);
			System.out.println("+++P" + m.from + " place " + m.msg + " en " + n + " exemplaires. (" + (nmsg() - n)
					+ "->" + nmsg() + ")");
//			System.out.println("test8 " + m.from);
			while (m.getN() > 0) {
//				System.out.println("check release putk 2 " + m.from);
				acce.release();
//				System.out.println("check acquire putk 3 " + m.from);
				acce.acquire();
//				System.out.println("check acquire putk 3bis " + m.from);
			}
		} finally {
//			System.out.println("check release putk 3 " + m.from);
			acce.release();
		}
	}
}
