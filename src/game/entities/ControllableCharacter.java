package game.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import game.utils.DefaultKeyListener;

public class ControllableCharacter extends Entity {
	private float speed = 0.01f;
	private float jumpStrength = 0.005f;
	private float gravity = 0.00001f;

	private Input userInput;

	public ControllableCharacter(Image spritesheet, int sheetwidth, int sheetheight,
			Vector2f pos, Input inp) {
		super(spritesheet, sheetwidth, sheetheight, pos);
		accel.y = gravity;
		userInput = inp;
		userInput.addKeyListener(new DefaultKeyListener() {
			@Override
			public void keyPressed(int key, char c) {
				if (key == Input.KEY_W) {
					vel.y = -jumpStrength;
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
			vel.x = -speed;
		}
		if (userInput.isKeyDown(Input.KEY_D)) {
			vel.x = speed;
		}
		super.update(frameTime);
	}

	public Vector2f getPosition() {
		return pos.copy();
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

	/**
	 * Rotates rectangle right by 90 degrees n times
	 *
	 * @param n
	 * @return
	 */
	private Rectangle rotateRectangle(int n, Rectangle r) {
		Transform rotate = Transform.createRotateTransform((float) (Math.PI / 2));
		for (int i = 0; i < n; i++) {
			Shape ns = r.transform(rotate);
			r = convertToRectangle(ns);
		}
		return r;
	}

	private Rectangle convertToRectangle(Shape s) {
		return new Rectangle(s.getMinX(), s.getMinY(), s.getWidth(), s.getHeight());
	}

	/**
	 * Check if Point p is higher than (greater y coordinate) than the line defined
	 * by l1 and l2.
	 *
	 * @param l1
	 * @param l2
	 * @param p
	 * @return
	 */
	private boolean greaterThanLine(Vector2f l1, Vector2f l2, Vector2f p) {
		return p.y > (l2.y - l1.y) / (l2.x - l1.x) * (p.x - l1.x);
	}
}
