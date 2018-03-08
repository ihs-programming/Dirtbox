package game.network;

import java.net.Socket;

public class SocketListenerImpl implements SocketListener {

	@Override
	public boolean addSocket(Socket s) {
		return false;
	}

	@Override
	public Runnable getHandler(Socket s) {
		return () -> this.addSocket(s);
	}

}
