package game.save;

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.TreeMap;

import game.blocks.Block;
import game.blocks.BlockType;
import game.network.gamestate.BlockState;
import game.network.io.Util;
import game.world.World;

public class Saver {
	/**
	 * We store these blocks in 20 byte chunks. [xpos] [ypos] [type]
	 *
	 * @param w
	 */
	public static byte[] save(World w) {
		return serializeBlocks(w.blocks);
	}

	public static byte[] serializeBlocks(TreeMap<Point, Block> blocks) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (Point p : blocks.keySet()) {
			Block b = blocks.get(p);
			byte[] tmp = toBytes(b);
			try {
				out.write(tmp);
			} catch (IOException e) {
			}
		}
		return out.toByteArray();
	}

	public static TreeMap<Point, Block> load(byte[] data) {
		TreeMap<Point, Block> blocks = new TreeMap<>(BlockState.pointComparer);
		if (data.length % 12 != 0) {
			throw new IllegalArgumentException("Invalid data length");
		}
		for (int i = 0; i < data.length; i += 12) {
			int px = Util.toInt(data, i);
			int py = Util.toInt(data, i + 4);
			BlockType type = BlockType.values()[Util.toInt(data, i + 8)];

			blocks.put(new Point(px, py), Block.createBlock(type, px, py, true));
		}
		return blocks;
	}

	public static byte[] toBytes(Block b) {
		return Util.combine(
				Util.combine(Util.toBytes((int) b.getPos().x),
						Util.toBytes((int) b.getPos().y)),
				Util.toBytes(b.type.ordinal()));
	}
}
