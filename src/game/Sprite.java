package game;

import java.util.HashMap;

import org.lwjgl.util.Point;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Sprite {
	public Image img;
	public Vector2f loc = new Vector2f();

	private HashMap<Point, Image> cache = new HashMap<>();

	public Sprite(Image img) {
		this.img = img;
	}

	public Rectangle getBoundingBox() {
		return new Rectangle(loc.x, loc.y, img.getWidth(), img.getHeight());
	}

	public Image getCachedImage(int nw, int nh) {
		if (cache.containsKey(new Point(nw, nh))) {
			return cache.get(new Point(nw, nh));
		}
		Image ret = img.getScaledCopy(nw, nh);
		cache.put(new Point(nw, nh), ret);

		return ret;
	}
}
