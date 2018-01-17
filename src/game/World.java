package game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.blocks.Block;
import game.blocks.BlockType;
import game.blocks.SolidBlock;
import game.entities.Entity;

class generateRegion extends Thread {
	private static final int CHUNK_HEIGHT = 127;
	private static final int CHUNK_SIZE = 63;
	private static final int BEDROCK_LAYER = 127;
	private static final int CHUNK_BOUNDARY_HEIGHT = (int) (CHUNK_HEIGHT * 0.7);

	public static TreeMap<Position, Block> blocks = new TreeMap<>(new PositionComparator());

	generateRegion() {

	}

	generateRegion(Rectangle s) {
		for (int i = (int) (s.getMinX() - 1); i <= s.getMaxX() + 1; i++) {
			for (int j = (int) (s.getMinY() - 1); j <= s.getMaxY() + 1; j++) {
				generateWorld(i, j);
			}
		}
		start();
	}

	public void generateWorld(int x, int y) {
		Position curpos = new Position(x, y);
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
			Block[][] chunk = generateChunk(chunkStart, 0);
			for (int i = 0; i < chunk.length; i++) {
				for (int j = 0; j < chunk[i].length; j++) {
					blocks.put(new Position(i + chunkStart, j), chunk[i][j]);
				}
			}
		} else {
			// Do not generate blocks in the air
		}
	}

	private Block[][] generateChunk(int x, int y) {
		long chunkgenerationtime = System.nanoTime();
		BiomeType biometype = randombiome(); // selects a random biome

		Block[][] blocks = new Block[CHUNK_SIZE][CHUNK_HEIGHT];

		int blocksez[][];
		blocksez = new int[CHUNK_SIZE][CHUNK_HEIGHT];

		BlockType blocksenum[][] = new BlockType[CHUNK_SIZE][CHUNK_HEIGHT];

		for (int i = 0; i < CHUNK_SIZE; i++) {
			int depth = CHUNK_HEIGHT - 1;
			blocks[i][depth] = new SolidBlock(BlockType.STONE, (i + x) * Block.BLOCK_SPRITE_SIZE,
					(depth + y) * Block.BLOCK_SPRITE_SIZE);
			blocksez[i][depth] = 3;
		}
		boolean cavemap[][] = generateMap();
		for (int j = CHUNK_HEIGHT - 2; j >= 0; j--) {
			for (int i = 0; i < CHUNK_SIZE; i++) {

				int blockType = blocktype(blocksez, i, j, biometype);
				blocksez[i][j] = blockType;

				BlockType type = BlockType.EMPTY;
				blocksenum[i][j] = BlockType.EMPTY;

				type = blockpicker(blockType, biometype, i, j, blocksez, blocksenum);

				blocksenum[i][j] = type;
				if (cavemap[i][j]) {
					blocks[i][j] = new SolidBlock(type, (i + x) * Block.BLOCK_SPRITE_SIZE,
							(j + y) * Block.BLOCK_SPRITE_SIZE);
				} else {
					blocks[i][j] = new SolidBlock(BlockType.EMPTY,
							(i + x) * Block.BLOCK_SPRITE_SIZE, (j + y) * Block.BLOCK_SPRITE_SIZE);
				}
			}

		}
		if (Viewport.DEBUG_MODE) {
			System.out.println((System.nanoTime() - chunkgenerationtime) / 1000000.0
					+ " ms to generate chunk of type " + biometype);
		}
		return blocks;
	}

	private BiomeType randombiome() { // selects a random biome
		int randombiome = (int) Math.floor(Math.random() * 4);
		BiomeType biome = null;
		switch (randombiome) {
		case 0:
			biome = BiomeType.PLAIN;
			break;
		case 1:
			biome = BiomeType.DESERT;
			break;
		case 2:
			biome = BiomeType.MOUNTAIN;
			break;
		case 3:
			biome = BiomeType.OCEAN;
			break;
		}
		return biome;
	}

	private int caseplain(int blocksez[][], int i, int j, int empty) {
		int block = 0;
		if (blocksez[i + 1][j + 1] != 0 && blocksez[i - 1][j + 1] != 0) {
			block = empty;
		} else {
			block = 0;
		}
		return block;
	}

	private int casedesert(int blocksez[][], int i, int j, int empty) {
		int block = 0;
		if (blocksez[i + 1][j + 1] != 0 && blocksez[i - 1][j + 1] != 0
				&& blocksez[i + 2][j + 1] != 0 && blocksez[i - 2][j + 1] != 0) {
			block = empty;
		} else {
			block = 0;
		}
		return block;
	}

	private int casemountain(int blocksez[][], int i, int j, int empty) {
		int block = 0;
		int mountaincheck = (int) Math.floor(Math.random() * 2) + 1;
		int mountaincheck2 = (int) Math.floor(Math.random() * 2) + 1;
		if (blocksez[i + 1][j + mountaincheck] != 0 && blocksez[i - 1][j + mountaincheck2] != 0) {
			block = empty;
		} else {
			block = 0;
		}
		return block;
	}

	private int caseocean(int blocksez[][], int i, int j, int empty) {
		int block = 0;
		if (blocksez[i + 1][j + 1] != 0 && blocksez[i - 1][j + 1] != 0
				&& blocksez[i + 2][j + 1] != 0 && blocksez[i - 2][j + 1] != 0) {
			block = empty;
		} else {
			block = 0;
		}
		if (j < CHUNK_HEIGHT - (0.9 + (Math.pow((i - CHUNK_SIZE / 2.0) / (CHUNK_SIZE / 2.0), 2)
				+ (Math.random() - 0.5) / 5) / 10) * CHUNK_BOUNDARY_HEIGHT) {
			block = 0;
		}
		return block;
	}

	private int blocktype(int blocksez[][], int i, int j, BiomeType biometype) {
		int empty = (int) (Math.random() + j / (CHUNK_HEIGHT * 30.0 / 100.0));

		if (blocksez[i][j + 1] != 0) {
			blocksez[i][j] = empty;
			if (empty - 1 >= Math.random() * 30.0) {
				blocksez[i][j] = 4;
			}
			if (empty == 1 && !(i < 2) && !(i >= CHUNK_SIZE - 2)) {
				switch (biometype) {
				case PLAIN:
					blocksez[i][j] = caseplain(blocksez, i, j, empty);
					break;
				case DESERT:
					blocksez[i][j] = casedesert(blocksez, i, j, empty);
					break;
				case MOUNTAIN:
					blocksez[i][j] = casemountain(blocksez, i, j, empty);
					break;
				case OCEAN:
					blocksez[i][j] = caseocean(blocksez, i, j, empty);
					break;
				}
			} else {
				blocksez = edgecase(biometype, blocksez, empty, i, j);
			}
		}

		if (blocksez[i][j + 1] == 5) {
			blocksez[i][j] = 0;
		}
		if (blocksez[i][j + 1] == 1 && blocksez[i][j + 2] != 5 && blocksez[i][j + 1] != 0
				&& j < CHUNK_HEIGHT * 30.0 / 100.0 * Math.random()) {
			blocksez[i][j] = 5;
		}
		if (blocksez[i][j + 1] == 0) {
			blocksez[i][j] = 0;
		}
		return blocksez[i][j];
	}

	private int leftedgecase(BiomeType biometype, int blocksez[][], int i, int j, int empty) {
		switch (biometype) {
		case PLAIN:
			if (blocksez[i + 1][j + 1] != 0 && blocksez[i][j + 1] != 0) {
				blocksez[i][j] = empty;
			} else {
				blocksez[i][j] = 0;
			}
			break;
		case DESERT:
			if (blocksez[i + 1][j + 1] != 0 && blocksez[i][j + 1] != 0
					&& blocksez[i + 2][j + 1] != 0 && blocksez[i][j + 1] != 0) {
				blocksez[i][j] = empty;
			} else {
				blocksez[i][j] = 0;
			}
			break;
		case MOUNTAIN:
			int mountaincheck = (int) Math.floor(Math.random() * 2) + 1;
			int mountaincheck2 = (int) Math.floor(Math.random() * 2) + 1;
			if (blocksez[i][j + mountaincheck] != 0 && blocksez[i][j + mountaincheck2] != 0) {
				blocksez[i][j] = empty;
			} else {
				blocksez[i][j] = 0;
			}
			break;
		case OCEAN:
			if (blocksez[i + 1][j + 1] != 0 && blocksez[i][j + 1] != 0
					&& blocksez[i + 2][j + 1] != 0 && blocksez[i][j + 1] != 0) {
				blocksez[i][j] = empty;
			} else {
				blocksez[i][j] = 0;
			}
			break;
		}
		if (j < CHUNK_HEIGHT - CHUNK_BOUNDARY_HEIGHT) {
			blocksez[i][j] = 0;
		}
		return blocksez[i][j];
	}

	private int rightedgecase(BiomeType biometype, int blocksez[][], int i, int j, int empty) {
		switch (biometype) {
		case PLAIN:
			if (blocksez[i][j + 1] != 0 && blocksez[i - 1][j + 1] != 0) {
				blocksez[i][j] = empty;
			} else {
				blocksez[i][j] = 0;
			}
			break;
		case DESERT:
			if (blocksez[i][j + 1] != 0 && blocksez[i - 1][j + 1] != 0 && blocksez[i][j + 1] != 0
					&& blocksez[i - 2][j + 1] != 0) {
				blocksez[i][j] = empty;
			} else {
				blocksez[i][j] = 0;
			}
			break;
		case MOUNTAIN:
			int mountaincheck = (int) Math.floor(Math.random() * 2) + 1;
			int mountaincheck2 = (int) Math.floor(Math.random() * 2) + 1;
			if (blocksez[i][j + mountaincheck] != 0 && blocksez[i - 1][j + mountaincheck2] != 0) {
				blocksez[i][j] = empty;
			} else {
				blocksez[i][j] = 0;
			}
			break;
		case OCEAN:
			if (blocksez[i][j + 1] != 0 && blocksez[i - 1][j + 1] != 0 && blocksez[i][j + 1] != 0
					&& blocksez[i - 2][j + 1] != 0) {
				blocksez[i][j] = empty;
			} else {
				blocksez[i][j] = 0;
			}
			break;
		}
		if (j < CHUNK_HEIGHT - CHUNK_BOUNDARY_HEIGHT) {
			blocksez[i][j] = 0;
		}
		return blocksez[i][j];
	}

	private int[][] edgecase(BiomeType biometype, int blocksez[][], int empty, int i, int j) {
		if (empty == 1) {
			if (biometype != BiomeType.MOUNTAIN) {
				if (j < CHUNK_HEIGHT * 30.0 / 100.0 * Math.random()) {
					blocksez[i][j] = 0;
				}
			}

			if (i <= 2) {
				blocksez[i][j] = leftedgecase(biometype, blocksez, i, j, empty);
			}
			if (i >= CHUNK_SIZE - 2) {
				blocksez[i][j] = rightedgecase(biometype, blocksez, i, j, empty);
			}
		}
		return blocksez;
	}

	// The following determines the block depending on the biome

	private BlockType airselector(BiomeType biometype, int j, BlockType type) {
		if (biometype != BiomeType.OCEAN) {
			type = BlockType.EMPTY;
		} else {
			if (j >= CHUNK_HEIGHT - (CHUNK_BOUNDARY_HEIGHT - 2)) {
				type = BlockType.WATER;
			}
		}
		return type;
	}

	private BlockType dirtselector(BiomeType biometype, BlockType type) {
		type = BlockType.DIRT;
		switch (biometype) {
		case PLAIN:
			type = BlockType.DIRT;
			break;
		case DESERT:
			type = BlockType.SANDSTONE;
			break;
		case MOUNTAIN:
			type = BlockType.STONE;
			double random = Math.random();
			if (random <= 0.01) {
				type = BlockType.COAL_ORE;
			} else if (random <= 0.015) {
				type = BlockType.IRON_ORE;
			}
			break;
		case OCEAN:
			type = BlockType.SANDSTONE;
			break;
		}
		return type;
	}

	private BlockType gravelselector(int j, BlockType type) {
		if (Math.random() * j < 0.05 * Math.random() * CHUNK_HEIGHT) {
			type = BlockType.GRAVEL;
		} else {
			type = BlockType.STONE;
		}
		return type;
	}

	private BlockType stoneselector(int i, int j, int blocksez[][], BlockType blocksenum[][],
			BiomeType biometype, BlockType type) {
		type = BlockType.STONE;

		if (i + 1 < CHUNK_SIZE && i - 1 >= 0 && j + 1 < CHUNK_HEIGHT && j - 1 >= 0) {
			for (int blocksearch = 0; blocksearch < 8; blocksearch++) {
				int looky = (int) Math.round(Math.random() * 2 - 1);
				int lookx = (int) Math.round(Math.random() * 2 - 1);
				if (blocksez[i + lookx][j + looky] == 4 || blocksez[i + lookx][j + looky] == 2) {
					type = blocksenum[i + lookx][j + looky];
					break;
				}
			}
		} else {
			if (i + 1 >= CHUNK_SIZE || i - 1 < 0) {
				if (biometype != BiomeType.MOUNTAIN) {
					if (j < CHUNK_HEIGHT * 35.0 / 100.0 * Math.random()) {
						blocksez[i][j] = 0;
						type = BlockType.EMPTY;
					}
				}
			}
		}
		return type;
	}

	private BlockType oreselector(int j, BlockType type) {
		if (Math.random() <= 0.05) {
			if (CHUNK_HEIGHT - (j + 1) <= CHUNK_HEIGHT / 10.0 && Math.random() <= 0.3) {
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
		} else {
			type = BlockType.STONE;
		}
		return type;
	}

	private BlockType grassselector(BiomeType biometype, BlockType type) {
		type = BlockType.UNDEFINED;
		switch (biometype) {
		case PLAIN:
			type = BlockType.GRASS;
			break;
		case DESERT:
			type = BlockType.SAND;
			break;
		case MOUNTAIN:
			type = BlockType.GRAVEL;
			break;
		case OCEAN:
			type = BlockType.SAND;
			break;
		}
		return type;
	}

	private BlockType blockpicker(int blockType, BiomeType biometype, int i, int j,
			int blocksez[][], BlockType blocksenum[][]) {
		BlockType type = BlockType.EMPTY;
		switch (blockType) {
		case 0:
			type = airselector(biometype, j, type);
			break;
		case 1:
			type = dirtselector(biometype, type);
			break;
		case 2:
			type = gravelselector(j, type);
			break;
		case 3:
			type = stoneselector(i, j, blocksez, blocksenum, biometype, type);
			break;
		case 4:
			type = oreselector(j, type);
			break;
		case 5:
			type = grassselector(biometype, type);
			break;
		default:
			type = BlockType.UNDEFINED;
		}
		return type;
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
			cellmap = doSimulationStep(cellmap, CHUNK_SIZE, CHUNK_HEIGHT, deathLimit, birthLimit);
		}
		return cellmap;
	}

	public boolean[][] doSimulationStep(boolean[][] oldMap, int width, int height, int deathLimit,
			int birthLimit) {
		boolean[][] newMap = new boolean[width][height];
		// Loop over each row and column of the map
		for (int x = 0; x < oldMap.length; x++) {
			for (int y = 0; y < oldMap[0].length; y++) {
				if (y > CHUNK_HEIGHT - 0.9 * CHUNK_BOUNDARY_HEIGHT) {
					int nbs = countAliveNeighbours(oldMap, x, y);
					// The new value is based on our simulation rules
					// First, if a cell is alive but has too few neighbours, kill it.
					if (oldMap[x][y]) {
						if (nbs < deathLimit) {
							newMap[x][y] = false;
						} else {
							newMap[x][y] = true;
						}
					} // Otherwise, if the cell is dead now, check if it has the right number of
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

	@Override
	public void run() {

	}
}

public class World {

	private ArrayList<Entity> characters;

	public World() {
		characters = new ArrayList<>();
		try {
			Image stalinsprite = new Image("data/characters/stalin.png");
			stalinsprite.setFilter(Image.FILTER_NEAREST);
			stalinsprite = stalinsprite.getScaledCopy(1, 2);
			Entity stalin = new Entity(stalinsprite, 1, 1, new Vector2f(0, 0));
			characters.add(stalin);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public void draw(Viewport vp) {

		Shape view = vp.getGameViewShape();
		Rectangle viewRect = new Rectangle(view.getMinX(), view.getMinY(), view.getWidth(),
				view.getHeight());
		Thread worldgenerationthread = new Thread(new generateRegion(viewRect));
		worldgenerationthread.start();
		for (int i = (int) (viewRect.getMinX() - 1); i <= viewRect.getMaxX(); i++) {
			Position start = new Position(i, (int) (viewRect.getMinY() - 1));
			Position end = new Position(i, (int) (viewRect.getMaxY() + 1));
			NavigableSet<Position> existingBlocks = generateRegion.blocks.navigableKeySet()
					.subSet(start, true, end, true);
			/*
			 * The following three lines somehow randomly cause up to 1000 ms of lag This is
			 * a big issue, as the game otherwise runs quite smoothly. Please fix!
			 * "734.582767 ms for draw (!!!) 743.448732 ms for render"
			 */
			for (Position p : existingBlocks) {
				generateRegion.blocks.get(p).draw(vp);
			}

		}
		for (Entity e : this.characters) {
			e.draw(vp);
		}

	}
}

class PositionComparator implements Comparator<Position> {
	@Override
	public int compare(Position p1, Position p2) {
		if (p1.x == p2.x) {
			return p1.y - p2.y;
		}
		return p1.x - p2.x;
	}
}

class Position {
	public int x;
	public int y;

	Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Position) {
			Position p = (Position) o;
			return p.x == x && p.y == y;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int px = mapToPositive(x);
		int py = mapToPositive(y);

		// Cantor pairing function
		// See: https://en.wikipedia.org/wiki/Pairing_function
		return (px + py) * (px + py + 1) / 2 + py;
	}

	private int mapToPositive(int v) {
		if (v < 0) {
			return 2 * -v + 1;
		} else {
			return 2 * v;
		}
	}
}