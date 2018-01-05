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
				blocks[i][j].draw(vp,
						i*Block.BLOCK_SPRITE_SIZE,
						j*Block.BLOCK_SPRITE_SIZE);
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
		int blocksez[][];
		blocksez = new int[DEBUG_WORLD_DEFAULT_SIZE][DEBUG_WORLD_DEFAULT_SIZE];
		for (int i = 0; i < DEBUG_WORLD_DEFAULT_SIZE; i++) {
			blocks[i][DEBUG_WORLD_DEFAULT_SIZE-1] = new Block(3);
			blocksez[i][DEBUG_WORLD_DEFAULT_SIZE-1] = 3;
		}
		for (int j = 98; (j>=0); j--) {
			for (int i = 0; i < DEBUG_WORLD_DEFAULT_SIZE; i++) {
				
				int empty = (int) (Math.random()+(j/30.0));
				System.out.println(empty);
				
				blocks[i][j] = new Block(0);
				if (blocksez[i][j+1]!=(0)) {
					
					blocksez[i][j] = empty;
					blocks[i][j] = new Block(empty);
				}
				if (blocksez[i][j+1]==5) {
					blocksez[i][j]=0;
					blocks[i][j] = new Block(0);
				}
				if (blocksez[i][j+1]==(1)&&blocksez[i][j+2]!=(5)&&j<30.0*Math.random()) {
					blocksez[i][j] = 5;
					blocks[i][j] = new Block(5);
				}
				
				
			}
		}
	}
}
