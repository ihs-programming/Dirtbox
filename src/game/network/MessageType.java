package game.network;

public enum MessageType {
	// @formatter:off
	HEARTBEAT(0),

	/**
	 * Uses "ack" method from:
	 * https://gafferongames.com/post/reliability_ordering_and_congestion_avoidance_over_udp/
	 */
	SMALL_RELIABLE(3),
	UNKNOWN(-1);
	// @formatter:on

	private int id;

	private MessageType(int id) {
		this.id = id;
	}
}
