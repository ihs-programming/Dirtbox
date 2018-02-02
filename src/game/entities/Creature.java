package game.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import game.Viewport;

public abstract class Creature extends Entity {
	private final float HEALTH_BAR_HEIGHT = .1f;
	private final float HEALTH_BAR_DISPLACEMENT = .1f;
	protected int totalHealth = 20;
	protected int health;

	public Creature(Image spritesheet, int sheetwidth, int sheetheight, Vector2f pos) {
		super(spritesheet, sheetwidth, sheetheight, pos);
		accel.y = GRAVITY;
		health = totalHealth;
	}

	public void doHit(Entity aggressor, int damage) {
		Vector2f dist = getLocation().sub(aggressor.getLocation());

		if (dist.length() < 5) {
			vel.x += dist.normalise().scale(0.01f).x;
			health -= damage;
		}
	}

	@Override
	public boolean alive() {
		return health > 0;
	}

	@Override
	public void draw(Viewport vp) {
		super.draw(vp);
		if (Viewport.DEBUG_MODE) {
			// create health bar
			Rectangle healthBarOutline = new Rectangle(0, 0, 1, 1);
			Rectangle healthBar = new Rectangle(0, 0, 1.0f * health / totalHealth, 1);
			Transform barTransform = new Transform(new float[] { getHitbox().getWidth(),
					0,
					pos.x, 0, HEALTH_BAR_HEIGHT, pos.y - HEALTH_BAR_DISPLACEMENT });

			vp.fill(healthBar.transform(barTransform), Color.red);
			vp.draw(healthBarOutline.transform(barTransform), Color.white);
		}
	}
}
