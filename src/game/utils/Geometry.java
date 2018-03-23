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

	public static org.dyn4j.geometry.Polygon convertShape(Polygon s) {
		Vector2[] verts = new Vector2[s.getPointCount()];
		for (int i = 0; i < verts.length; i++) {
			verts[i] = convert(new Vector2f(s.getPoint(i)));
		}
		return new org.dyn4j.geometry.Polygon(verts);
	}

	public static Vector2f convert(Vector2 v) {
		return new Vector2f((float) v.x, (float) v.y);
	}

	public static Vector2 convert(Vector2f v) {
		return new Vector2(v.x, v.y);
	}

	/**
	 * Takes a polygon, and replaces each vertex with "blunt" edges
	 *
	 * Diagram:
	 *
	 * @formatter:off
	 *       .
	 *      / \
	 *     /   \
	 *    /     \
	 *   .-------.
	 *
	 * Becomes:
	 *
	 *     .--.
	 *    /    \
	 *   /      \
	 *   |      |
	 *   .------.
	 * @formatter:on
	 * @param orig
	 * @return
	 */
	public static Polygon dampenEdges(Shape orig, float damping) {
		Polygon dampened = new Polygon();
		int totalPoints = orig.getPointCount();
		for (int i = 0; i < totalPoints; i++) {
			Vector2f p = new Vector2f(orig.getPoint(i));
			Vector2f left = p.copy().negate().add(
					new Vector2f(orig.getPoint((i + totalPoints - 1) % totalPoints)));
			Vector2f right = p.copy().negate().add(
					new Vector2f(orig.getPoint((i + 1) % totalPoints)));
			Vector2f leftPoint = calculateProjection(p, left, damping);
			Vector2f rightPoint = calculateProjection(p, right, damping);
			addPoint(dampened, leftPoint);
			addPoint(dampened, rightPoint);
		}
		return dampened;
	}

	private static Vector2f calculateProjection(Vector2f p, Vector2f direction,
			float amount) {
		return direction.copy().scale(amount / direction.length()).add(p);
	}

	public static Polygon addPoint(Polygon orig, Vector2f point) {
		orig.addPoint(point.x, point.y);
		return orig;
	}
}
