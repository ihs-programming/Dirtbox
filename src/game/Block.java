package game;

import org.newdawn.slick.geom.Vector2f;

public abstract class Block {
	public static final int BLOCK_SPRITE_SIZE=1;

	private Sprite s;
	private Vector2f pos;
	
	public Block(int sx, int sy, float xpos, float ypos) {
		s = new Sprite(SpriteSheetLoader.getBlockImage(sx, sy));
		pos = new Vector2f(xpos, ypos);
	}
	
	public void draw(Viewport vp) {
		s.loc.set(pos.x, pos.y);
		vp.draw(s);
	}
	
	public Vector2f getPos() {
		return pos;
	}
}