package game.network;

import java.net.Socket;

/**
 * Created by Robert on 8/6/2017.
 */
interface SocketListener {
	boolean addSocket(Socket s);

	Runnable getHandler(Socket s);
}
