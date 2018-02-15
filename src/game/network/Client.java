package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class Client {
	private DatagramSocket socket;
	private Map<InetAddress, HostInformation> knownHosts = new HashMap<>();
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

	public Map<InetAddress, String> getHostInfo() {
		Map<InetAddress, String> hostmap = new HashMap<>();
		for (Map.Entry<InetAddress, HostInformation> host : knownHosts.entrySet()) {
			String hostname = host.getKey().getHostName();
			String hostinfo = host.getValue().toString();
			String info = String.format("%s: %s\n", hostname, hostinfo);
			hostmap.put(host.getKey(), info);
		}
		return hostmap;
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
						HostInformation info = knownHosts
								.getOrDefault(packet.getAddress(), new HostInformation());
						info.update();
						knownHosts.put(packet.getAddress(), info);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
