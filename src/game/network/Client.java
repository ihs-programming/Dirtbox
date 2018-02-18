package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Low level interface that maintains packet reliability, network discovery, and
 * not much more
 */
public class Client {
	private static final float TIMEOUT = 5f;

	private DatagramSocket socket;
	private Map<InetAddress, HostInformation> knownHosts = new HashMap<>();
	private Thread listenerThread;
	private ArrayList<String> hostMessages = new ArrayList<>();
	private Optional<InetAddress> hostAddr;

	public Client() {
		try {
			socket = new DatagramSocket(Protocol.DEFAULT_DISCOVERY_PORT);
			socket.setBroadcast(true);
			listenerThread = new RecieverThread();
			listenerThread.start();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public Map<InetAddress, String> getHostInfo() {
		Map<InetAddress, String> hostmap = new HashMap<>();
		ArrayList<InetAddress> toRemove = new ArrayList<>();
		for (Map.Entry<InetAddress, HostInformation> host : knownHosts.entrySet()) {
			String hostname = host.getKey().getHostName();
			String hostinfo = host.getValue().toString();
			if (host.getValue().timeSinceLastUpdate() > TIMEOUT) {
				toRemove.add(host.getKey());
			} else {
				String info = String.format("%s: %s\n", hostname, hostinfo);
				hostmap.put(host.getKey(), info);
			}
		}
		for (InetAddress addr : toRemove) {
			knownHosts.remove(addr);
		}
		return hostmap;
	}

	/**
	 * Establishes tcp connection with server
	 *
	 * @param addr
	 */
	public void connect(InetAddress addr) throws IOException {
		hostAddr = Optional.of(InetAddress.getByAddress(addr.getAddress()));
	}

	public void disconnect() {
		hostAddr = null;
	}

	public List<String> getMessages() {
		return hostMessages;
	}

	public void send(String message) throws IOException {
		if (!hostAddr.isPresent()) {
			throw new IOException("Not connected to host");
		}
		InetAddress addr = hostAddr.get();
		DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length());
		packet.setPort(Protocol.DEFAULT_DISCOVERY_PORT);
	}

	/**
	 * Receives and parses incoming messages
	 */
	private class RecieverThread extends Thread {
		@Override
		public void run() {
			byte[] buffer = new byte[8];
			while (!socket.isClosed()) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(packet);
					if (Protocol.parseMessage(packet) == MessageType.HEARTBEAT) {
						HostInformation info = knownHosts
								.getOrDefault(packet.getAddress(), new HostInformation());
						info.update();
						knownHosts.put(packet.getAddress(), info);
					} else if (hostAddr.isPresent()
							&& hostAddr.get().equals(packet.getAddress())) {
						hostMessages.add(new String(packet.getData(), packet.getOffset(),
								packet.getLength()));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
