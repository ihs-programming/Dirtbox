package game.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Manages the connection with the remote address. Allows for reliable udp
 * packets, and prevents packet congestion
 */
public class UDPConnection {
	private final int MESSAGE_LENGTH_SIZE = 3;

	private DatagramSocket socket;
	private InetSocketAddress addr;
	private int latestPacketID = 0;
	private int latestRemotePacketID = 0;

	private Thread messageSender;
	private ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();

	private static class Message {
		public ByteBuffer buffer;
		public boolean reliable;

		public Message(ByteBuffer buffer, boolean reliable) {
			this.buffer = buffer;
			this.reliable = reliable;
		}
	}

	public UDPConnection(DatagramSocket socket, InetSocketAddress dest) {
		this.socket = socket;
		addr = dest;
		messageSender = new MessageSendingThread();
		messageSender.start();
	}

	public void sendMessage(byte[] message) {
		messageQueue.add(new Message(ByteBuffer.wrap(message), false));
	}

	public void sendReliableMessage(byte[] message) {
		messageQueue.add(new Message(ByteBuffer.wrap(message), true));
	}

	public void recieveMessage(byte[] message) {

	}

	public void parseMessage(DatagramPacket packet) {

	}

	public boolean isConnected() {
		return !socket.isClosed();
	}

	private class MessageSendingThread extends Thread {
		@Override
		public void run() {
			while (isConnected()) {

			}
		}
	}
}
