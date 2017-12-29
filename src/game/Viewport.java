package game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

public class Viewport implements KeyListener {
	private Graphics g;
	private Vector2f center = new Vector2f();
	private Vector2f screenCenter = new Vector2f();
	private float scaleFactor = 1f;
	private Vector2f movement = new Vector2f();
	
	private static final float MOVEMENT_FACTOR = 1f;
	private static final float SCALE_INCREASE = 1.2f;
	private static final float SCALE_DECREASE = 1.0f/1.2f;
	
	public Viewport() {
		
	}

	public Viewport(Graphics g) {
		this.g = g;
	}
	
	public void draw(Sprite s) {
		Transform t = getDrawTransform();
		Vector2f res = t.transform(s.loc.copy());
		g.drawImage(s.img.getScaledCopy(scaleFactor), res.x + screenCenter.x, res.y + screenCenter.y);
	}
	
	public void draw(Shape s) {
		g.draw(s.transform(getDrawTransform()));
	}
	
	private Transform getDrawTransform() {
		Transform t = new Transform();
		t.concatenate(Transform.createScaleTransform(scaleFactor, scaleFactor));
		t.concatenate(Transform.createTranslateTransform(-center.x, -center.y));
		return t;
	}
	
	public void setGraphics(Graphics g) {
		this.g = g;
	}
	
	public void update(int delta) {
		center.add(movement.copy().scale(delta/scaleFactor));
	}
	
	public void setScreenCenter(Vector2f center) {
		screenCenter.set(center);
	}

	@Override
	public void inputEnded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAcceptingInput() {
		return true;
	}

	@Override
	public void setInput(Input input) {
		// TODO Auto-generated method stub
		
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
