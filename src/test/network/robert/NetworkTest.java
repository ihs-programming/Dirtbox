package test.network.robert;

import game.network.robert.Server;
import game.network.robert.UDPBroadcast;

public class NetworkTest {
	public static void main(String args[]) throws Exception {
		Server s = new Server();
		UDPBroadcast cast = new UDPBroadcast(1000, 2000);

		Thread.sleep(2000);

		System.out.println(cast.getActiveAddresses());

	}
}
