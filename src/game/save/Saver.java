package game.save;

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import game.blocks.Block;
import game.blocks.BlockType;
import game.network.io.Util;
import game.world.World;

public class Saver {
	private ArrayList<String> arraylist = new ArrayList<>();

	public HashSet<Block> getAllBlocks(World w) {
		Set<Block> blocks = w.getBlocks().entrySet().stream().map(e -> e.getValue())
				.collect(Collectors.toSet());

		return new HashSet<>(blocks);
	}

	/**
	 * We store these blocks in 12 byte chunks. [xpos] [ypos] [type]
	 *
	 * @param w
	 */
	public byte[] save(World w) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HashSet<Block> all = getAllBlocks(w);
		for (Block b : all) {
			byte[] tmp = Util.combine(
					Util.combine(Util.toBytes((int) b.getPos().x),
							Util.toBytes((int) b.getPos().y)),
					Util.toBytes(b.type.ordinal()));
			try {
				out.write(tmp);
			} catch (IOException e) {
			}
		}

		return out.toByteArray();
	}

	public TreeMap<Point, Block> load(byte[] data) {
		TreeMap<Point, Block> blocks = new TreeMap<>();
		if (data.length % 12 != 0) {
			throw new IllegalArgumentException("Invalid data length");
		}
		for (int i = 0; i < data.length; i += 12) {
			int xpos = Util.toInt(data, i);
			int ypos = Util.toInt(data, i + 4);
			BlockType type = BlockType.values()[Util.toInt(data, i + 8)];

			blocks.put(new Point(xpos, ypos), Block.createBlock(type, xpos, ypos));
		}
		return blocks;
	}

}
