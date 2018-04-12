package game.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Robert on 8/6/2017.
 */
public class ServerThread implements Runnable {
	private SocketListener sl;

	public ServerThread(SocketListener sl) {
		this.sl = sl;
	}

	@Override
	public void run() {
		ServerSocket ss;
		try {
			ss = new ServerSocket(Client.TCP_PORT);
			while (true) {
				try {
					Socket s = ss.accept();

					Runnable sh = sl.getHandler(s);

					new Thread(sh).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}