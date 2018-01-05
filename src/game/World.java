package game;

import java.io.IOException;

public class World {
	private final static int DEBUG_WORLD_DEFAULT_SIZE=100;
	
	Block[][] blocks = new Block[DEBUG_WORLD_DEFAULT_SIZE][DEBUG_WORLD_DEFAULT_SIZE];
	
	public World() {
		generateWorld();
	}
	
	public void draw(Viewport vp) {
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[i].length; j++) {
				blocks[i][j].draw(vp);
			}
		}
	}
	
	/**
	 * TODO
	 * Loads a file specifying the world map into the game
	 * @param filename
	 * @throws IOException
	 */
	public void loadFromFile(String filename) throws IOException {
	}
	
	/**
	 * TODO
	 * Improve world generation algorithm to have biomes and stuff
	 */
	public void generateWorld() {
		for (int i = 0; i < DEBUG_WORLD_DEFAULT_SIZE; i++) {
			for (int j = 0; j < DEBUG_WORLD_DEFAULT_SIZE; j++) {
				int empty = (int) (Math.random() + .5);
				BlockType t = BlockType.Dirt;
				switch(empty) {
				case 0: t = BlockType.Stone;
				}
				blocks[i][j] = new SolidBlock(t, i, j);
			}
		}
	}
}
