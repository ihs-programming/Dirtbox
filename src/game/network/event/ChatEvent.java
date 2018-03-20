package game.network.event;

public class ChatEvent extends Event {
	private String text;

	public ChatEvent(byte[] b) {
		text = new String(b);
	}

	public ChatEvent(String text) {
		this.text = text;
	}

	@Override
	public byte[] toBytes() {
		return text.getBytes();
	}

}
