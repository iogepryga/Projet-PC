package prodcons.v5;

public class Message {
	String msg;
	int from;

	public Message(int from, String msg) {
		this.msg = msg;
		this.from = from;
	}

	public String toString() {
		return "\"" + msg + "\" de " + from + ".";
	}
}
