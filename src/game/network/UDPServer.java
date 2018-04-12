package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer implements Runnable {
	static {
		DatagramSocket sock = null;
		try {
			sock = new DatagramSocket(
					UDPBroadcast.DEFAULT_DISCOVERY_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
			// Probably should exit here
		}
		socket = sock;
	}
	// Reasoning for this is because we can only create one datagram socket
	static final DatagramSocket socket;

	public UDPServer() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			while (true) {
				DatagramPacket ping = new DatagramPacket(
						new byte[UDPBroadcast.PING_PACKET_SIZE],
						UDPBroadcast.PING_PACKET_SIZE);
				socket.receive(ping);
				if (UDPBroadcast.validPacket(ping)) {
					socket.send(ping);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
