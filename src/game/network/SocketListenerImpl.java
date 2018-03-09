package game.network;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;

import game.network.io.EncodedOutputStream;
import game.network.io.EncodedReader;
import game.network.io.Header;

public class SocketListenerImpl implements SocketListener {
	private class User {
		public final EncodedOutputStream out;
		public final EncodedReader in;
		public final Socket s;

		public User(Socket s) throws IOException {
			this.s = s;
			this.in = new EncodedReader(s.getInputStream());
			new Thread(in).start();
			out = new EncodedOutputStream(s.getOutputStream());
		}

		@Override
		public int hashCode() {
			return s.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return s.equals(o);
		}
	}

	private HashSet<User> users = new HashSet<>();

	@Override
	public boolean addSocket(Socket s) {
		try {
			User u = new User(s);

			u.in.addListener((header, data) -> {
				switch (header) {
				case EVENT:
					sendAll(Header.EVENT, data);
					break;
				}
			});

			users.add(u);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to add user");
		}
		return false;
	}

	public void sendAll(Header h, byte[]... data) {
		for (User u : users) {
			u.out.write(h, data);
			System.out.println(h);
		}
	}

	@Override
	public Runnable getHandler(Socket s) {
		return () -> this.addSocket(s);
	}

}
