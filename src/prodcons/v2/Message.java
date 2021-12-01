package prodcons.v2;

public class Message {
	String msg;
	int from;

	public Message(int from, String msg) {
		this.msg = msg;
		this.from = from;
	}

	public String toString() {
		return "De " + from + " : " + msg;
	}
}
