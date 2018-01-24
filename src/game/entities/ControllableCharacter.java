package game.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.utils.DefaultKeyListener;

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

	private Input userInput;

	public ControllableCharacter(Image spritesheet, int sheetwidth, int sheetheight,
			Vector2f pos, Input inp) {
		super(spritesheet, sheetwidth, sheetheight, pos);
		accel.y = GRAVITY;
		userInput = inp;
		userInput.addKeyListener(new DefaultKeyListener() {
			@Override
			public void keyPressed(int key, char c) {
				if (key == Input.KEY_W) {
					vel.y = -JUMP;
				}
			}

			@Override
			public void keyReleased(int key, char c) {
			}
		});
	}

	@Override
	public void update(float frameTime) {
		vel.x = 0;
		if (userInput.isKeyDown(Input.KEY_A)) {
			vel.x = -SPEED;
		}
		if (userInput.isKeyDown(Input.KEY_D)) {
			vel.x = SPEED;
		}
		super.update(frameTime);
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
			// push player up
			if (boundingBox.getMinY() < charHitbox.getMaxY()) {
				displacement.y -= charHitbox.getMaxY() - boundingBox.getMinY();
				vel.y = Math.min(vel.y, 0);
			}
			pos.add(displacement);
		} else {
			throw new UnsupportedOperationException(
					"Collision with non rectangles not implemented yet\n" +
							"	will result in undefined behavior\n");
		}
	}
}