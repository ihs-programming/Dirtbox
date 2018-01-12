package game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import game.utils.DefaultKeyListener;

public class Viewport implements DefaultKeyListener {
	private Graphics graphics;
	private Vector2f center = new Vector2f(); // in game units
	private Vector2f screenDimensions = new Vector2f(); // in pixels
	private float scaleFactor = 1f;
	private Vector2f movement = new Vector2f();

	private static final float MOVEMENT_FACTOR = 1f;
	private static final float SCALE_INCREASE = 1.2f;
	private static final float SCALE_DECREASE = 1.0f / 1.2f;

	public Viewport() {

	}

	public Viewport(Graphics g) {
		this.graphics = g;
	}

	public void draw(Sprite s) {
		Transform t = getDrawTransform();
		Shape resultImageBox = s.getBoundingBox().transform(t);
		if (getViewShape().contains(resultImageBox) || getViewShape().intersects(resultImageBox)
				|| resultImageBox.contains(getViewShape())) {
			Vector2f res = t.transform(s.loc.copy());
			int nw = (int) Math.ceil(s.img.getWidth() * scaleFactor);
			int nh = (int) Math.ceil(s.img.getHeight() * scaleFactor);
			graphics.drawImage(s.img.getScaledCopy(nw, nh), (int) res.x, (int) res.y);
		}
	}

	public void draw(Shape s) {
		Shape resultShape = s.transform(getDrawTransform());
		if (getViewShape().contains(resultShape)) {
			graphics.draw(s.transform(getDrawTransform()));
		}
	}

	private void printDebugInfo() {
		System.out.println("debug button pressed");
	}

	public void setGraphics(Graphics g) {
		this.graphics = g;
	}

	public void update(int delta) {
		center.add(movement.copy().scale(delta / scaleFactor));
	}

	public void setScreenCenter(Vector2f center) {
		screenDimensions.set(center.copy().scale(2f));
	}

	public Shape getViewShape() {
		return new Rectangle(0, 0, screenDimensions.x, screenDimensions.y);
	}

	public Shape getGameViewShape() {
		return getViewShape().transform(getInverseDrawTransform());
	}

	/**
	 * Note that this method implicitly depends on getInverseDrawTransform (if this
	 * method is changed, likely so should getInverseDrawTransform).
	 *
	 * @return
	 */
	private Transform getDrawTransform() {
		Transform t = new Transform();

		// Note that the transforms are applied in reverse order
		// e.g. the first concatenated transform is applied last
		Transform[] trans = new Transform[] {
				Transform.createTranslateTransform(screenDimensions.x / 2, screenDimensions.y / 2),
				Transform.createScaleTransform(scaleFactor, scaleFactor),
				Transform.createTranslateTransform(-center.x, -center.y) };

		for (Transform ts : trans) {
			t.concatenate(ts);
		}
		return t;
	}

	public void zoom(float factor) {
		scaleFactor *= factor;
	}

	public void setZoom(float factor) {
		scaleFactor = factor;
	}

	/**
	 * Note that this method implicitly depends on getDrawTransform (if this method
	 * is changed, likely so should getDrawTransform)
	 *
	 * @return
	 */
	private Transform getInverseDrawTransform() {
		Transform t = new Transform();

		// Inverted order and transformation of getDrawTransform
		Transform[] trans = new Transform[] {
				Transform.createTranslateTransform(-screenDimensions.x / 2,
						-screenDimensions.y / 2),
				Transform.createScaleTransform(1f / scaleFactor, 1f / scaleFactor),
				Transform.createTranslateTransform(center.x, center.y) };

		for (int i = trans.length - 1; i >= 0; i--) {
			t.concatenate(trans[i]);
		}
		return t;
	}

	@Override
	public void keyPressed(int key, char c) {
		switch (key) {
		case Input.KEY_UP:
			movement.y -= MOVEMENT_FACTOR;
			break;
		case Input.KEY_DOWN:
			movement.y += MOVEMENT_FACTOR;
			break;
		case Input.KEY_RIGHT:
			movement.x += MOVEMENT_FACTOR;
			break;
		case Input.KEY_LEFT:
			movement.x -= MOVEMENT_FACTOR;
			break;
		case Input.KEY_MINUS:
			scaleFactor *= SCALE_DECREASE;
			break;
		case Input.KEY_EQUALS:
			scaleFactor *= SCALE_INCREASE;
			break;
		case Input.KEY_P:
			printDebugInfo();
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(int key, char c) {
		switch (key) {
		case Input.KEY_UP:
			movement.y += MOVEMENT_FACTOR;
			break;
		case Input.KEY_DOWN:
			movement.y -= MOVEMENT_FACTOR;
			break;
		case Input.KEY_RIGHT:
			movement.x -= MOVEMENT_FACTOR;
			break;
		case Input.KEY_LEFT:
			movement.x += MOVEMENT_FACTOR;
			break;
		default:
			break;
		}
	}
}
