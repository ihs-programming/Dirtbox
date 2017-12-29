package game;

import org.newdawn.slick.geom.Vector2f;

public class Block {
	public static final int BLOCK_SPRITE_SIZE=1;

	private Sprite s;
	private Vector2f pos;
	
	public Block(int sx, int sy) {
		s = new Sprite(SpriteSheetLoader.getBlockImage(sx, sy));
	}
	
	public void draw(Viewport vp, float xpos, float ypos) {
		s.loc.set(xpos, ypos);
		vp.draw(s);
	}
}