package game.network;

public class Message {
	private MessageType type;
	private byte[] content;

	public Message(MessageType type, byte[] information) {
		this.type = type;
		content = information;
	}
}
