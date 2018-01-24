package game.generation;

import java.awt.Point;
import java.util.Arrays;
import java.util.TreeMap;

import org.newdawn.slick.geom.Rectangle;

import game.BiomeType;
import game.Viewport;
import game.blocks.Block;
import game.blocks.BlockType;
import game.blocks.EmptyBlock;
import game.generation.TreeMaker.TreeType;
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

	public RegionGenerator(Rectangle s, TreeMap<Point, Block> blocks) {
		this.blocks = blocks;
		for (int i = (int) (s.getMinX() - 1); i <= s.getMaxX() + 1; i++) {
			for (int j = Math.max(0, (int) (s.getMinY() - 1)); j <= s.getMaxY()
					+ 1; j++) {
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
			boolean cavemap[][] = generateMap(chunkStart);
			for (int i = 0; i < chunk.length; i++) {
				for (int j = 0; j < chunk[i].length; j++) {
					if (cavemap[i][j]) {
						blocks.put(new Point(i + chunkStart, j), chunk[i][j]);
					} else {
						blocks.put(new Point(i + chunkStart, j),
								Block.createBlock(BlockType.EMPTY, i + chunkStart, j));
					}
				}
			}
		} else {
			// Do not generate blocks in the air
		}
	}

	private static final int SEALEVEL = (int) (CHUNK_HEIGHT * 0.25)
			+ getAmplitude(BiomeType.OCEAN) + 1;

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
			if (biometype != BiomeType.BUFFER) {
				heightMap[i] = (int) (getAmplitude(biometype)
						* ImprovedNoise.noise(seed + SEED_STEP * i / CHUNK_SIZE,
								RegionGenerator.seed, RegionGenerator.seed)
						+ CHUNK_HEIGHT * 0.25);
			} else {
				heightMap[i] = (int) (getAmplitude(biomes, chunkNumber, i)
						* ImprovedNoise.noise(seed + SEED_STEP * i / CHUNK_SIZE,
								RegionGenerator.seed, RegionGenerator.seed)
						+ CHUNK_HEIGHT * 0.25);
			}
		}
		if (biometype == BiomeType.OCEAN) {
			boolean oceanLeft = biomes[chunkNumber - 1] == BiomeType.OCEAN;
			boolean oceanRight = biomes[chunkNumber + 1] == BiomeType.OCEAN;

			// Kinda like another biome?
			boolean deepOcean = oceanLeft && oceanRight;

			if (deepOcean) {
				for (int i = 0; i < heightMap.length; i++) {
					heightMap[i] += 2 * Math.sqrt(heightMap.length / 2);
					heightMap[i] += 2 * Math.sqrt(heightMap.length / 2
							- Math.abs(heightMap.length / 2 - i));
				}
			} else {
				for (int i = 0; i < heightMap.length / 2; i++) {
					if (oceanLeft) {
						heightMap[i] += 2 * Math.sqrt(heightMap.length / 2);
					} else {
						heightMap[i] += 2 * Math.sqrt(heightMap.length / 2
								- Math.abs(heightMap.length / 2 - i));
					}
				}
				for (int i = heightMap.length / 2; i < heightMap.length; i++) {
					if (oceanRight) {
						heightMap[i] += 2 * Math.sqrt(heightMap.length / 2);
						System.out.println(heightMap[i]);
					} else {
						heightMap[i] += 2 * Math.sqrt(heightMap.length / 2
								- Math.abs(heightMap.length / 2 - i));
					}
				}
			}
		}
		// Generate the underlying blocks
		for (int i = 0; i < CHUNK_SIZE; i++) {
			for (int z = 0; z < heightMap[i]; z++) {
				blocks[i][z] = Block.createBlock(BlockType.EMPTY,
						(i + x) * Block.BLOCK_SPRITE_SIZE,
						(z + y) * Block.BLOCK_SPRITE_SIZE);
				blocksenum[i][z] = BlockType.EMPTY;
			}
			for (int z = Math.max(0, heightMap[i]); z < CHUNK_HEIGHT; z++) {
				if (blocks[i][z] == null) {
					BlockType type = getType(i, z, biometype, heightMap);
					if (biometype == BiomeType.BUFFER) {
						type = getType(i, z,
								biomes[chunkNumber
										+ (Math.random() * 0.9 + 0.05 > -(Math
												.atan(0.5 * (i - CHUNK_SIZE / 2.0))
												/ Math.PI) + 0.5 ? 1
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

		if (biometype == BiomeType.OCEAN) {
			int height = SEALEVEL;
			for (int i = 0; i < heightMap.length; i++) {
				for (int z = height; z < CHUNK_HEIGHT; z++) {
					if (blocks[i][z] instanceof EmptyBlock) {
						blocks[i][z] = Block.createBlock(BlockType.WATER,
								(i + x) * Block.BLOCK_SPRITE_SIZE,
								(z + y) * Block.BLOCK_SPRITE_SIZE);
					}
				}
			}
		} else if (biometype == BiomeType.PLAIN || biometype == BiomeType.HILLS) {
			for (int i = 0; i < blocks.length - 5; i += 5) {
				BlockType[][] tree = TreeMaker.makeTree(5, 5, 2, TreeType.OAK);
				int trunkHeight = heightMap[i + 2] - 1;

				if (Math.random() < 0.8) {
					continue;
				}

				for (int a = 0; a < tree.length; a++) {
					for (int b = 0; b < tree[a].length; b++) {
						if (tree[tree.length - 1 - a][b] != BlockType.EMPTY) {
							if (blocks[i + a][trunkHeight - b].type == BlockType.EMPTY) {
								blocks[i + a][trunkHeight - b] = Block.createBlock(
										tree[tree.length - 1 - a][b],
										(i + a + x) * Block.BLOCK_SPRITE_SIZE,
										(trunkHeight - b + y) * Block.BLOCK_SPRITE_SIZE);
							}
						}
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

	private static int getAmplitude(BiomeType biometype) {
		int amplitude = 0;
		switch (biometype) {
		case OCEAN:
			amplitude = 3;
			break;
		case DESERT:
			amplitude = 15;
			break;
		case PLAIN:
			amplitude = 10;
			break;
		case HILLS:
			amplitude = 25;
		case MOUNTAIN:
			amplitude = 30;
			break;
		default:
			amplitude = 20;
			break;
		}
		return amplitude;
	}

	private int getAmplitude(BiomeType biomes[], int chunknumber, int i) {
		int rightamplitude = getAmplitude(biomes[chunknumber + 1]);
		int leftamplitude = getAmplitude(biomes[chunknumber - 1]);
		return (int) (i / (double) CHUNK_SIZE * rightamplitude
				+ (1 - i / (double) CHUNK_SIZE) * leftamplitude);
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
		// Surface material
		if (y == 0) {
			switch (biome) {
			case OCEAN:
				type = BlockType.SAND;
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
			// Top layer
		} else if (y < 5 + 2 * Math.random()) {
			switch (biome) {
			case DESERT:
			case OCEAN:
				type = BlockType.SAND;
				break;
			case MOUNTAIN:
				type = BlockType.STONE;
				break;
			default:
				type = BlockType.DIRT;
				break;
			}
			// Stone layer
		} else {
			if (biome == BiomeType.DESERT && y < 10 + 2 * Math.random()) {
				return BlockType.SANDSTONE;
			}
			if (Math.random() < 0.003) {
				type = oreselector(x, y, heightMap);
			} else {
				type = BlockType.STONE;
			}
		}
		return type;
	}

	private BlockType oreselector(int x, int j, int heightMap[]) {

		BlockType[] ores = new BlockType[] { BlockType.COAL_ORE, BlockType.IRON_ORE,
				BlockType.GOLD_ORE, BlockType.REDSTONE_ORE, BlockType.DIAMOND_ORE };
		int[] weights = new int[ores.length];

		int tot = 0;
		for (int i = 0; i < weights.length; i++) {
			weights[i] = OreProbability.getWeight(ores[i], j);
			tot += weights[i];
		}

		int prob = (int) (tot * Math.random());

		for (int i = 0; i < weights.length; i++) {
			if (prob < weights[i]) {
				return ores[i];
			}
			prob -= weights[i];
		}

		return BlockType.STONE;
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

	public boolean[][] generateMap(int xLeft) {
		boolean[][] cellmap = new boolean[CHUNK_SIZE][CHUNK_HEIGHT];
		for (int i = 0; i < cellmap.length; i++) {
			Arrays.fill(cellmap[i], true);
			for (int z = 60; z < cellmap[i].length; z++) {
				cellmap[i][z] = ImprovedNoise.noise((seed + i + xLeft) / 20.0, z / 20.0,
						1) < 0.45;
			}
		}
		return cellmap;
	}
}
