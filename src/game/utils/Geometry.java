package game.utils;

import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Polygon;
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

	public static Shape[] convertShape(Body b) {
		Shape[] shapes = new Shape[b.getFixtureCount()];
		int i = 0;
		for (Fixture f : b.getFixtures()) {
			shapes[i++] = convertShape(f.getShape(), b.getTransform());
		}
		return shapes;
	}

	public static Shape convertShape(Convex c, Transform t) {
		Vector2[] axes = c.getAxes(null, t);
		Polygon p = new Polygon();
		for (Vector2 point : axes) {
			p.addPoint((float) point.x, (float) point.y);
		}
		return p;
	}
}
