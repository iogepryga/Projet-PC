package prodcons.v6;

public class Message {
	String msg;
	int from;
	boolean multi;
	int n;

	public Message(int from, String msg) {
		this.msg = msg;
		this.from = from;
		multi = false;
		n = 1;
	}

	public String toString() {
		return "De " + from + " : " + msg;
	}
	
	public void setMulti(boolean multi) {
		this.multi = multi;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public boolean isMulti() {
		return multi;
	}
}
