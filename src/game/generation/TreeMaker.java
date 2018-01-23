package game.generation;

import java.util.Arrays;

import game.blocks.BlockType;

public class TreeMaker {
	public enum TreeType {
		OAK
	}

	public static BlockType[][] makeTree(int w, int h, int radius, TreeType tree) {
		BlockType[][] ret = new BlockType[w][h + 1 + radius];
		int trunk = w / 2;
		for (BlockType[] element : ret) {
			Arrays.fill(element, BlockType.EMPTY);
		}
		for (int i = 0; i < h; i++) {
			ret[trunk][i] = BlockType.WOOD;
		}
		ret[trunk][h - 1] = BlockType.LEAVES;
		for (int i = 0; i < w; i++) {
			for (int z = 0; z <= h + 1 + radius; z++) {
				if (Math.sqrt(
						Math.pow(z - h + 1, 2) + Math.pow(i - trunk, 2)) <= radius
								* (0.75 + Math.random() / 2)) {
					ret[i][z] = BlockType.LEAVES;
				}
			}
		}
		return ret;
	}
}
