package game.utils;

import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public class Geometry {
	public static Rectangle getBoundingBox(Shape s) {
		return new Rectangle(s.getMinX(), s.getMinY(), s.getWidth(), s.getHeight());
	}

	public static Circle createCircle(Vector2f center, float radius) {
		return new Circle(center.x, center.y, radius);
	}
}
