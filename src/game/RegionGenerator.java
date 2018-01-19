package game;

import java.awt.Point;
import java.util.TreeMap;

import org.newdawn.slick.geom.Rectangle;

import game.blocks.Block;
import game.blocks.BlockType;
import game.blocks.SolidBlock;
import util.ImprovedNoise;

public class RegionGenerator {
	private static final int CHUNK_HEIGHT = 127;
	private static final int CHUNK_SIZE = 63;
	private static final int BEDROCK_LAYER = 127;
	private static final int CHUNK_BOUNDARY_HEIGHT = (int) (CHUNK_HEIGHT * 0.7);

	public static TreeMap<Point, Block> blocks = new TreeMap<>((p1, p2) -> {
		if (p1.x == p2.x) {
			return p1.y - p2.y;
		}
		return p1.x - p2.x;
	});

	RegionGenerator() {

	}

	RegionGenerator(Rectangle s) {
		for (int i = (int) (s.getMinX() - 1); i <= s.getMaxX() + 1; i++) {
			for (int j = (int) (s.getMinY() - 1); j <= s.getMaxY() + 1; j++) {
				generateWorld(i, j);
			}
		}
	}

	public void generateWorld(int x, int y) {
		generateWorld(x, y, (int) (1000 * Math.random()));
	}

	public void generateWorld(int x, int y, int seed) {
		Point curpos = new Point(x, y);
		if (blocks.containsKey(curpos)) {
			return;
		}
		if (y >= BEDROCK_LAYER) {
			blocks.put(curpos, new SolidBlock(BlockType.BEDROCK, x, y));
		} else if (y >= 0) {
			int chunkStart = x / CHUNK_SIZE * CHUNK_SIZE;
			if (x < 0) {
				chunkStart -= CHUNK_SIZE;
			}
			Block[][] chunk = generateChunk(chunkStart, 0, seed++);
			for (int i = 0; i < chunk.length; i++) {
				for (int j = 0; j < chunk[i].length; j++) {
					blocks.put(new Point(i + chunkStart, j), chunk[i][j]);
				}
			}
		} else {
			// Do not generate blocks in the air
		}
	}

	private Block[][] generateChunk(int x, int y, int seed) {
		long chunkgenerationtime = System.nanoTime();
		BiomeType biometype = randombiome(); // selects a random biome

		// The blocks
		Block[][] blocks = new Block[CHUNK_SIZE][CHUNK_HEIGHT];
		int[] heightMap = new int[CHUNK_SIZE];
		for (int i = 0; i < heightMap.length; i++) {
			heightMap[i] = (int) (20
					* ImprovedNoise.noise(seed + 1.0 / CHUNK_SIZE * i, 1, 1)
					+ CHUNK_SIZE / 2);
		}
		for (int i = 0; i < CHUNK_SIZE; i++) {
			for (int z = 0; z < heightMap[i]; z++) {
				blocks[i][z] = new SolidBlock(BlockType.EMPTY,
						(i + x) * Block.BLOCK_SPRITE_SIZE,
						(z + y) * Block.BLOCK_SPRITE_SIZE);
			}
			for (int z = heightMap[i]; z < CHUNK_HEIGHT; z++) {
				BlockType type = getType(z - heightMap[i], biometype);
				blocks[i][z] = new SolidBlock(type,
						(i + x) * Block.BLOCK_SPRITE_SIZE,
						(z + y) * Block.BLOCK_SPRITE_SIZE);
			}
		}

		if (biometype == BiomeType.DESERT) {
			int height = Math.max(heightMap[0], heightMap[heightMap.length - 1]) + 1;
			for (int i = 0; i < heightMap.length; i++) {
				for (int z = height; z < CHUNK_HEIGHT; z++) {
					if (((SolidBlock) blocks[i][z]).type == BlockType.EMPTY) {
						blocks[i][z] = new SolidBlock(BlockType.WATER,
								(i + x) * Block.BLOCK_SPRITE_SIZE,
								(z + y) * Block.BLOCK_SPRITE_SIZE);
					}
				}
			}
		}

		if (Viewport.DEBUG_MODE) {
			System.out.println((System.nanoTime() - chunkgenerationtime) / 1000000.0
					+ " ms to generate chunk of type " + biometype);
		}
		return blocks;
	}

	/**
	 * Get the block type.
	 *
	 * @param y
	 *            Y is DISTANCE RELATIVE TO SURFACE.
	 * @param biome
	 * @return
	 */
	private BlockType getType(int y, BiomeType biome) {
		if (y < 5 + 2 * Math.random()) {
			switch (biome) {
			case DESERT:
				return BlockType.SAND;
			case MOUNTAIN:
				return BlockType.STONE;
			}

			if (y == 0) {
				return BlockType.GRASS;
			}
			return BlockType.DIRT;
		}
		if (Math.random() < 0.01) {
			double val = Math.random();
			if (val < 0.1) {
				return BlockType.DIAMOND_ORE;
			} else if (val < 0.2) {
				return BlockType.REDSTONE_ORE;
			} else if (val < 0.3) {
				return BlockType.GOLD_ORE;
			} else {
				return BlockType.COAL_ORE;
			}
		}

		return BlockType.STONE;
	}

	private BiomeType randombiome() { // selects a random biome
		return BiomeType.values()[(int) (Math.random() * BiomeType.values().length)];
	}
}
