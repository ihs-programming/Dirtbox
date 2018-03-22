package game.network.event;

import java.awt.Point;

import game.network.io.Util;

public class BlockBreakEvent extends Event {
	private int x;
	private int y;

	public BlockBreakEvent(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public BlockBreakEvent(byte[] b) {
		x = Util.toInt(b, 0);
		y = Util.toInt(b, 4);
	}

	@Override
	public byte[] toBytes() {
		return Util.combine(Util.toBytes(x), Util.toBytes(y));
	}

	public Point getPos() {
		return new Point(x, y);
	}
}
