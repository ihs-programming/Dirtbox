package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UDPBroadcast {
	public static final int DEFAULT_DISCOVERY_PORT = 8838;
	public static final byte PING_VALUE = 69;
	public static final int PING_PACKET_SIZE = 3;

	private boolean running = true;

	private DatagramSocket socket;
	private Set<InetAddress> buffer;

	/**
	 * Dunno how good this is tbh. It kinda wastes a Thread.
	 *
	 * @param freq
	 *            Frequency in ms to broadcast
	 * @param delay
	 *            Time before giving up
	 * @throws SocketException
	 */
	public UDPBroadcast(int freq, int delay) throws IOException {
		buffer = new HashSet<>();

		InetAddress broadcast = InetAddress.getByName("255.255.255.255");
		socket = UDPServer.socket;
		byte[] pingPacket = new byte[PING_PACKET_SIZE];
		Arrays.fill(pingPacket, PING_VALUE);
		DatagramPacket search = new DatagramPacket(
				pingPacket, PING_PACKET_SIZE,
				broadcast, DEFAULT_DISCOVERY_PORT);

		Runnable broadcaster = () -> {
			try {
				for (int i = 0; i < delay; i += freq) {
					socket.send(search);
					Thread.sleep(freq);
				}
			} catch (InterruptedException | IOException ie) {
				ie.printStackTrace();
			}
		};
		Runnable reciever = () -> {
			while (running) {
				try {
					DatagramPacket ping = new DatagramPacket(new byte[PING_PACKET_SIZE],
							PING_PACKET_SIZE);
					socket.receive(ping);
					if (validPacket(ping)) {
						buffer.add(ping.getAddress());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(broadcaster).start();
		new Thread(reciever).start();

	}

	public void cleanup() {
		socket.close();
		running = false;
	}

	public static boolean validPacket(DatagramPacket ping) {
		if (ping.getLength() != PING_PACKET_SIZE) {
			return false;
		}
		boolean packetGood = true;
		for (int i = 0; i < PING_PACKET_SIZE; i++) {
			if (ping.getData()[i] != PING_VALUE) {
				packetGood = false;
			}
		}
		return packetGood;
	}

	public Set<InetAddress> getActiveAddresses() {
		return buffer;
	}
}
