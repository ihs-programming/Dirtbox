package game.network;

import java.net.DatagramPacket;

/**
 * Several helper functions dealing with the network protocol that the game uses
 * to communicate with other clients
 */
public class Protocol {
	private final static int HEADER_SIZE = 8;
	public final static int DEFAULT_PORT = 8838;

	/**
	 * Checks if the given packet is
	 *
	 * @param p
	 * @return
	 */
	public static MessageType parseMessage(DatagramPacket p) {
		if (p.getLength() < HEADER_SIZE || p.getPort() != DEFAULT_PORT) {
			// packet must at least contain HEADER_SIZE bytes
			return MessageType.UNKNOWN;
		}
		return MessageType.HEARTBEAT;
	}

	public static DatagramPacket createMessage(MessageType type) {
		byte[] buffer;
		switch (type) {
		case HEARTBEAT:
			buffer = new byte[HEADER_SIZE];
			return new DatagramPacket(buffer, buffer.length);
		case UNKNOWN:
			buffer = new byte[1];
			return new DatagramPacket(buffer, 1);
		}
		return null;
	}
}