package game.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public abstract class Creature extends Entity {
	protected int health = 20;

	public Creature(Image spritesheet, int sheetwidth, int sheetheight, Vector2f pos) {
		super(spritesheet, sheetwidth, sheetheight, pos);
		accel.y = GRAVITY;
	}

	public void doHit(Creature aggressor, int damage) {
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
}
