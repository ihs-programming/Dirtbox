package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.entities.Entity;

public class World {
	private final static int CHUNK_SIZE = 100;
	private final static int BEDROCK_LAYER = 100;

	private Map<Position, Block> blocks = new HashMap<>();
	private ArrayList<Entity> characters;

	public World() {
		generateWorld(0, 0);
		characters = new ArrayList<>();
		try {
			Entity stalin = new Entity(new Image("data/characters/stalin.jpg"), 1, 1,
					new Vector2f(0, 0));
			characters.add(stalin);
			stalin.magnify(.1f);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public void draw(Viewport vp) {
		Shape view = vp.getGameViewShape();
		Rectangle viewRect = new Rectangle(view.getMinX(), view.getMinY(), view.getWidth(),
				view.getHeight());
		generateRegion(viewRect);
		for (Block b : blocks.values()) {
			b.draw(vp);
		}
		for (Entity e : this.characters) {
			e.draw(vp);
		}
	}

	private void generateRegion(Rectangle s) {
		for (int i = (int) (s.getMinX() - 1); i <= s.getMaxX() + 1; i++) {
			for (int j = (int) (s.getMinY() - 1); j <= s.getMaxY() + 1; j++) {
				Position curpos = new Position(i, j);
				if (blocks.containsKey(curpos)) {
					continue;
				}
				if (j >= BEDROCK_LAYER) {
					blocks.put(curpos, new SolidBlock(BlockType.UNDEFINED, i, j));
				} else if (j >= 0) {
					int chunkStart = i / CHUNK_SIZE * CHUNK_SIZE;
					if (i < 0) {
						chunkStart -= CHUNK_SIZE;
					}
					generateWorld(chunkStart, 0);
				} else {
					// Do not generate blocks in the air
				}
			}
		}
	}

	public void generateWorld(int x, int y) {
		Block[][] chunk = generateChunk(x, y);
		for (int i = 0; i < chunk.length; i++) {
			for (int j = 0; j < chunk[i].length; j++) {
				blocks.put(new Position(i + x, j + y), chunk[i][j]);
			}
		}
	}

	/**
	 * TODO Improve world generation algorithm to have biomes and stuff
	 */
	public Block[][] generateChunk(int x, int y) {
		Block[][] blocks = new Block[CHUNK_SIZE][CHUNK_SIZE];
		int blocksez[][];
		blocksez = new int[CHUNK_SIZE][CHUNK_SIZE];
		for (int i = 0; i < CHUNK_SIZE; i++) {
			int depth = CHUNK_SIZE - 1;
			blocks[i][depth] = new SolidBlock(BlockType.STONE, (i + x) * Block.BLOCK_SPRITE_SIZE,
					(depth + y) * Block.BLOCK_SPRITE_SIZE);
			blocksez[i][depth] = 3;
		}
		for (int j = 98; j >= 0; j--) {
			for (int i = 0; i < CHUNK_SIZE; i++) {
				int empty = (int) (Math.random() + j / 30.0);
				int blockType = 0;

				if (blocksez[i][j + 1] != 0) {
					blocksez[i][j] = empty;
					blockType = empty;
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
						&& j < 30.0 * Math.random()) {
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
					type = BlockType.GRAVEL;
					break;
				case 3:
					type = BlockType.STONE;
					break;
				case 4:
					type = BlockType.GOLD;
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