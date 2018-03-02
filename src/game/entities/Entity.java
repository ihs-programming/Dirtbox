package game.entities;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.Viewport;
import game.world.World;

/**
 * Represents anything that is in the world
 */
public class Entity {
	protected static final float GRAVITY = 0.00002613f;
	private static final boolean DEBUG_COLLISION = true;

	protected Sprite sprite;
	protected Vector2f prevPos = new Vector2f();
	protected Vector2f accel = new Vector2f();

	protected Polygon[] lastMovement = new Polygon[4];
	private Shape intersectionEdge;
	private Point scalefactor;
	private Body physicsBody;

	protected World world;

	public Entity(Sprite sprite, Vector2f pos, World w) {
		this.sprite = sprite.getCopy();
		world = w;
	}

	public Entity(Image img, Vector2f pos, World w) {
		this(new Sprite(img), pos, w);
	}

	public Body getBody() {
		if (physicsBody == null) {
			physicsBody = new Body();
			physicsBody.setMass(new Mass(new Vector2(), 1, 1));
			generateHitbox();
		}
		return physicsBody;
	}

	private void generateHitbox() {
		float width = 0.95f * sprite.getWidth();
		float height = 0.99f * sprite.getHeight();
		Convex shape = new org.dyn4j.geometry.Rectangle(width, height);
		physicsBody.removeAllFixtures();
		physicsBody.addFixture(shape);
		this.scalefactor = new Point(0.95f, 0.99f);
	}

	public Shape getHitbox() {
		Polygon p = (Polygon) physicsBody.getFixture(0).getShape();
		org.newdawn.slick.geom.Polygon poly = new org.newdawn.slick.geom.Polygon();
		for (int i = 0; i < p.getPoints().length; i++) {
			poly.addPoint(p.getPoint(i)[0], p.getPoint(i)[1]);
		}
		return poly;
	}

	public void draw(Viewport vp) {
		sprite.loc.set(getLocation());
		vp.draw(sprite);
	}

	public void update(World w, float frametime) {
		prevPos.set(getLocation());
	}

	public Vector2f getLocation() {
		Vector2 v = getBody().getWorldCenter();
		return convert(v);
	}

	public void setLocation(Vector2f loc) {
		Vector2f prevCent = convert(getBody().getWorldCenter());
		getBody().translate(convert(prevCent.negate().add(loc)));
	}

	public Vector2f getVelocity() {
		return convert(getBody().getLinearVelocity());
	}

	public void setVelocity(Vector2f v) {
		getBody().setLinearVelocity(convert(v));
	}

	private Vector2f convert(Vector2 v) {
		return new Vector2f((float) v.x, (float) v.y);
	}

	private Vector2 convert(Vector2f v) {
		return new Vector2(v.x, v.y);
	}

	protected void falldamage() {
	}

	/**
	 * Return false if the entity should be deleted.
	 *
	 * @return
	 */
	public boolean alive() {
		return true;
	}
}