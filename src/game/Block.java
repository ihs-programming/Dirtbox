package game;

public class Block {
	public static final int BLOCK_SPRITE_SIZE=32;

	private Sprite s;
	
	public Block(int sx, int sy) {
		s = new Sprite(SpriteSheetLoader.getBlockImage(sx, sy));
	}
	
	public void draw(Viewport vp, float xpos, float ypos) {
		s.loc.set(xpos, ypos);
		vp.draw(s);
	}
}