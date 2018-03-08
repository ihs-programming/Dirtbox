package game.blocks;

import java.awt.Point;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.MassType;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.SpriteSheetLoader;
import game.Viewport;
import game.utils.Geometry;

public abstract class Block {
	public static final int BLOCK_SPRITE_SIZE = 1;

	/**
	 * Used for debug purposes
	 */
	public static int draw_hit_count = 0;

	private Sprite sprite;
	private Vector2f pos;
	protected Body physicsBody;

	private int lighting;
	public final BlockType type;

	public static Block createBlock(BlockType type, float xpos, float ypos) {
		if (type == BlockType.EMPTY) {
			return new EmptyBlock(xpos, ypos);
		} else if (type == BlockType.WATER) {
			return new LiquidBlock(type, xpos, ypos);
		} else if (type == BlockType.WOOD || type == BlockType.LEAVES) {
			return new BackgroundBlock(type, xpos, ypos);
		}
		return new SolidBlock(type, xpos, ypos);
	}

	protected Block(BlockType type, int sx, int sy, float xpos, float ypos) {
		sprite = new Sprite(SpriteSheetLoader.getBlockImage(sx, sy));
		pos = new Vector2f(xpos, ypos);

		this.type = type;
	}

	public void draw(Viewport vp) {
		sprite.loc.set(pos.x, pos.y);
		if (this instanceof EmptyBlock) {
			return;
		}

		if (lighting > 0) {
			vp.draw(sprite);
			draw_hit_count++;
		}
	}

	public void drawShading(Viewport vp) {
		if (lighting != 63) {
			vp.fill(sprite.getBoundingBox(),
					new Color(0, 0, 0,
							255 - (int) ((1f - Viewport.gamma) * 255 * lighting / 63.0)));
		}
	}

	public BlockType getBlockType() {
		return this.type;
	}

	public Vector2f getPos() {
		return pos;
	}

	public Point getPointPos() {
		return new Point(Math.round(pos.x), Math.round(pos.y));
	}

	public Shape getHitbox() {
		return Geometry.convertShape(getBody())[0];
	}

	public int getLighting() {
		return lighting;
	}

	public void setLighting(int lighting) {
		this.lighting = lighting;
	}

	public Sprite getSprite() {
		return sprite.getCopy(); // ensure image can't be modified outside of this
									// class
	}

	public Body getBody() {
		if (physicsBody == null) {
			Convex c = new org.dyn4j.geometry.Rectangle(Block.BLOCK_SPRITE_SIZE,
					Block.BLOCK_SPRITE_SIZE);
			physicsBody = new Body();
			physicsBody.addFixture(c);
			physicsBody.translateToOrigin();
			float disp = BLOCK_SPRITE_SIZE / 2;
			disp = 0;
			physicsBody.translate(pos.x + disp, pos.y + disp);
			physicsBody.setMass(MassType.INFINITE);
		}
		return physicsBody;
	}
}
