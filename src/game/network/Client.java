package game.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.newdawn.slick.geom.Rectangle;

import game.network.event.Event;
import game.network.io.EncodedOutputStream;
import game.network.io.EncodedReader;
import game.network.io.Header;
import game.network.io.Util;
import game.world.World;

public class Client {
	public static final int TCP_PORT = 8837;
	private Socket s;
	private EncodedOutputStream out;
	private EncodedReader in;

	public Client(InetAddress inetAddress) throws IOException {
		s = new Socket("", TCP_PORT);
		out = new EncodedOutputStream(s.getOutputStream());
		in = new EncodedReader(s.getInputStream());

		new Thread(in).start();
	}

	public void sendEvent(Event e) {
		out.write(Header.EVENT, Event.toBytes(e));
	}

	public void requestBlocks(Rectangle rect) {
		out.write(Header.WORLD, Util.toBytes((int) rect.getX()),
				Util.toBytes((int) rect.getY()), Util.toBytes((int) rect.getWidth()),
				Util.toBytes((int) rect.getHeight()));
	}

	public void bindTo(World w) {
		in.clearListeners();

		in.addListener((header, data) -> {
			switch (header) {
			case EVENT:
				try {
					w.addEvent(Event.fromBytes(data));
				} catch (ReflectiveOperationException e) {
					System.err.println("Malformed event packet");
				}
				break;
			case WORLD:
				w.recieveNewBlocks(data);
				break;
			}
		});
	}
}
