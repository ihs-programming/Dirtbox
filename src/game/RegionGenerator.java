package game;

import java.awt.Point;
import java.util.TreeMap;

import org.newdawn.slick.geom.Rectangle;

import game.blocks.Block;
import game.blocks.BlockType;
import game.blocks.EmptyBlock;
import util.ImprovedNoise;

public class RegionGenerator {
	private static final double SEED_STEP = 1.5;

	private static final int CHUNK_HEIGHT = 127;
	private static final int CHUNK_SIZE = 63;
	private static final int BEDROCK_LAYER = 127;
	private static final int CHUNK_BOUNDARY_HEIGHT = (int) (CHUNK_HEIGHT * 0.7);

	/**
	 * The seed that region generators use. This is static because multiple
	 * instances of RegionGenerator are created.
	 */
	private final static double seed = 1000 * Math.random();
	/**
	 * Limits generation to 10 thousand chunks for now.
	 */
	private final static BiomeType[] biomes = new BiomeType[10000];

	/*
	 * Block object that the regiongenerator will operate on
	 */
	private TreeMap<Point, Block> blocks;
	static {
		biomes[0] = randombiome();
		for (int i = 1; i < biomes.length; i++) {
			// Switch biome type
			if (Math.random() < 0.5) {
				BiomeType next = randombiome();
				biomes[i] = BiomeType.BUFFER;

				i++;
				if (i < biomes.length) {
					biomes[i] = next;
				}
			} else {
				biomes[i] = biomes[i - 1];
			}
		}
	}

	/**
	 * For some reason, multiple instances of this are created. This meant a lot of
	 * hard to find bugs.
	 *
	 * @param s
	 */
	RegionGenerator(Rectangle s, TreeMap<Point, Block> blocks) {
		this.blocks = blocks;
		for (int i = (int) (s.getMinX() - 1); i <= s.getMaxX() + 1; i++) {
			for (int j = (int) (s.getMinY() - 1); j <= s.getMaxY() + 1; j++) {
				generateWorld(i, j);
			}
		}
	}

	public void generateWorld(int x, int y) {
		Point curpos = new Point(x, y);
		if (blocks.containsKey(curpos)) {
			return;
		}
		if (y >= BEDROCK_LAYER) {
			blocks.put(curpos, Block.createBlock(BlockType.BEDROCK, x, y));
		} else if (y >= 0) {
			int chunkStart = x / CHUNK_SIZE * CHUNK_SIZE;
			if (x < 0) {
				chunkStart -= CHUNK_SIZE;
			}
			Block[][] chunk = generateChunk(chunkStart, 0, 0);
			boolean cavemap[][] = generateMap();
			for (int i = 0; i < chunk.length; i++) {
				for (int j = 0; j < chunk[i].length; j++) {
					if (cavemap[i][j]) {
						blocks.put(new Point(i + chunkStart, j), chunk[i][j]);
					} else {
						blocks.put(new Point(i + chunkStart, j),
								Block.createBlock(BlockType.EMPTY, i, j));
					}
				}
			}
		} else {
			// Do not generate blocks in the air
		}
	}

