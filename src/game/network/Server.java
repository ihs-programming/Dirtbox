package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Server {
	private DatagramSocket socket;
	private Thread broadcastingThread;

	public Server() {
		try {
			socket = new DatagramSocket(Protocol.DEFAULT_PORT);
			socket.setBroadcast(true);
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
							pkt.setPort(Protocol.DEFAULT_PORT);
							socket.send(pkt);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
