package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import game.entities.Entity;

public class World {
	private final static int DEBUG_WORLD_DEFAULT_SIZE = 100;

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
		for (Block b : blocks.values()) {
			b.draw(vp);
		}
		for (Entity e : this.characters) {
			e.draw(vp);
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
		Block[][] blocks = new Block[DEBUG_WORLD_DEFAULT_SIZE][DEBUG_WORLD_DEFAULT_SIZE];
		int blocksez[][];
		blocksez = new int[DEBUG_WORLD_DEFAULT_SIZE][DEBUG_WORLD_DEFAULT_SIZE];
		for (int i = 0; i < DEBUG_WORLD_DEFAULT_SIZE; i++) {
			int depth = DEBUG_WORLD_DEFAULT_SIZE - 1;
			blocks[i][depth] = new SolidBlock(BlockType.STONE, (i + x) * Block.BLOCK_SPRITE_SIZE,
					(depth + y) * Block.BLOCK_SPRITE_SIZE);
			blocksez[i][depth] = 3;
		}
		for (int j = 98; j >= 0; j--) {
			for (int i = 0; i < DEBUG_WORLD_DEFAULT_SIZE; i++) {
				int empty = (int) (Math.random() + j / 30.0);
				int blockType = 0;

				if (blocksez[i][j + 1] != 0) {
					blocksez[i][j] = empty;
					blockType = empty;
					if (empty == 1 && !(i - 1 < 0) && !(i + 1 >= DEBUG_WORLD_DEFAULT_SIZE)) {
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
}