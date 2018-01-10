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

import game.entities.Entity;

public class World {
	private final static int CHUNK_HEIGHT = 100;
	private final static int CHUNK_SIZE = 100;
	private final static int BEDROCK_LAYER = 100;

	private TreeMap<Position, Block> blocks = new TreeMap<>(new PositionComparator());
	private ArrayList<Entity> characters;

	public World() {
		generateWorld(0, 0);
		characters = new ArrayList<>();
		try {
			Entity stalin = new Entity(new Image("data/characters/stalin.jpg"), 1, 1, new Vector2f(0, 0));
			characters.add(stalin);
			stalin.magnify(.1f);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public void draw(Viewport vp) {
		Shape view = vp.getGameViewShape();
		Rectangle viewRect = new Rectangle(view.getMinX(), view.getMinY(), view.getWidth(), view.getHeight());
		generateRegion(viewRect);
		for (int i = (int) (viewRect.getMinX() - 1); i <= viewRect.getMaxX(); i++) {
			Position start = new Position(i, (int) (viewRect.getMinY() - 1));
			Position end = new Position(i, (int) (viewRect.getMaxY() + 1));
			NavigableSet<Position> existingBlocks = blocks.navigableKeySet().subSet(start, true, end, true);
			for (Position p : existingBlocks) {
				blocks.get(p).draw(vp);
			}
		}
		for (Entity e : this.characters) {
			e.draw(vp);
		}
	}

	private void generateRegion(Rectangle s) {
		for (int i = (int) (s.getMinX() - 1); i <= s.getMaxX() + 1; i++) {
			for (int j = (int) (s.getMinY() - 1); j <= s.getMaxY() + 1; j++) {
				generateWorld(i, j);
			}
		}
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

	/**
	 * TODO Improve world generation algorithm to have biomes and stuff
	 */
	public Block[][] generateChunk(int x, int y) {
		Block[][] blocks = new Block[CHUNK_SIZE][CHUNK_HEIGHT];
		int blocksez[][];
		blocksez = new int[CHUNK_SIZE][CHUNK_SIZE];
		for (int i = 0; i < CHUNK_SIZE; i++) {
			int depth = CHUNK_SIZE - 1;
			blocks[i][depth] = new SolidBlock(BlockType.STONE, (i + x) * Block.BLOCK_SPRITE_SIZE,
					(depth + y) * Block.BLOCK_SPRITE_SIZE);
			blocksez[i][depth] = 3;
		}
		for (int j = CHUNK_HEIGHT - 2; j >= 0; j--) {
			for (int i = 0; i < CHUNK_SIZE; i++) {
				int empty = (int) (Math.random() + j / (CHUNK_HEIGHT * 30.0 / 100.0));
				int blockType = 0;

				if (blocksez[i][j + 1] != 0) {
					blocksez[i][j] = empty;
					blockType = empty;
					if (empty - 1 >= Math.random() * 50) {
						blockType = 4;
						blocksez[i][j] = 4;
					}
					if (empty == 1 && !(i - 1 < 0) && !(i + 1 >= CHUNK_SIZE)) {
						if (blocksez[i + 1][j + 1] != 0 && blocksez[i - 1][j + 1] != 0) {
							blocksez[i][j] = empty;
							blockType = empty;
						} else {
							blocksez[i][j] = 0;
							blockType = 0;
						}
					}
				}

				if (blocksez[i][j + 1] == 5) {
					blocksez[i][j] = 0;
					blockType = 0;
				}
				if (blocksez[i][j + 1] == 1 && blocksez[i][j + 2] != 5 && blocksez[i][j + 1] != 0
						&& j < CHUNK_HEIGHT * 30.0 / 100.0 * Math.random()) {
					blocksez[i][j] = 5;
					blockType = 5;
				}
				if (blocksez[i][j + 1] == 0) {
					blocksez[i][j] = 0;
					blockType = 0;
				}

				BlockType type = BlockType.EMPTY;
				switch (blockType) {
				case 0:
					type = BlockType.EMPTY;
					break;
				case 1:
					type = BlockType.DIRT;
					break;
				case 2:
					if (Math.random() * j < 0.05 * Math.random() * CHUNK_HEIGHT) {
						type = BlockType.GRAVEL;
					} else {
						type = BlockType.STONE;
					}
					break;
				case 3:
					type = BlockType.STONE;
					break;
				case 4:
					if (CHUNK_HEIGHT - (j + 1) <= CHUNK_HEIGHT / 10.0 && Math.random() <= 0.3) {
						if (Math.random() < 0.4) {
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
					break;
				case 5:
					type = BlockType.GRASS;
					break;
				default:
					type = BlockType.UNDEFINED;
				}
				blocks[i][j] = new SolidBlock(type, (i + x) * Block.BLOCK_SPRITE_SIZE,
						(j + y) * Block.BLOCK_SPRITE_SIZE);
			}
		}
		return blocks;
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