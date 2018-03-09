package game.network;

import static game.network.io.Util.toInt;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;

import org.newdawn.slick.geom.Rectangle;

import game.network.gamestate.BlockState;
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
	private BlockState blockStates = new BlockState();

	@Override
	public boolean addSocket(Socket s) {
		try {
			User u = new User(s);

			System.out.println("HI");
			byte[] bd = blockStates
					.getBlocks(new Rectangle(-1000, -1000, 2000, 2000));
			System.out.println("HI");
			u.out.write(Header.WORLD, bd);
			System.out.println("HI");

			u.in.addListener((header, data) -> {
				switch (header) {
				case EVENT:
					sendAll(Header.EVENT, data);
					break;
				case WORLD:
					Rectangle rect = new Rectangle(toInt(data, 0), toInt(data, 4),
							toInt(data, 8), toInt(data, 12));
					byte[] blockData = blockStates.getBlocks(rect);
					u.out.write(Header.WORLD, blockData);
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
