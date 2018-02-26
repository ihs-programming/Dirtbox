package game.network;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class HostInformation {
	private LocalDateTime lastUpdated;

	public HostInformation() {
		lastUpdated = LocalDateTime.now();
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * Time in seconds since host sent a heartbeat
	 *
	 * @return
	 */
	public float timeSinceLastUpdate() {
		return ChronoUnit.SECONDS.between(lastUpdated, LocalDateTime.now());
	}

	/**
	 * Signal that heartbeat was recieved from host
	 */
	public void update() {
		lastUpdated = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return String.format("Last updated: %s", lastUpdated.toString());
	}
}