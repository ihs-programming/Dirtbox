package game.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public abstract class Creature extends Entity {

	public Creature(Image spritesheet, int sheetwidth, int sheetheight, Vector2f pos) {
		super(spritesheet, sheetwidth, sheetheight, pos);
		accel.y = GRAVITY;
	}
}
