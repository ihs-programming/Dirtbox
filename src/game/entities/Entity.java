package game.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
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

	private Shape hitbox;
	protected Sprite sprite;
	protected Vector2f pos = new Vector2f();
	protected Vector2f prevPos = new Vector2f();
	protected Vector2f vel = new Vector2f();
	protected Vector2f accel = new Vector2f();

	protected Polygon[] lastMovement = new Polygon[4];
	private Shape intersectionEdge;
	private Point scalefactor;

	protected World world;

	public Entity(Sprite sprite, Vector2f pos, World w) {
		this.pos = pos.copy();
		this.sprite = sprite.getCopy();
		world = w;
		generateHitbox();
	}

	public Entity(Image img, Vector2f pos, World w) {
		this(new Sprite(img), pos, w);
	}

	public Shape getHitbox() {
		if (this.hitbox == null) {
			generateHitbox();
		} else {
			hitbox.setX(pos.x + (1 - scalefactor.getX()) * hitbox.getWidth());
			hitbox.setY(pos.y + (1 - scalefactor.getY()) * hitbox.getHeight());
		}
		return this.hitbox;
	}

	private void generateHitbox() {
		float width = 0.95f * sprite.getWidth();
		float height = 0.99f * sprite.getHeight();
		this.hitbox = new Rectangle(
				pos.x + 0.025f * width, pos.y + 0.01f * height, width, height);
		this.scalefactor = new Point(0.95f, 0.99f);
	}

	public void draw(Viewport vp) {
		sprite.loc.set(pos);
		vp.draw(sprite);
		if (Viewport.DEBUG_MODE && Entity.DEBUG_COLLISION) {
			renderMovement(vp);
		}
	}

	private void renderMovement(Viewport vp) {
		vp.fill(Geometry.createCircle(new Vector2f(hitbox.getCenter()), .2f),
				Color.pink);
		if (lastMovement != null) {
			Color c = new Color(255, 0, 0);
			int height = 50;
			for (Polygon element : lastMovement) {
				if (element != null) {
					vp.draw(element, c);
					if (this instanceof ControllableCharacter) {
						vp.draw(String.format("Element location: %f %f",
								element.getMinX(),
								element.getMinY()), 10, height, Color.white);
						height += 20;
					}
				}
				if (intersectionEdge != null) {
					vp.draw(intersectionEdge, Color.orange);
				}
			}
		}
	}

	public void update(World w, float frametime) {
		prevPos.set(pos);
		pos.add(vel.copy().scale(frametime));
		vel.add(accel.copy().scale(frametime));
		hitbox.setCenterX(pos.x);
		hitbox.setCenterY(pos.y);
	}

	public Vector2f getLocation() {
		float width = sprite.getWidth();
		float height = sprite.getHeight();
		return pos.copy().add(new Vector2f(width / 2, height / 2));
	}

	/**
	 * Note that currently the character just moves above the colliding hitbox
	 *
	 * @param hitbox
	 */
	public void collide(Shape hitbox) {
		Shape charHitbox = this.getHitbox();
		// Check if hitboxes actually should interact
		if (!(hitbox.contains(charHitbox) ||
				charHitbox.contains(hitbox) ||
				hitbox.intersects(charHitbox))) {
			return;
		}
		if (hitbox instanceof Point) {
			// Do nothing
			// (Point means that there is no hitbox)
		} else if (hitbox instanceof Rectangle) {
			float[] displacement = new float[2];
			Vector2f prevDirection = pos.copy().sub(prevPos).negate();
			float charPoints[] = { charHitbox.getMinX(), charHitbox.getMinY(),
					charHitbox.getMaxX(), charHitbox.getMaxY() };
			float hitboxPoints[] = { hitbox.getMinX(), hitbox.getMinY(), hitbox.getMaxX(),
					hitbox.getMaxY() };
			// 0 is lower edge
			int[] collisionOrder = { 1, 3, 0, 2 };
			for (int j = 0; j < 4; j++) {
				int i = collisionOrder[j];
				// edgeMovement represents area that an edge of the entity
				// hitbox passes
				// through
				Polygon edgeMovement = new Polygon();
				edgeMovement.addPoint(charPoints[i % 4], charPoints[(i + 3) % 4]);
				edgeMovement.addPoint(charPoints[(i + 2) % 4], charPoints[(i + 3) % 4]);

				Vector2f prevDisplacement = prevDirection;
				if (i % 2 == 1) {
					// Swaps x and y coordinate
					float[] prevVal = { prevDirection.x, prevDirection.y };
					prevDisplacement.set(prevVal[1], prevVal[0]);
				}
				edgeMovement.addPoint(charPoints[(i + 2) % 4] + prevDisplacement.x,
						charPoints[(i + 3) % 4] + prevDisplacement.y);
				edgeMovement.addPoint(charPoints[i % 4] + prevDisplacement.x,
						charPoints[(i + 3) % 4] + prevDisplacement.y);
				if (i % 2 == 1) {
					// Swaps x and y coordinate
					float[] prevVal = { prevDirection.x, prevDirection.y };
					prevDirection.set(prevVal[1], prevVal[0]);
				}

				Line hitEdge = new Line(hitboxPoints[i % 4], hitboxPoints[(i + 1) % 4],
						hitboxPoints[(i + 2) % 4], hitboxPoints[(i + 1) % 4]);
				if (i % 2 == 1) {
					// reflect across y=x
					Transform reflect = new Transform(new float[] { 0, 1, 0, 1, 0, 0 });
					edgeMovement = (Polygon) edgeMovement.transform(reflect);
					hitEdge = (Line) hitEdge.transform(reflect);
				}
				lastMovement[i] = edgeMovement;
				if (edgeMovement.intersects(hitEdge)) {
					intersectionEdge = hitEdge;
					float epsilon = 1e-5f;
					if (i == 0 || i == 3) {
						epsilon = -epsilon;
					}
					displacement[(i + 1) % 2] += hitboxPoints[(i + 1) % 4]
							- charPoints[(i + 3) % 4] + epsilon; // epsilon
																	// helps
																	// avoid
																	// numerical
																	// precision
																	// errors
					switch (i) {
					case 0: // down
						falldamage();
						vel.y = Math.min(vel.y, 0);
						break;
					case 1: // right
						vel.x = Math.min(vel.x, 0);
						break;
					case 2: // up
						vel.y = Math.max(vel.y, 0);
						break;
					case 3: // left
						vel.x = Math.max(vel.x, 0);
						break;
					}
				}
			}

			pos.add(new Vector2f(displacement));
		} else {
			throw new UnsupportedOperationException(
					"Collision with non rectangles not implemented yet\n" +
							"	will result in undefined behavior\n");
		}
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