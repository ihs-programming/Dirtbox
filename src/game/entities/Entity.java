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
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.Viewport;
import game.physics.PhysicsBody;
import game.utils.Geometry;
import game.world.World;

/**
 * Represents anything that is in the world
 */
public class Entity implements PhysicsBody {
	protected static final float GRAVITY = 0.00002613f;
	public static final float HITBOX_CORNER_INDENT = .01f;

	protected Sprite sprite;
	protected Vector2f prevPos = new Vector2f();
	protected Vector2f accel = new Vector2f();

	protected Polygon[] lastMovement = new Polygon[4];
	protected Body physicsBody;

	protected World world;

	public Entity(Sprite sprite, Vector2f pos, World w) {
		this.sprite = sprite.getCopy();
		world = w;

		physicsBody = new Body();
		physicsBody.setMass(new Mass(new Vector2(), 1, 1));
		physicsBody.setMassType(MassType.FIXED_ANGULAR_VELOCITY);
		generateHitbox();
	}

	public Entity(Image img, Vector2f pos, World w) {
		this(new Sprite(img), pos, w);
	}

	@Override
	public Body getBody() {
		return physicsBody;
	}

	private void generateHitbox() {
		float width = 0.95f * sprite.getWidth();
		float height = 0.99f * sprite.getHeight();

		Rectangle hitbox = new Rectangle(-width / 2, -height / 2, width, height);
		Polygon pruned = Geometry.dampenEdges(hitbox, .05f);

		Convex shape = Geometry.convertShape(pruned);
		shape.translate(sprite.getWidth() * 0.475, sprite.getHeight() * 0.5);
		physicsBody.removeAllFixtures();
		physicsBody.addFixture(shape, 1, 0, 0);
		physicsBody.setUserData(this);
	}

	public Shape getHitbox() {
		return Geometry.convertShape(physicsBody)[0];
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
				vp.draw(String.format("Velocity: %f %f",
						physicsBody.getLinearVelocity().x,
						physicsBody.getLinearVelocity().y), 20, 70, Color.white);
			}
		}
	}

	public void update(World w, float frametime) {
		prevPos.set(getLocation());
	}

	public Vector2f getLocation() {
		Vector2 v = physicsBody.getWorldCenter();
		return Geometry.convert(v);
	}

	public void setLocation(Vector2f loc) {
		Vector2f prevCent = getLocation();
		physicsBody.translate(Geometry.convert(prevCent.negate().add(loc)));
	}

	public Vector2f getVelocity() {
		return Geometry.convert(physicsBody.getLinearVelocity());
	}

	public void setVelocity(Vector2f v) {
		physicsBody.setLinearVelocity(Geometry.convert(v));
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