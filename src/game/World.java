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

class generateRegion extends Thread{
	private static final int CHUNK_HEIGHT = 200;
	private static final int CHUNK_SIZE = 100;
	private static final int BEDROCK_LAYER = 200;
	private static final int CHUNK_BOUNDARY_HEIGHT = (int) (CHUNK_HEIGHT * 0.7);

	public static TreeMap<Position, Block> blocks = new TreeMap<>(new PositionComparator());
	
	generateRegion(){
		
	}
	generateRegion(Rectangle s){
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

		for (int j = CHUNK_HEIGHT - 2; j >= 0; j--) {
			for (int i = 0; i < CHUNK_SIZE; i++) {

				int blockType = blocktype(blocksez, i, j, biometype);
				blocksez[i][j] = blockType;

				BlockType type = BlockType.EMPTY;
				blocksenum[i][j] = BlockType.EMPTY;

				type = blockpicker(blockType, biometype, i, j, blocksez, blocksenum);

				blocksenum[i][j] = type;
				blocks[i][j] = new SolidBlock(type, (i + x) * Block.BLOCK_SPRITE_SIZE,
						(j + y) * Block.BLOCK_SPRITE_SIZE);
			}

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
					if (blocksez[i + 1][j + 1] != 0 && blocksez[i - 1][j + 1] != 0) {
						blocksez[i][j] = empty;
					} else {
						blocksez[i][j] = 0;
					}
					break;
				case DESERT:
					if (blocksez[i + 1][j + 1] != 0 && blocksez[i - 1][j + 1] != 0
							&& blocksez[i + 2][j + 1] != 0 && blocksez[i - 2][j + 1] != 0) {
						blocksez[i][j] = empty;
					} else {
						blocksez[i][j] = 0;
					}
					break;
				case MOUNTAIN:
					int mountaincheck = (int) Math.floor(Math.random() * 2) + 1;
					int mountaincheck2 = (int) Math.floor(Math.random() * 2) + 1;
					if (blocksez[i + 1][j + mountaincheck] != 0
							&& blocksez[i - 1][j + mountaincheck2] != 0) {
						blocksez[i][j] = empty;
					} else {
						blocksez[i][j] = 0;
					}
					break;
				case OCEAN:
					if (blocksez[i + 1][j + 1] != 0 && blocksez[i - 1][j + 1] != 0
							&& blocksez[i + 2][j + 1] != 0 && blocksez[i - 2][j + 1] != 0) {
						blocksez[i][j] = empty;
					} else {
						blocksez[i][j] = 0;
					}
					if (j < CHUNK_HEIGHT
							- (0.9 + (Math.pow((i - CHUNK_SIZE / 2.0) / (CHUNK_SIZE / 2.0), 2)
									+ (Math.random() - 0.5) / 5) / 10) * CHUNK_BOUNDARY_HEIGHT) {
						blocksez[i][j] = 0;
					}
					break;
				}
			} else {
				if (empty == 1) {
					if (biometype != BiomeType.MOUNTAIN) {
						if (j < CHUNK_HEIGHT * 30.0 / 100.0 * Math.random()) {
							blocksez[i][j] = 0;
						}
					}

					if (i <= 2) {
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
							if (blocksez[i][j + mountaincheck] != 0
									&& blocksez[i][j + mountaincheck2] != 0) {
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
					}
					if (i >= CHUNK_SIZE - 2) {
						switch (biometype) {
						case PLAIN:
							if (blocksez[i][j + 1] != 0 && blocksez[i - 1][j + 1] != 0) {
								blocksez[i][j] = empty;
							} else {
								blocksez[i][j] = 0;
							}
							break;
						case DESERT:
							if (blocksez[i][j + 1] != 0 && blocksez[i - 1][j + 1] != 0
									&& blocksez[i][j + 1] != 0 && blocksez[i - 2][j + 1] != 0) {
								blocksez[i][j] = empty;
							} else {
								blocksez[i][j] = 0;
							}
							break;
						case MOUNTAIN:
							int mountaincheck = (int) Math.floor(Math.random() * 2) + 1;
							int mountaincheck2 = (int) Math.floor(Math.random() * 2) + 1;
							if (blocksez[i][j + mountaincheck] != 0
									&& blocksez[i - 1][j + mountaincheck2] != 0) {
								blocksez[i][j] = empty;
							} else {
								blocksez[i][j] = 0;
							}
							break;
						case OCEAN:
							if (blocksez[i][j + 1] != 0 && blocksez[i - 1][j + 1] != 0
									&& blocksez[i][j + 1] != 0 && blocksez[i - 2][j + 1] != 0) {
								blocksez[i][j] = empty;
							} else {
								blocksez[i][j] = 0;
							}
							break;
						}
						if (j < CHUNK_HEIGHT - CHUNK_BOUNDARY_HEIGHT) {
							blocksez[i][j] = 0;
						}
					}
				}

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
			NavigableSet<Position> existingBlocks = generateRegion.blocks.navigableKeySet().subSet(start, true,
					end, true);
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