package game.entities.creature;

import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.entities.Creature;
import game.utils.ImprovedNoise;
import game.world.GameWorld;

public class Bunny extends Creature {
	public static final float JUMP_STRENGTH = 0.008f;

	private int count = 0;

	public Bunny(Sprite sprite, Vector2f pos, GameWorld w) {
		super(sprite, pos, w);
	}

	@Override
	public void update(GameWorld w, float frametime) {
		super.update(w, frametime);

		Vector2f vel = getVelocity();
		vel.x *= 0.9f;
		setVelocity(vel);

		if (Math.random() < 0.01) {
			jump(ImprovedNoise.noise(-0.1, count++ / 10.0, 1) > 0);
		}
	}

	public void jump(boolean forward) {
		jump(Bunny.JUMP_STRENGTH, 1);
		Vector2f vel = getVelocity();
		vel.x = JUMP_STRENGTH * (forward ? 1 : -1);
		setVelocity(vel);
	}
}
