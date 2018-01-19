package game;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import game.utils.DefaultKeyListener;

/**
 * Handles all drawing in the game. Does not, and should not handle ui drawing
 *
 * Useful because it allows the position of the "camera" (viewport) to move
 * around
 */
public class Viewport implements DefaultKeyListener, MouseListener {
	private Graphics graphics;
	static Vector2f center = new Vector2f(); // in game units
	private Vector2f screenDimensions = new Vector2f(); // in pixels
	private float scaleFactor = 1f;
	private Vector2f movement = new Vector2f();

	private static final float MOVEMENT_FACTOR = 1f;
	private static final float SCALE_INCREASE = 1.2f;
	private static final float SCALE_DECREASE = 1.0f / 1.2f;

	public static boolean DEBUG_MODE = false;

	public Viewport() {

	}

	public Viewport(Graphics g) {
		this.graphics = g;
	}

	public void draw(Sprite s) {
		Transform t = getDrawTransform();

		// Check if the sprite needs to be drawn
		Shape resultImageBox = s.getBoundingBox().transform(t);
		if (getViewShape().contains(resultImageBox)
				|| getViewShape().intersects(resultImageBox)
				|| resultImageBox.contains(getViewShape())) {
			Vector2f res = t.transform(s.loc.copy());
			int nw = (int) Math.ceil(s.img.getWidth() * scaleFactor);
			int nh = (int) Math.ceil(s.img.getHeight() * scaleFactor);
			graphics.drawImage(s.img.getScaledCopy(nw, nh), (int) res.x, (int) res.y);
		}
	}

	public void draw(Shape s) {
		Shape resultShape = s.transform(getDrawTransform());

		// Check if the sprite needs to be drawn
		if (getViewShape().contains(resultShape)) {
			graphics.draw(s.transform(getDrawTransform()));
		}
	}

	private void printDebugInfo() {
		if (DEBUG_MODE) {
			System.out.println("Debug button pressed, debug mode OFF");
			DEBUG_MODE = !DEBUG_MODE;
		} else {
			System.out.println("Debug button pressed, debug mode ON");
			DEBUG_MODE = !DEBUG_MODE;
		}
	}

	public void setGraphics(Graphics g) {
		this.graphics = g;
	}

	public void update(int delta) {
		double darknessvalue = 0.6 + Math
				.sin(2.0 * Math.PI * System.currentTimeMillis()
						/ World.DAY_NIGHT_DURATION)
				* 0.4;
		Color BackgroundColor = new Color((int) (darknessvalue * 0),
				(int) (darknessvalue * 127), (int) (darknessvalue * 255));
		graphics.setBackground(BackgroundColor);
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
	 * @return transform mapping game position to screen position
	 */
	private Transform getDrawTransform() {
		Transform t = new Transform();

		// Note that the transforms are applied in reverse order
		// e.g. the first concatenated transform is applied last
		Transform[] trans = new Transform[] {
				Transform.createTranslateTransform(screenDimensions.x / 2,
						screenDimensions.y / 2),
				Transform.createScaleTransform(scaleFactor, scaleFactor),
				Transform.createTranslateTransform(-center.x, -center.y) };

		for (Transform ts : trans) {
			t.concatenate(ts);
		}
		return t;
	}

	/**
	 * Note that this method implicitly depends on getDrawTransform (if this method
	 * is changed, likely so should getDrawTransform)
	 *
	 * @return transform mapping screen position to game position
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

	public void zoom(float factor) {
		scaleFactor *= factor;
	}

	public void setZoom(float factor) {
		scaleFactor = factor;
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

	@Override
	public void mouseWheelMoved(int change) {
		if (change < 0) {
			scaleFactor *= SCALE_DECREASE;
		} else {
			scaleFactor *= SCALE_INCREASE;
		}
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		// TODO Auto-generated method stub

	}
}
