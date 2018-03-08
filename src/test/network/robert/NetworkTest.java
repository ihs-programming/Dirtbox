package test.network.robert;

import game.network.UDPBroadcast;
import game.network.UDPServer;

public class NetworkTest {
	public static void main(String args[]) throws Exception {
		UDPServer s = new UDPServer();
		UDPBroadcast cast = new UDPBroadcast(1000, 2000);

		Thread.sleep(2000);

		System.out.println(cast.getActiveAddresses());

	}
}
