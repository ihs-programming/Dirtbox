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
			shapes[i++] = convertShape(f.getShape(), b.getInitialTransform());
		}
		return shapes;
	}

	public static Shape convertShape(Convex c, Transform t) {
		if (!(c instanceof org.dyn4j.geometry.Polygon)) {
			throw new UnsupportedOperationException();
		}
		org.dyn4j.geometry.Polygon p = (org.dyn4j.geometry.Polygon) c;
		Vector2[] vertices = p.getVertices();
		Polygon np = new Polygon();
		for (Vector2 point : vertices) {
			Vector2 pointCopy = point.copy();
			t.transform(pointCopy);
			np.addPoint((float) pointCopy.x, (float) pointCopy.y);
		}
		return np;
	}

	public static Vector2f convert(Vector2 v) {
		return new Vector2f((float) v.x, (float) v.y);
	}

	public static Vector2 convert(Vector2f v) {
		return new Vector2(v.x, v.y);
	}
}
