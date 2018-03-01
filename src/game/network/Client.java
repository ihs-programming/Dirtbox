package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Low level interface that maintains packet reliability and network discovery
 */
public class Client {
	private DatagramSocket socket;
	private Map<InetSocketAddress, HostInformation> knownHosts = new HashMap<>();
	private Thread listenerThread;
	private ArrayList<String> hostMessages = new ArrayList<>();
	private Optional<UDPConnection> connection = Optional.empty();

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

	public Map<InetSocketAddress, String> getHostInfo() {
		Map<InetSocketAddress, String> hostmap = new HashMap<>();
		ArrayList<InetSocketAddress> toRemove = new ArrayList<>();
		for (Map.Entry<InetSocketAddress, HostInformation> host : knownHosts.entrySet()) {
			String hostname = host.getKey().getHostName();
			String hostinfo = host.getValue().toString();
			if (host.getValue().timeSinceLastUpdate() > Protocol.TIMEOUT) {
				toRemove.add(host.getKey());
			} else {
				String info = String.format("%s: %s\n", hostname, hostinfo);
				hostmap.put(host.getKey(), info);
			}
		}
		for (InetSocketAddress addr : toRemove) {
			knownHosts.remove(addr);
		}
		return hostmap;
	}

	public Optional<InetSocketAddress> getCurrentHost() {
		if (connection.isPresent()) {
			return Optional.of(connection.get().addr);
		}
		return Optional.empty();
	}

	/**
	 * Establishes tcp connection with server
	 *
	 * @param addr
	 */
	public void connect(InetSocketAddress addr) throws IOException {
		connection = Optional.of(new UDPConnection(socket, addr));
	}

	public void disconnect() {
		if (connection.isPresent()) {
			connection.get().disconnect();
		}
		connection = Optional.empty();
	}

	public List<String> getMessages() {
		return hostMessages;
	}

	public void send(String message) throws IOException {
		if (!connection.isPresent()) {
			throw new IOException("Not connected to host");
		}
		UDPConnection con = connection.get();
		con.sendMessage(message.getBytes());
	}

	private void parsePacket(DatagramPacket packet) {
		if (Protocol.parseMessage(packet) == MessageType.DISCOVERY) {
			InetSocketAddress addr = (InetSocketAddress) packet
					.getSocketAddress();
			HostInformation info = knownHosts
					.getOrDefault(addr, new HostInformation());
			info.update();
			knownHosts.put(addr, info);
		} else if (connection.isPresent()
				&& connection.get().addr.equals(packet.getSocketAddress())) {
			hostMessages.add(new String(packet.getData(), packet.getOffset(),
					packet.getLength()));
			connection.get().parseMessage(packet);
		}
	}

	/**
	 * Receives and parses incoming messages
	 */
	private class RecieverThread extends Thread {
		@Override
		public void run() {
			byte[] buffer = new byte[Protocol.MAX_PACKET_SIZE];
			while (!socket.isClosed()) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(packet);
					parsePacket(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
