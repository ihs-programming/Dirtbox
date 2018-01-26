package game.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import game.utils.ImprovedNoise;

public class Creature extends Entity {

	public Creature(Image spritesheet, int sheetwidth, int sheetheight, Vector2f pos) {
		super(spritesheet, sheetwidth, sheetheight, pos);
		accel.y = GRAVITY;
	}

	int count = 0;

	@Override
	public void update(float frametime) {
		super.update(frametime);
		vel.x = 0.01f * (float) ImprovedNoise.noise(-1, count++ / 1000.0, 1);
	}
}
