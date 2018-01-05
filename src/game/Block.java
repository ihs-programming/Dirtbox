package game;

import org.newdawn.slick.geom.Vector2f;

public class Block {
	public static final int BLOCK_SPRITE_SIZE=1;

	private Sprite s;
	private Vector2f pos;
	
	public Block(int blockid) {
		int sx=10;
		int sy=1;
		if (blockid==0){
			sx=1;
			sy=1;
		}
		if (blockid==1){
			sx=2;
			sy=0;
		}
		if (blockid==3){
			sx=1;
			sy=0;
		}
		if (blockid==2){
			sx=0;
			sy=0;
		}
		if (blockid==4){
			sx=0;
			sy=2;
		}
		if (blockid==5){
			sx=3;
			sy=0;
		}
		s = new Sprite(SpriteSheetLoader.getBlockImage(sx, sy));
	}
	
	public void draw(Viewport vp, float xpos, float ypos) {
		s.loc.set(xpos, ypos);
		vp.draw(s);
	}
}