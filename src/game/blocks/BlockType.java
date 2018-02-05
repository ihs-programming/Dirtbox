package game.blocks;

public enum BlockType {
	// Align the blocks like this to help git with version control
	// Git keeps track of what lines change, so keeping blocks on different
	// lines should reduce merge conflicts)
	//
	// Notice also that the blocks are alphabetically ordered...
	// @formatter:off
	BEDROCK(1, 1),
	COAL_ORE(2, 2),
	DIAMOND_ORE(2, 3),
	DIRT(2, 0),
	EMPTY(4, 11),
	GOLD_ORE(0, 2),
	GRAVEL(3, 1),
	GRASS(3, 0),
	IRON_ORE(1, 2),
	LEAVES(5, 3),
	REDSTONE_ORE(3, 3),
	SAND(2, 1),
	SANDSTONE(0, 11),
	STONE(1, 0),
	UNDEFINED(14, 1),
	WATER(14, 0),
	WOOD(4, 1);
	// @formatter:on

	// Position on spritesheet (from 0, 0 being the top left block)
	int sx;
	int sy;

	private BlockType(int x, int y) {
		sx = x;
		sy = y;
	}

	public static boolean isOre(BlockType type) {
		switch (type) {
		case COAL_ORE:
		case DIAMOND_ORE:
		case GOLD_ORE:
		case IRON_ORE:
		case REDSTONE_ORE:
			return true;
		default:
			return false;
		}
	}

	public static boolean isSeeThrough(BlockType type) {
		return type == EMPTY;
	}

	public static int getLightValue(Block b) {
		switch (b.type) {
		case IRON_ORE:
			return 63;
		default:
			return -1;
		}
	}
}
