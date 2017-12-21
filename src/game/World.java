package game;

public class World implements Drawable {
	private final static int DEBUG_WORLD_DEFAULT_SIZE=8;
	
	Block[][] blocks = new Block[DEBUG_WORLD_DEFAULT_SIZE][DEBUG_WORLD_DEFAULT_SIZE];
	
	public World() {
		for (int i = 0; i < DEBUG_WORLD_DEFAULT_SIZE; i++) {
			for (int j = 0; j < DEBUG_WORLD_DEFAULT_SIZE; j++) {
				blocks[i][j] = new Block(0, 0);
			}
		}
	}
	
	@Override
	public void draw(Viewport vp) {
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[i].length; j++) {
				blocks[i][j].draw(vp,
						i*Block.BLOCK_SPRITE_SIZE,
						j*Block.BLOCK_SPRITE_SIZE);
			}
		}
	}
}
