package game;

import java.awt.Point;
import java.util.ArrayList;
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

/**
 * Contains all objects in the game
 *
 * e.g. Blocks, People, maybe in the future animals, etc.
 */
public class World {
	private static final int CHUNK_HEIGHT = 255;
	private static final int CHUNK_SIZE = 127;
	private static final int BEDROCK_LAYER = 255;

	private TreeMap<Point, Block> blocks = new TreeMap<>((p1, p2) -> {
		if (p1.x == p2.x) {
			return p1.y - p2.y;
		}
		return p1.x - p2.x;
	});
	private ArrayList<Entity> characters;

	public World() {
		generateWorld(0, 0);
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
		generateRegion(viewRect);
		for (int i = (int) (viewRect.getMinX() - 1); i <= viewRect.getMaxX(); i++) {
			Point start = new Point(i, (int) (viewRect.getMinY() - 1));
			Point end = new Point(i, (int) (viewRect.getMaxY() + 1));
			NavigableSet<Point> existingBlocks = blocks.navigableKeySet().subSet(start, true, end,
					true);
			for (Point p : existingBlocks) {
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
			Block[][] chunk = generateChunk(chunkStart, 0);
			for (int i = 0; i < chunk.length; i++) {
				for (int j = 0; j < chunk[i].length; j++) {
					blocks.put(new Point(i + chunkStart, j), chunk[i][j]);
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
		BlockType blocksenum[][] = new BlockType[CHUNK_SIZE][CHUNK_HEIGHT];
		blocksez = new int[CHUNK_SIZE][CHUNK_HEIGHT];
		for (int i = 0; i < CHUNK_SIZE; i++) {
			int depth = CHUNK_HEIGHT - 1;
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
					if (empty - 1 >= Math.random() * 30.0) {
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
				blocksenum[i][j] = BlockType.EMPTY;
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
					if (i + 1 < CHUNK_SIZE && i - 1 >= 0 && j + 1 < CHUNK_HEIGHT && j - 1 >= 0) {
						for (int blocksearch = 0; blocksearch < 8; blocksearch++) {
							int looky = (int) Math.round(Math.random() * 2 - 1);
							int lookx = (int) Math.round(Math.random() * 2 - 1);
							if (blocksez[i + lookx][j + looky] == 4) {
								type = blocksenum[i + lookx][j + looky];
							}
						}
					}

					break;
				case 4:
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
					break;
				case 5:
					type = BlockType.GRASS;
					break;
				default:
					type = BlockType.UNDEFINED;
				}
				blocksenum[i][j] = type;
				blocks[i][j] = new SolidBlock(type, (i + x) * Block.BLOCK_SPRITE_SIZE,
						(j + y) * Block.BLOCK_SPRITE_SIZE);
			}
		}
		return blocks;
	}
}
