package game.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public class ControllableCharacter extends Entity {
	private static final float SPEED = 0.0085f;
	private static final float JUMP = 0.012f;
	private static final float GRAVITY = 0.00002613f; // 1 block = 1 m^2, 1 block = 16 px,
														// 1 m =
	// 16 px,
	// 1 frame = 1/60s, 1 frame = 16.7ms,
	// 9.8m/s^2
	// = 9.8*16px/60frames, gravity =
	// -2.613px/frame

	public static final float BLOCK_MINE_TIME = 10.0f;

	public ControllableCharacter(Image spritesheet, int sheetwidth, int sheetheight,
			Vector2f pos) {
		super(spritesheet, sheetwidth, sheetheight, pos);
		accel.y = GRAVITY;
	}

	/**
	 * Command to move the character
	 *
	 * @param isLeft
	 */
	public void move(boolean isLeft) {
		if (isLeft) {
			vel.x = -SPEED;
		} else {
			vel.x = SPEED;
		}
	}

	/**
	 * Stops the character from moving (if he were moving)
	 */
	public void stopMoving() {
		vel.x = 0;
	}

	public void jump() {
		vel.y = -JUMP;
	}

	/**
	 * Note that currently the character just moves above the colliding hitbox
	 *
	 * @param hitbox
	 */
	public void collide(Shape hitbox) {
		Shape charHitbox = this.getHitbox();
		// Check if hitboxes actually should interact
		if (!(hitbox.contains(charHitbox) ||
				charHitbox.contains(hitbox) ||
				hitbox.intersects(charHitbox))) {
			return;
		}
		if (hitbox instanceof Point) {
			// Do nothing
			// (Point means that there is no hitbox)
		} else if (hitbox instanceof Rectangle) {
			Rectangle boundingBox = (Rectangle) hitbox;
			Vector2f displacement = new Vector2f();
			Vector2f corner = new Vector2f(charHitbox.getMinX(), charHitbox.getMaxY());
			Vector2f prevVel = prevPos.copy().negate().add(pos);
			if (prevVel.x > 0) {
				corner.x = charHitbox.getMaxX();
			}
			Vector2f prevCorner = corner.copy().sub(prevVel);
			Line l = new Line(prevCorner, corner);
			Line blockTop = new Line(
					boundingBox.getMinX(), boundingBox.getMinY(),
					boundingBox.getWidth(), 0f, false);
			if (l.intersects(blockTop)) {
				// push player up
				if (boundingBox.getMinY() < charHitbox.getMaxY()) {
					displacement.y = -(charHitbox.getMaxY() - boundingBox.getMinY());
					vel.y = Math.min(vel.y, 0);
				}
			} else {
				// collide from left to right
				if (prevCorner.x < boundingBox.getCenterX()) {
					// displacement.x -= charHitbox.getMaxX() - boundingBox.getMinX();
					// vel.x = Math.min(0f, vel.x);
				}
			}
			pos.add(displacement);
		} else {
			throw new UnsupportedOperationException(
					"Collision with non rectangles not implemented yet\n" +
							"	will result in undefined behavior\n");
		}
	}

}