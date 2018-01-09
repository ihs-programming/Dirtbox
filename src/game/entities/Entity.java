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
	private Vector2f pos;
	private Vector2f vel;
	private Vector2f accel;
	private float scale = 1f;
	
	//based on self acceleration
	private float maxHorzSpeed;

	public Entity(Image spritesheet, int sheetwidth, int sheetheight, float hitwidth, float hitheight, Vector2f pos) {
		this.pos = pos.copy();
		setSpriteSheet(spritesheet, sheetwidth, sheetheight);
		this.hitbox = new Rectangle(0, 0, hitwidth, hitheight);
		this.hitbox.setCenterX(this.pos.x);
		this.hitbox.setCenterY(this.pos.y);
	}

	public Entity(Image spritesheet, int sheetwidth, int sheetheight, Vector2f pos) {
		this.pos = pos.copy();
		this.setSpriteSheet(spritesheet, sheetwidth, sheetheight);
		this.generateHitBox();
		this.hitbox.setCenterX(this.pos.x);
		this.hitbox.setCenterY(this.pos.y);
	}

	public Entity(SpriteSheet sheet, Vector2f pos) {
		this.pos = pos.copy();
		this.setSpriteSheet(sheet);
		this.generateHitBox();
	}

	public void setSpriteSheet(SpriteSheet sheet) {
		this.spritesheet = sheet;
		this.sprite = new Sprite(this.spritesheet.getSprite(0, 0));
		this.sprite.loc = pos;
	}

	public void setSpriteSheet(Image sheet, int width, int height) {
		this.spritesheet = new SpriteSheet(sheet, sheet.getWidth() / width, sheet.getHeight() / height);
		this.sprite = new Sprite(this.spritesheet.getSprite(0, 0));
		this.sprite.loc = pos;
	}

	public void generateHitBox() {
		float width = this.spritesheet.getWidth() / this.spritesheet.getHorizontalCount();
		float height = this.spritesheet.getHeight() / this.spritesheet.getVerticalCount();
		this.hitbox = new Rectangle(-width / 2, -height / 2, width, height);
	}

	public void draw(Viewport vp) {
		this.sprite.img = this.spritesheet.getSprite(0, 0).getScaledCopy(scale);
		vp.draw(this.sprite);
	}

	public void update(float frametime) {
		Vector2f prevpos = pos.copy();
		this.pos.add(this.vel.scale(frametime));
		this.vel.add(this.accel.scale(frametime));
		this.hitbox.setCenterX(this.pos.x);
		this.hitbox.setCenterY(this.pos.y);
	}

	public void magnify(float factor) {
		this.scale *= factor;
	}
	
	public Vector2f getAccel() {
		return this.accel;
	}
	public void setAccel(Vector2f newAccel) {
		this.accel = newAccel;
	}
	public Vector2f getVel() {
		return this.vel;
	}
	public void setVel(Vector2f newVel) {
		this.vel = newVel;
	}
	public float getMaxHorzSpeed() {
		return this.maxHorzSpeed;
	}
	public void setMaxHorzSpeed(float newMaxHorzSpeed) {
		this.maxHorzSpeed = newMaxHorzSpeed;
	}
}