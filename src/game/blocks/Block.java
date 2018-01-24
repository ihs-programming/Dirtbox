package game.blocks;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
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

	private int lighting;
	private Image baseImage;

	public static Block createBlock(BlockType type, float xpos, float ypos) {
		if (type == BlockType.EMPTY) {
			return new EmptyBlock(xpos, ypos);
		} else if (type == BlockType.WATER) {
			return new LiquidBlock(type, xpos, ypos);
		}
		return new SolidBlock(type, xpos, ypos);
	}

	protected Block(int sx, int sy, float xpos, float ypos) {
		baseImage = SpriteSheetLoader.getBlockImage(sx, sy);
		sprite = new Sprite(baseImage);
		pos = new Vector2f(xpos, ypos);
	}

	public void draw(Viewport vp) {
		sprite.loc.set(pos.x, pos.y);
		if (lighting > 0) {
			vp.draw(sprite);
		}
		vp.fill(sprite.getBoundingBox(),
				new Color(0, 0, 0,
						255 - (int) ((1f - Viewport.gamma) * 255 * lighting / 64.0)));
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

	public int getLighting() {
		return lighting;
	}

	public void setLighting(int lighting) {
		this.lighting = lighting;
	}
}