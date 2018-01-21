package game.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
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

	public void collide(Shape hitbox) {
		if (hitbox instanceof Rectangle) {
			Rectangle boundingBox = (Rectangle) hitbox;
			Vector2f center = pos.copy();
			if (!boundingBox.contains(center.x, center.y)) {
				return;
			}

			Transform rotateRight = Transform.createRotateTransform((float) (Math.PI / 2),
					boundingBox.getCenterX(), boundingBox.getCenterY());

			Vector2f blockBoxCenter = new Vector2f(boundingBox.getCenter());
			// Rotate rectangle and check if center point should be pushed down
			for (int i = 0; i < 4; i++) {
				Vector2f lowerLeftCorner = new Vector2f(
						boundingBox.getMinX(), boundingBox.getMaxY());
				Vector2f lowerRightCorner = new Vector2f(
						boundingBox.getMaxX(), boundingBox.getMaxY());

				if (greaterThanLine(blockBoxCenter, lowerRightCorner, center) &&
						greaterThanLine(blockBoxCenter, lowerLeftCorner, center)) {
					center.y = boundingBox.getMaxY();
				}
				// rotate bounding box
				Shape s = boundingBox.transform(rotateRight);
				boundingBox = new Rectangle(
						s.getMinX(), s.getMinY(), s.getWidth(), s.getHeight());
				center = rotateRight.transform(center);
			}
			pos.set(center);
		} else {
			throw new UnsupportedOperationException(
					"Collision with non rectangles not implemented yet\n" +
							"	will result in undefined behavior\n");
		}
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
		return p.y < (l2.y - l1.y) / (l2.x - l1.x) * (p.x - l1.x);
	}
}
