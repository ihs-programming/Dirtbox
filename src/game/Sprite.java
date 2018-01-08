package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Sprite {
	public Image img;
	public Vector2f loc = new Vector2f();

	public Sprite(Image img) {
		this.img = img;
	}

	public Rectangle getBoundingBox() {
		return new Rectangle(0, 0, img.getWidth(), img.getHeight());
	}
}
