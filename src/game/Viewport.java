package game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import game.utils.DefaultKeyListener;

public class Viewport implements DefaultKeyListener {
	private Graphics g;
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
		this.g = g;
	}

	public void draw(Sprite s) {
		Rectangle resultImageBox = s.getBoundingBox();
		Transform t = getDrawTransform();
		if (getViewShape().contains(resultImageBox.transform(t))) {
			Vector2f res = t.transform(s.loc.copy());
			int nw = (int) Math.ceil(s.img.getWidth() * scaleFactor);
			int nh = (int) Math.ceil(s.img.getHeight() * scaleFactor);
			g.drawImage(s.img.getScaledCopy(nw, nh), (int) res.x, (int) res.y);
		}
	}

	public void draw(Shape s) {
		Shape resultShape = s.transform(getDrawTransform());
		if (getViewShape().contains(resultShape)) {
			g.draw(s.transform(getDrawTransform()));
		}
	}

	private void printDebugInfo() {
		Transform t = getDrawTransform();

		System.out.println(getViewShape());
	}

	private Transform getDrawTransform() {
		Transform t = new Transform();

		// Note that the transforms are applied in reverse order
		// e.g. the first concatenated transform is applied last
		t.concatenate(
				Transform.createTranslateTransform(screenDimensions.x / 2, screenDimensions.y / 2));
		t.concatenate(Transform.createScaleTransform(scaleFactor, scaleFactor));
		t.concatenate(Transform.createTranslateTransform(-center.x, -center.y));
		return t;
	}

	private Transform getInverseDrawTransform() {
		Transform t = new Transform();

		// Note that the transforms are applied in reverse order
		// e.g. the first concatenated transform is applied last
		t.concatenate(Transform.createTranslateTransform(-center.x, -center.y));
		t.concatenate(Transform.createScaleTransform(scaleFactor, scaleFactor));
		t.concatenate(
				Transform.createTranslateTransform(screenDimensions.x / 2, screenDimensions.y / 2));
		return t;
	}

	public void setGraphics(Graphics g) {
		this.g = g;
	}

	public void update(int delta) {
		center.add(movement.copy().scale(delta / scaleFactor));
	}

	public void setScreenCenter(Vector2f center) {
		screenDimensions.set(center.copy().scale(2f));
	}

	public Shape getViewShape() {
		Transform t = new Transform();
		// t = t.concatenate(Transform.createScaleTransform(.5f, .5f));
		t.concatenate(getInverseDrawTransform());
		Rectangle viewRectangle = new Rectangle(0, 0, screenDimensions.x, screenDimensions.y);
		return viewRectangle.transform(t);
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
		}
	}
}
