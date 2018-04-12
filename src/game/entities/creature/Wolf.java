package game.entities.creature;

import java.util.Set;

import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.entities.Creature;
import game.entities.Entity;
import game.world.GameWorld;

public class Wolf extends Creature {

	public Wolf(Sprite sprite, Vector2f pos, GameWorld w) {
		super(sprite, pos, w);
	}

	@Override
	public void update(GameWorld w, float frametime) {
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
				Vector2f vel = getVelocity();
				if (target.getLocation().getX() > getLocation().getX()) {
					vel.x = 0.005f;
				} else {
					vel.x = -0.005f;
				}
				setVelocity(vel);
			}
			if (Math.random() < 0.1) {
				((Creature) target).doHit(this, 1);
			}
			if (Math.random() < 0.01) {
				jump(Bunny.JUMP_STRENGTH, 1);
			}
		}

	}

	private static boolean isTarget(Entity e) {
		return e instanceof Bunny;
	}
}
