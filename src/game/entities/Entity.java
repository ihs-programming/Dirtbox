package game.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.Viewport;

public class Entity {
	protected static final float GRAVITY = 0.00002613f;

	private SpriteSheet spritesheet;
	private Shape hitbox;
	private Sprite sprite;
	protected Vector2f pos = new Vector2f();
	protected Vector2f vel = new Vector2f();
	protected Vector2f accel = new Vector2f();
	private float scale = 1f;

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
		this.generateHitbox();
	}

	public void setSpriteSheet(SpriteSheet sheet) {
		this.spritesheet = sheet;
		this.sprite = new Sprite(this.spritesheet.getSprite(0, 0));
		this.sprite.loc = pos;
	}

	public void setSpriteSheet(Image sheet, int width, int height) {
		this.spritesheet = new SpriteSheet(sheet, sheet.getWidth() / width,
				sheet.getHeight() / height);
		this.sprite = new Sprite(this.spritesheet.getSprite(0, 0));
		this.sprite.loc = pos;
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
	}

	public void update(float frametime) {
		this.pos.add(this.vel.copy().scale(frametime));
		this.vel.add(this.accel.copy().scale(frametime));
		this.hitbox.setCenterX(this.pos.x);
		this.hitbox.setCenterY(this.pos.y);
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
			Rectangle boundingBox = (Rectangle) hitbox;
			Vector2f displacement = new Vector2f();
			// push player up
			if (boundingBox.getMinY() < charHitbox.getMaxY()) {
				displacement.y -= charHitbox.getMaxY() - boundingBox.getMinY();
				vel.y = Math.min(vel.y, 0);
			}
			pos.add(displacement);
		} else {
			throw new UnsupportedOperationException(
					"Collision with non rectangles not implemented yet\n" +
							"	will result in undefined behavior\n");
		}
	}
}