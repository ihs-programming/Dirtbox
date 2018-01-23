package game.generation;

import java.util.Arrays;

import game.blocks.BlockType;

public class TreeMaker {
	public enum TreeType {
		OAK
	}

	public static BlockType[][] makeTree(int w, int h, TreeType tree) {
		BlockType[][] ret = new BlockType[w][h];
		int trunk = w / 2;
		for (BlockType[] element : ret) {
			Arrays.fill(element, BlockType.EMPTY);
		}
		for (int i = 0; i < h; i++) {
			ret[trunk][i] = BlockType.WOOD;
		}
		ret[trunk][h - 1] = BlockType.WATER;
		int leafOffset = 0;
		for (int i = 2; i < h; i += 1) {
			for (int z = leafOffset; z < w - leafOffset; z++) {
				if (ret[z][i] == BlockType.EMPTY) {
					ret[z][i] = BlockType.WATER;
				}
			}
			leafOffset++;
		}
		return ret;
	}
}