	private Block[][] generateChunk(int x, int y, double seed) {
		int chunkNumber = biomes.length / 2 + x / CHUNK_SIZE;
		seed = SEED_STEP * chunkNumber + RegionGenerator.seed;

		long chunkgenerationtime = System.nanoTime();
		BiomeType biometype = biomes[chunkNumber];

		// The blocks
		Block[][] blocks = new Block[CHUNK_SIZE][CHUNK_HEIGHT];
		BlockType blocksenum[][] = new BlockType[CHUNK_SIZE][CHUNK_HEIGHT];
		int[] heightMap = new int[CHUNK_SIZE];
		for (int i = 0; i < heightMap.length; i++) {
			heightMap[i] = (int) (20
					* ImprovedNoise.noise(seed + SEED_STEP * i / CHUNK_SIZE,
							RegionGenerator.seed, RegionGenerator.seed)
					+ CHUNK_SIZE / 2);
		}
		// Generate the underlying blocks
		for (int i = 0; i < CHUNK_SIZE; i++) {
			for (int z = 0; z < heightMap[i]; z++) {
				blocks[i][z] = Block.createBlock(BlockType.EMPTY,
						(i + x) * Block.BLOCK_SPRITE_SIZE,
						(z + y) * Block.BLOCK_SPRITE_SIZE);
				blocksenum[i][z] = BlockType.EMPTY;
			}
			for (int z = heightMap[i]; z < CHUNK_HEIGHT; z++) {
				if (blocks[i][z] == null) {
					BlockType type = getType(i, z, biometype, heightMap);
					if (biometype == BiomeType.BUFFER) {
						type = getType(i, z,
								biomes[chunkNumber
										+ (2.0 * i * Math.random() >= CHUNK_SIZE / 2.0 ? 1
												: -1)],
								heightMap);
					}

					// TODO: Won't generate ores at edge
					if (BlockType.isOre(type) && i < CHUNK_SIZE - 4
							&& z < CHUNK_HEIGHT - 4) {
						for (int a = 0; a < 3; a++) {
							for (int b = 0; b < 3; b++) {
								if (Math.random() < 0.5) {
									blocks[i + a][z + b] = Block.createBlock(type,
											(i + x + a) * Block.BLOCK_SPRITE_SIZE,
											(z + y + b) * Block.BLOCK_SPRITE_SIZE);
								}
							}
						}
					}
					blocks[i][z] = Block.createBlock(type,
							(i + x) * Block.BLOCK_SPRITE_SIZE,
							(z + y) * Block.BLOCK_SPRITE_SIZE);

				}

			}
		}

		if (biometype == BiomeType.DESERT) {
			int height = Math.max(heightMap[0], heightMap[heightMap.length - 1]) + 1;
			for (int i = 0; i < heightMap.length; i++) {
				for (int z = height; z < CHUNK_HEIGHT; z++) {
					if (blocks[i][z] instanceof EmptyBlock) {
						blocks[i][z] = Block.createBlock(BlockType.WATER,
								(i + x) * Block.BLOCK_SPRITE_SIZE,
								(z + y) * Block.BLOCK_SPRITE_SIZE);
						blocksenum[i][z] = BlockType.WATER;
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
	private BlockType getType(int x, int z, BiomeType biome, int heightMap[]) {
		BlockType type = BlockType.UNDEFINED;
		int y = z - heightMap[x];
		if (y < 5 + 2 * Math.random()) {
			switch (biome) {
			case DESERT:
				type = BlockType.SAND;
				break;
			case MOUNTAIN:
				type = BlockType.STONE;
				break;
			default:
				type = BlockType.DIRT;
				break;
			}

			if (y == 0) {
				switch (biome) {
				case DESERT:
					type = BlockType.SAND;
					break;
				case MOUNTAIN:
					type = BlockType.STONE;
					break;
				default:
					type = BlockType.GRASS;
					break;
				}
			}
		} else {
			if (biome == BiomeType.DESERT && y < 15 + 4 * Math.random()) {
				return BlockType.SANDSTONE;
			}
			if (y >= 8) {
				if (Math.random() < 0.003) {
					type = oreselector(x, z, heightMap);
				} else {
					type = BlockType.STONE;
				}
			} else {
				if (Math.random() < 0.5) {
					switch (biome) {
					case DESERT:
						type = BlockType.SANDSTONE;
						break;
					case MOUNTAIN:
						type = BlockType.STONE;
						break;
					default:
						type = BlockType.DIRT;
						break;
					}
				} else {
					type = BlockType.STONE;
				}
			}

		}
		return type;
	}

	private BlockType oreselector(int x, int j, int heightMap[]) {
		BlockType type = BlockType.STONE;
		if (Math.random() <= 1) {
			if (j > CHUNK_HEIGHT * 0.9 && Math.random() <= 0.3) {
				if (Math.random() < 0.2) {
					type = BlockType.DIAMOND_ORE;
				} else {
					type = BlockType.REDSTONE_ORE;
				}
			} else {
				double oreselection = Math.random();
				if (oreselection < 0.1) {
					type = BlockType.GOLD_ORE;
				}
				if (oreselection >= 0.1 && oreselection < 0.35) {
					type = BlockType.IRON_ORE;
				}
				if (oreselection >= 0.35) {
					type = BlockType.COAL_ORE;
				}
			}
		}
		return type;
	}

	private static BiomeType randombiome() { // selects a random biome
		BiomeType ret = BiomeType
				.values()[(int) (Math.random() * BiomeType.values().length)];
		// We don't want to return a BUFFER biome
		if (ret == BiomeType.BUFFER) {
			return randombiome();
		}
		return ret;
	}

	float chanceToStartAlive = 0.57f;

	public boolean[][] generateMap() {
		// Create a new map

		boolean[][] cellmap = new boolean[CHUNK_SIZE][CHUNK_HEIGHT];
		// Set up the map with random values
		cellmap = initialiseMap(cellmap, CHUNK_SIZE, CHUNK_HEIGHT);
		// And now run the simulation for a set number of steps
		int numberOfSteps = 7;
		int deathLimit = 3;
		int birthLimit = 4;
		for (int i = 0; i < numberOfSteps; i++) {
			cellmap = doSimulationStep(cellmap, CHUNK_SIZE, CHUNK_HEIGHT, deathLimit,
					birthLimit);
		}
		return cellmap;
	}

	public boolean[][] doSimulationStep(boolean[][] oldMap, int width, int height,
			int deathLimit,
			int birthLimit) {
		boolean[][] newMap = new boolean[width][height];
		// Loop over each row and column of the map
		for (int x = 0; x < oldMap.length; x++) {
			for (int y = 0; y < oldMap[0].length; y++) {
				if (y > CHUNK_HEIGHT - 0.9 * CHUNK_BOUNDARY_HEIGHT) {
					int nbs = countAliveNeighbours(oldMap, x, y);
					// The new value is based on our simulation rules
					// First, if a cell is alive but has too few neighbours,
					// kill it.
					if (oldMap[x][y]) {
						if (nbs < deathLimit) {
							newMap[x][y] = false;
						} else {
							newMap[x][y] = true;
						}
					} // Otherwise, if the cell is dead now, check if it has the
						// right
						// number of
						// neighbours to be 'born'
					else {
						if (nbs > birthLimit) {
							newMap[x][y] = true;
						} else {
							newMap[x][y] = false;
						}
					}
				} else {
					newMap[x][y] = true;
				}
			}
		}
		return newMap;
	}

	public int countAliveNeighbours(boolean[][] map, int x, int y) {
		int count = 0;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				int neighbour_x = x + i;
				int neighbour_y = y + j;
				// If we're looking at the middle point
				if (i == 0 && j == 0) {
					// Do nothing, we don't want to add ourselves in!
				}
				// In case the index we're looking at it off the edge of the map
				else if (neighbour_x < 0 || neighbour_y < 0 || neighbour_x >= map.length
						|| neighbour_y >= map[0].length) {
					count = count + 1;
				}
				// Otherwise, a normal check of the neighbour
				else if (map[neighbour_x][neighbour_y]) {
					count = count + 1;
				}
			}
		}
		return count;
	}

	public boolean[][] initialiseMap(boolean[][] map, int width, int height) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (y > CHUNK_HEIGHT - 0.9 * CHUNK_BOUNDARY_HEIGHT) {
					if (Math.random() < chanceToStartAlive) {
						map[x][y] = true;
					}
				} else {
					map[x][y] = true;
				}
			}
		}
		return map;
	}

}
