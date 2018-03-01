package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Manages the connection with the remote address. Allows for reliable udp
 * packets, and prevents packet congestion
 */
public class UDPConnection {
	private final static int PACKET_SIZE = 1024;
	/**
	 * Number of udp packets to recieve until it's assumed that a packet is lost
	 */
	private final static int NUM_ACKS = 32;

	private DatagramSocket socket;
	public final InetSocketAddress addr;
	private Thread messageSender;
	private BlockingQueue<Message> messageQueue = new LinkedBlockingDeque<>();
	private boolean connected = true;

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
		return connected;
	}

	public void disconnect() {
		connected = false;
	}

	private class MessageSendingThread extends Thread {
		/** @formatter:off
		 * Packet data format:
		 * [packet id]
		 * [bitfield of previous recieved packets]
		 * [message id]
		 * [message length]
		 * etc... (more messages)
		 * @formatter:on
		 */
		@Override
		public void run() {
			while (isConnected()) {
				try {
					Message m = messageQueue.take();
					DatagramPacket packet = new DatagramPacket(m.buffer.array(),
							m.buffer.capacity());
					packet.setSocketAddress(addr);
					socket.send(packet);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
