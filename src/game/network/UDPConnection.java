package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	private int latestPacketID = 0;
	private int latestRemotePacketID = 0;

	private int latestMessageID = 0;

	private Thread messageSender;
	private ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();
	private BitSet recievedMessages = new BitSet(NUM_ACKS);
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
			ByteBuffer prevMessage = ByteBuffer.allocate(0);
			while (isConnected()) {
				ByteBuffer message = ByteBuffer.allocate(PACKET_SIZE);
				message.putInt(latestPacketID++);
				while (message.remaining() - 4 > 0) {
					message.putInt(latestMessageID);
					if (prevMessage.hasRemaining()) {
						int size = Math.min(message.remaining(),
								prevMessage.remaining()) - 4;
						message.putInt(size);
						byte[] data = new byte[size];
						prevMessage.get(data);
						message.put(data);

						prevMessage.compact();
						prevMessage.flip();
					} else {
						Message nextMessage = messageQueue.poll();
						if (nextMessage == null) {
							break;
						}
						prevMessage = nextMessage.buffer;
					}
				}
				DatagramPacket packet = new DatagramPacket(message.array(),
						message.arrayOffset(), message.capacity(), addr);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
