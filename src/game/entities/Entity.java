package game.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.Viewport;

public class Entity {
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
		return pos.copy().sub(new Vector2f(width / 2, height / 2));
	}
}