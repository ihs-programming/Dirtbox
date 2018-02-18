package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class Server {
	private ArrayList<String> messages;
	private DatagramSocket socket;
	private Thread broadcastingThread;
	private Thread recievingThread;

	public Server() {
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);

			broadcastingThread = new BroadcastThread();
			broadcastingThread.start();

			recievingThread = new ReceiverThread();
			recievingThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns true if the server is currently running (discoverable)
	 *
	 * @return
	 */
	public boolean isRunning() {
		return !socket.isClosed();
	}

	/**
	 * Stops the server
	 */
	public void stop() {
		socket.close();
	}

	public void updateMessages() {
	}

	public ArrayList<String> getMessages() {
		return messages;
	}

	private class BroadcastThread extends Thread {
		@Override
		public void run() {
			while (!socket.isClosed()) {
				DatagramPacket packet = Protocol.createMessage(MessageType.HEARTBEAT);
				try {
					Protocol.broadcast(socket, packet);
				} catch (IOException e) {
					System.out.println("Unable to send message");
				}
			}
		}
	}

	private class ReceiverThread extends Thread {
		@Override
		public void run() {
			byte[] buffer = new byte[Protocol.MAX_PACKET_SIZE];
			while (!socket.isClosed()) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(packet);
					Protocol.broadcast(socket, packet);
				} catch (IOException e) {

				}
			}
		}
	}
}
