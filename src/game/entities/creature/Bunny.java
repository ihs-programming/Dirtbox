package game.entities.creature;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import game.World;
import game.entities.Creature;
import game.utils.ImprovedNoise;

public class Bunny extends Creature {
	public static final float JUMP_STRENGTH = 0.008f;

	int count = 0;

	public Bunny(Image spritesheet, int sheetwidth, int sheetheight, Vector2f pos) {
		super(spritesheet, sheetwidth, sheetheight, pos);
	}

	@Override
	public void update(World w, float frametime) {
		super.update(w, frametime);

		vel.x *= 0.9f;

		if (Math.random() < 0.01) {
			vel.y = -JUMP_STRENGTH;
			vel.x = 0.1f * (float) ImprovedNoise.noise(-0.1, count++ / 1000.0, 1);
			System.out.println(vel.x);
		}
	}
}
