package game.entities.creature;

import java.util.Set;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import game.entities.Creature;
import game.entities.Entity;
import game.world.World;

public class Wolf extends Creature {
	private int count;

	public Wolf(Image spritesheet, int sheetwidth, int sheetheight, Vector2f pos) {
		super(spritesheet, sheetwidth, sheetheight, pos);
	}

	@Override
	public void update(World w, float frametime) {
		super.update(w, frametime);

		Set<Entity> targets = w.getEntities(getLocation(), 50);
		Entity target = null;
		for (Entity e : targets) {
			if (isTarget(e)) {
				target = e;
			}
		}

		if (target != null) {
			if (Math.abs(target.getLocation().getX() - getLocation().getX()) > 2) {
				if (target.getLocation().getX() > getLocation().getX()) {
					vel.x = 0.005f;
				} else {
					vel.x = -0.005f;
				}
			}
			if (Math.random() < 0.1) {
				((Creature) target).doHit(this, 1);
			}
			if (Math.random() < 0.01) {
				vel.y = -Bunny.JUMP_STRENGTH;
			}
		}

	}

	private static boolean isTarget(Entity e) {
		return e instanceof Bunny;
	}
}
