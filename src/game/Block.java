package game;

import org.newdawn.slick.geom.Vector2f;

public abstract class Block {
	public static final int BLOCK_SPRITE_SIZE=1;

	private Sprite s;
	private Vector2f pos;
	
	public Block(int blockid) {
		int sx=10;
		int sy=1;
		if (blockid==0){ // Empty
			sx=1;
			sy=1;
		}
		if (blockid==1){ // Dirt
			sx=2;
			sy=0;
		}
		if (blockid==3){ // Stone
			sx=1;
			sy=0;
		}
		if (blockid==2){ // Gravel
			sx=0;
			sy=0;
		}
		if (blockid==4){ // Gold
			sx=0;
			sy=2;
		}
		if (blockid==5){ // Grass
			sx=3;
			sy=0;
		}
	}

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