package game.blocks;

import java.awt.Point;

import org.dyn4j.dynamics.Body;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.SpriteSheetLoader;
import game.Viewport;
import game.physics.PhysicsBody;
import game.physics.PhysicsBodyFactory;
import game.utils.Geometry;

public abstract class Block implements PhysicsBody {
	public static final int BLOCK_SPRITE_SIZE = 1;

	/**
	 * Used for debug purposes
	 */
	public static int draw_hit_count = 0;

	private Point blockSpriteSheetLoc;

	private Vector2f pos;
	private PhysicsBodyFactory bodyFactory;
	private Body physicsBody;

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

	protected Block(BlockType type, int sx, int sy, float xpos, float ypos,
			PhysicsBodyFactory pbf) {
		blockSpriteSheetLoc = new Point(sx, sy);
		pos = new Vector2f(xpos, ypos);

		this.type = type;
		bodyFactory = pbf;
	}

	public void draw(Viewport vp) {
		if (this instanceof EmptyBlock) {
			return;
		}
		if (lighting > 0) {
			vp.draw(getSprite());
			draw_hit_count++;
		}
	}

	public Sprite getSprite() {
		Sprite s = new Sprite(SpriteSheetLoader.getBlockImage(blockSpriteSheetLoc.x,
				blockSpriteSheetLoc.y));
		s.loc.set(pos);
		return s;
	}

	public void drawShading(Viewport vp) {
		if (lighting != 63) {
			vp.fill(getSprite().getBoundingBox(),
					new Color(0, 0, 0,
							255 - (int) ((1f - Viewport.gamma) * 255 * lighting / 63.0)));
		}
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

	@Override
	public Body getBody() {
		if (physicsBody == null) {
			physicsBody = bodyFactory.createBody(type);
		}
		return physicsBody;
	}
}
