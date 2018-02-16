package game.network;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private ServerSocket tcpSocket;
	private ArrayList<Socket> clientSockets = new ArrayList<>();
	private ArrayList<String> messages;
	private DatagramSocket socket;
	private Thread broadcastingThread;
	private Thread clientAccepter;

	public Server() {
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			tcpSocket = new ServerSocket(Protocol.DEFAULT_TCP_PORT);

			broadcastingThread = new BroadcastThread();
			broadcastingThread.start();
			clientAccepter = new ClientAccepterThread();
			clientAccepter.start();
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
		for (Socket s : clientSockets) {
			try {
				InputStreamReader reader;
				reader = new InputStreamReader(s.getInputStream());
				String output = "";
				for (int c = reader.read(); c != -1; c = reader.read()) {
					output += (char) c;
				}
				messages.add(output);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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

	private class ClientAccepterThread extends Thread {
		@Override
		public void run() {
			while (!tcpSocket.isClosed()) {
				try {
					Socket sock = tcpSocket.accept();
					clientSockets.add(sock);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
