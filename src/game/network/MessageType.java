package game.network;

public enum MessageType {
	// @formatter:off
	DISCOVERY(0),
	/**
	 * Uses "ack" method from:
	 * https://gafferongames.com/post/reliability_ordering_and_congestion_avoidance_over_udp/
	 */
	SMALL_RELIABLE(1),
	SMALL(2),
	UNKNOWN(-1);
	// @formatter:on

	private int id;

	private MessageType(int id) {
		this.id = id;
	}

	public static MessageType getType(byte header) {
		for (MessageType m : MessageType.values()) {
			if (m.id == header) {
				return m;
			}
		}
		return UNKNOWN;
	}

	public byte getHeader() {
		assert this != UNKNOWN;
		return (byte) id;
	}
}
