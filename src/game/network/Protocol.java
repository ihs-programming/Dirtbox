package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Several helper functions dealing with the network protocol that the game uses
 * to communicate with other clients
 */
public class Protocol {
	private final static int HEADER_SIZE = 8;

	public final static int DEFAULT_DISCOVERY_PORT = 8838;
	public final static int DEFAULT_TCP_PORT = 39204;

	/**
	 * Checks if the given packet is
	 *
	 * @param p
	 * @return
	 */
	public static MessageType parseMessage(DatagramPacket p) {
		if (p.getLength() < HEADER_SIZE) {
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

	public static void broadcast(DatagramSocket socket, DatagramPacket packet)
			throws IOException {
		packet.setAddress(InetAddress.getByName("255.255.255.255"));
		packet.setPort(Protocol.DEFAULT_DISCOVERY_PORT);
		socket.send(packet);
		Enumeration<NetworkInterface> interfaces = NetworkInterface
				.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
			if (networkInterface.isLoopback() || !networkInterface.isUp()) {
				continue;
			}
			for (InterfaceAddress interfaceAddr : networkInterface
					.getInterfaceAddresses()) {
				InetAddress broadcastAddr = interfaceAddr.getBroadcast();
				if (broadcastAddr == null) {
					continue;
				}

				DatagramPacket pkt = Protocol
						.createMessage(MessageType.HEARTBEAT);
				pkt.setAddress(broadcastAddr);
				pkt.setPort(Protocol.DEFAULT_DISCOVERY_PORT);
				socket.send(pkt);
			}
		}
	}
}