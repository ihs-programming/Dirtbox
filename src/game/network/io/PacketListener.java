package game.network.io;

/**
 * Created by Robert on 8/6/2017.
 */
public interface PacketListener {
	void packet(Header header, byte[] data);
}
