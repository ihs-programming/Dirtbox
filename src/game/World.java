package game;

import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import game.entities.Entity;

public class World {
	private final static int DEBUG_WORLD_DEFAULT_SIZE = 100;
	private final static int DEBUG_WORLD_DEFAULT_HEIGHT = 100;

	Block[][] blocks = new Block[DEBUG_WORLD_DEFAULT_SIZE][DEBUG_WORLD_DEFAULT_HEIGHT];
	ArrayList<Entity> characters;

	public World() {
		generateWorld();
		characters = new ArrayList<>();
		try {
			Entity stalin = new Entity(new Image("data/characters/stalin.jpg"), 1, 1, new Vector2f(0, 0));
			characters.add(stalin);
			stalin.magnify(.1f);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void draw(Viewport vp) {
		for (Block[] block : blocks) {
			for (Block element : block) {
				element.draw(vp);
			}
		}
		for (Entity e : this.characters) {
			e.draw(vp);
		}
	}

	/**
	 * TODO Loads a file specifying the world map into the game
	 *
	 * @param filename
	 * @throws IOException
	 */
	public void loadFromFile(String filename) throws IOException {
	}

	/**
	 * TODO Improve world generation algorithm to have biomes and stuff
	 */
	public void generateWorld() {
		int blocksez[][];
		blocksez = new int[DEBUG_WORLD_DEFAULT_SIZE][DEBUG_WORLD_DEFAULT_HEIGHT];
		for (int i = 0; i < DEBUG_WORLD_DEFAULT_SIZE; i++) {
			int depth = DEBUG_WORLD_DEFAULT_HEIGHT - 1;
			blocks[i][depth] = new SolidBlock(BlockType.BEDROCK, i * Block.BLOCK_SPRITE_SIZE,
					depth * Block.BLOCK_SPRITE_SIZE);
			blocksez[i][depth] = 3;
		}
		for (int j = DEBUG_WORLD_DEFAULT_HEIGHT - 2; j >= 0; j--) {
			for (int i = 0; i < DEBUG_WORLD_DEFAULT_SIZE; i++) {
				int empty = (int) (Math.random() + j / (DEBUG_WORLD_DEFAULT_HEIGHT * 30.0 / 100.0));
				int blockType = 0;

				if (blocksez[i][j + 1] != 0) {
					blocksez[i][j] = empty;
					blockType = empty;
					if (empty - 1 >= Math.random() * 50) {
						blockType = 4;
						blocksez[i][j] = 4;
					}
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
						&& j < DEBUG_WORLD_DEFAULT_HEIGHT * 30.0 / 100.0 * Math.random()) {
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
					if (Math.random() * j < 0.05 * Math.random() * DEBUG_WORLD_DEFAULT_HEIGHT) {
						type = BlockType.GRAVEL;
					} else {
						type = BlockType.STONE;
					}
					break;
				case 3:
					type = BlockType.STONE;
					break;
				case 4:
					if (DEBUG_WORLD_DEFAULT_HEIGHT - (j + 1) <= DEBUG_WORLD_DEFAULT_HEIGHT / 10.0
							&& Math.random() <= 0.3) {
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
					/*
				BlockType type = BlockType.UNDEFINED;
				// @formatter:off
				BlockType[] typeMapping = new BlockType[] {
						BlockType.EMPTY, BlockType.DIRT, BlockType.GRAVEL,
						BlockType.STONE, BlockType.GOLD, BlockType.GRASS
				};
				// @formatter:on

				if (blockType >= 0 && blockType < typeMapping.length) {
					type = typeMapping[blockType];
				 */
				}
				blocks[i][j] = new SolidBlock(type, i * Block.BLOCK_SPRITE_SIZE, j * Block.BLOCK_SPRITE_SIZE);
			}
		}
	}
}
