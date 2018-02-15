package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {
	private DatagramSocket socket;
	private Thread broadcastingThread;

	public Server() {
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			broadcastingThread = new BroadcastThread();
			broadcastingThread.start();
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

	private class BroadcastThread extends Thread {
		@Override
		public void run() {
			while (!socket.isClosed()) {
				DatagramPacket packet = Protocol.createMessage(MessageType.HEARTBEAT);
				Protocol.broadcast(socket, packet);
			}
		}
	}
}
