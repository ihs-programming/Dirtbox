package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Server {
	private MulticastSocket socket;
	private Thread broadcastingThread;

	public Server() {
		try {
			socket = new MulticastSocket(Protocol.DEFAULT_PORT);
			broadcastingThread = new BroadcastThread();
			broadcastingThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class BroadcastThread extends Thread {
		@Override
		public void run() {
			while (!socket.isClosed()) {
				try {
					DatagramPacket packet = Protocol.createMessage(MessageType.HEARTBEAT);
					packet.setAddress(InetAddress.getByName("255.255.255.255"));
					packet.setPort(Protocol.DEFAULT_PORT);
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
