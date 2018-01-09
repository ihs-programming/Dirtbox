package game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import game.utils.DefaultKeyListener;

public class Viewport implements DefaultKeyListener {
	private Graphics g;
	private Vector2f center = new Vector2f(); // in game units
	private Vector2f screenCenter = new Vector2f(); // in pixels
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
		Transform t = getDrawTransform();
		Vector2f res = t.transform(s.loc.copy());
		g.drawImage(s.img.getScaledCopy(scaleFactor), res.x, res.y);
	}

	private void printDebugInfo() {
		Transform t = getDrawTransform();

		System.out.printf("Center: %s\n", center.toString());
		System.out.printf("Screen center: %s\n", screenCenter.toString());
		System.out.printf("Scale factor: %f", scaleFactor);
		System.out.println(t.transform(new Vector2f(1, 1)));
	}

	public void draw(Shape s) {
		g.draw(s.transform(getDrawTransform()));
	}

	private Transform getDrawTransform() {
		Transform t = new Transform();

		// Note that the transforms are applied in reverse order
		// e.g. the first concatenated transform is applied last
		t.concatenate(Transform.createTranslateTransform(screenCenter.x, screenCenter.y));
		t.concatenate(Transform.createScaleTransform(scaleFactor, scaleFactor));
		t.concatenate(Transform.createTranslateTransform(-center.x, -center.y));
		return t;
	}

	public void setGraphics(Graphics g) {
		this.g = g;
	}

	public void update(int delta) {
		center.add(movement.copy().scale(delta / scaleFactor));
	}

	public void setScreenCenter(Vector2f center) {
		screenCenter.set(center);
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
