package game.network.robert;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	public static final int TCP_PORT = 8837;
	private Socket s;

	public Client(InetAddress addr) throws IOException {
		s = new Socket(addr, TCP_PORT);
	}
}
