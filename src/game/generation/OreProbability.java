package game.generation;

import game.blocks.BlockType;

public class OreProbability {
	/**
	 *
	 * @param ore
	 *            Type of ore
	 * @param y
	 *            Distance from the surface
	 * @return An integer representing weight
	 */
	public static int getWeight(BlockType ore, int y) {
		int ret = 0;
		switch (ore) {
		case COAL_ORE:
			ret = 100 - y * y / 49;
			break;
		case IRON_ORE:
			ret = y * y / 400;
			break;
		case GOLD_ORE:
			ret = y * y / 500;
			break;
		case REDSTONE_ORE:
			ret = y * y / 600;
			break;
		case DIAMOND_ORE:
			ret = y * y / 1400;
			break;
		default:
			break;
		}
		return ret;
	}
}
