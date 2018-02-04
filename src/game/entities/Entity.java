package game.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
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

public class Entity {
	protected static final float GRAVITY = 0.00002613f;
	private static final boolean DEBUG_COLLISION = false;

	private SpriteSheet spritesheet;
	private Shape hitbox;
	protected Sprite sprite;
	protected Vector2f pos = new Vector2f();
	protected Vector2f prevPos = new Vector2f();
	protected Vector2f vel = new Vector2f();
	protected Vector2f accel = new Vector2f();
	private float scale = 1f;

	protected Polygon[] lastMovement = new Polygon[4];
	private Shape intersectionEdge;

	public Entity(Image spritesheet, int sheetwidth, int sheetheight, float hitwidth,
			float hitheight, Vector2f pos) {
		this.pos = pos.copy();
		setSpriteSheet(spritesheet, sheetwidth, sheetheight);
		this.hitbox = new Rectangle(0, 0, hitwidth, hitheight);
		this.hitbox.setCenterX(this.pos.x);
		this.hitbox.setCenterY(this.pos.y);
	}

	public Entity(Image spritesheet, int sheetwidth, int sheetheight, Vector2f pos) {
		this.pos = pos.copy();
		this.setSpriteSheet(spritesheet, sheetwidth, sheetheight);
		this.generateHitbox();
		this.hitbox.setCenterX(this.pos.x);
		this.hitbox.setCenterY(this.pos.y);
	}

	public Entity(SpriteSheet sheet, Vector2f pos) {
		this.pos = pos.copy();
		this.setSpriteSheet(sheet);
	}

	public void setSpriteSheet(SpriteSheet sheet) {
		this.spritesheet = sheet;
		this.sprite = new Sprite(this.spritesheet.getSprite(0, 0));
		this.sprite.loc = pos;
	}

	public void setSpriteSheet(Image sheet, int width, int height) {
		setSpriteSheet(new SpriteSheet(sheet, sheet.getWidth() / width,
				sheet.getHeight() / height));
	}

	public Shape getHitbox() {
		generateHitbox();
		return this.hitbox;
	}

	private void generateHitbox() {
		float width = this.spritesheet.getWidth() / this.spritesheet.getHorizontalCount();
		float height = this.spritesheet.getHeight() / this.spritesheet.getVerticalCount();
		this.hitbox = new Rectangle(
				pos.x, pos.y, width, height);
	}

	public void draw(Viewport vp) {
		this.sprite.img = this.spritesheet.getSprite(0, 0).getScaledCopy(scale);
		vp.draw(this.sprite);
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

	public void magnify(float factor) {
		this.scale *= factor;
	}

	public Vector2f getLocation() {
		float width = spritesheet.getWidth();
		float height = spritesheet.getWidth();
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

	/**
	 * Return false if the entity should be deleted.
	 *
	 * @return
	 */
	public boolean alive() {
		return true;
	}
}