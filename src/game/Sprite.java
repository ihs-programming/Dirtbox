package game;

import java.util.HashMap;

import org.lwjgl.util.Point;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Sprite {
	private float scaleFactor;
	private Image img;
	public Vector2f loc = new Vector2f();

	private HashMap<Point, Image> cache = new HashMap<>();

	public Sprite(Image img) {
		this.img = img;
		scaleFactor = 1;
	}

	public Rectangle getBoundingBox() {
		return new Rectangle(loc.x, loc.y, img.getWidth(), img.getHeight());
	}

	public void scale(float amount) {
		// note that negative scales invert the image
		scaleFactor *= amount;
	}

	public Sprite getScaledCopy() {
		Sprite copy = new Sprite(img);
		copy.scale(scaleFactor);
		return copy;
	}

	public Image getImage() {
		return img.getScaledCopy(scaleFactor);
	}

	public float getWidth() {
		return img.getWidth() * scaleFactor;
	}

	public float getHeight() {
		return img.getHeight() * scaleFactor;
	}
}
