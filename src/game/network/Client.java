package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Client {
	private DatagramSocket socket;
	private Set<InetAddress> knownHosts = new HashSet<>();
	private Thread listenerThread;

	public Client() {
		try {
			socket = new DatagramSocket(Protocol.DEFAULT_PORT);
			socket.setBroadcast(true);
			listenerThread = new HostAccepterThread();
			listenerThread.start();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> getHosts() {
		ArrayList<String> hostnames = new ArrayList<>();
		for (InetAddress addr : knownHosts) {
			hostnames.add(addr.getHostName());
		}
		return hostnames;
	}

	private class HostAccepterThread extends Thread {
		@Override
		public void run() {
			while (!socket.isClosed()) {
				byte[] buffer = new byte[8];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(packet);
					if (Protocol.parseMessage(packet) == MessageType.HEARTBEAT) {
						knownHosts.add(packet.getAddress());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
