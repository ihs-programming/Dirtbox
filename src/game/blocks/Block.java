package game.blocks;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.SpriteSheetLoader;
import game.Viewport;

public class Block {
	public static final int BLOCK_SPRITE_SIZE = 1;

	private Sprite sprite;
	private Vector2f pos;

	protected Block(int sx, int sy, float xpos, float ypos) {
		sprite = new Sprite(SpriteSheetLoader.getBlockImage(sx, sy));
		pos = new Vector2f(xpos, ypos);
	}

	public void draw(Viewport vp) {
		sprite.loc.set(pos.x, pos.y);
		vp.draw(sprite);
	}

	public Vector2f getPos() {
		return pos;
	}

	public Shape getHitbox() {
		Rectangle hitbox = sprite.getBoundingBox();
		hitbox.setX(pos.x);
		hitbox.setY(pos.y);
		return hitbox;
	}
}