package game;

import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import game.entities.Entity;

public class World {
	private final static int DEBUG_WORLD_DEFAULT_SIZE=100;
	
	Block[][] blocks = new Block[DEBUG_WORLD_DEFAULT_SIZE][DEBUG_WORLD_DEFAULT_SIZE];
	ArrayList<Entity> characters;
	
	public World() {
		generateWorld();
		characters = new ArrayList<Entity>();
		try {
			Entity stalin = new Entity(new Image("data/characters/stalin.jpg"), 1, 1, new Vector2f(0, 0));
			characters.add(stalin);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void draw(Viewport vp) {
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[i].length; j++) {
				blocks[i][j].draw(vp,
						i*Block.BLOCK_SPRITE_SIZE,
						j*Block.BLOCK_SPRITE_SIZE);
			}
		}
		for(Entity e : this.characters) {
			e.draw(vp);
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
				blocks[i][j] = new Block(empty, empty);
			}
		}
	}
}
