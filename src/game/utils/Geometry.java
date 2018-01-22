package game.utils;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

public class Geometry {
	public static Rectangle getBoundingBox(Shape s) {
		return new Rectangle(s.getMinX(), s.getMinY(), s.getWidth(), s.getHeight());
	}
}
