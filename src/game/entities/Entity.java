package game.entities;

import org.dyn4j.Listener;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.Viewport;
import game.utils.Geometry;
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
			physicsBody.setMassType(MassType.FIXED_ANGULAR_VELOCITY);
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
	}

	public Shape getHitbox() {
		return Geometry.convertShape(getBody())[0];
	}

	public void draw(Viewport vp) {
		sprite.loc.set(getLocation());
		vp.draw(sprite);
		if (Viewport.DEBUG_MODE) {
			vp.draw(getHitbox(), Color.red);
			if (this instanceof ControllableCharacter) {
				vp.draw(String.format("Hitbox shape position: %f %f",
						getHitbox().getCenterX(),
						getHitbox().getCenterY()), 20, 50, Color.white);
				vp.draw(String.format("Velocity: %f %f", getBody().getLinearVelocity().x,
						getBody().getLinearVelocity().y), 20, 70, Color.white);
			}
		}
	}

	public void update(World w, float frametime) {
		prevPos.set(getLocation());
	}

	public Vector2f getLocation() {
		Vector2 v = getBody().getWorldCenter();
		return convert(v);
	}

	public void setLocation(Vector2f loc) {
		Vector2f prevCent = getLocation();
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

	public Listener getPhysicsListener() {
		return new Listener() {
		};
	}
}