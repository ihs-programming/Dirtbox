package game.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import game.Viewport;

public class ControllableCharacter extends Entity {
	private static final float SPEED = 0.0085f;
	private static final float JUMP = 0.012f;
	// 1 block = 1 m^2, 1 block = 16 px,
	// 1 m =
	// 16 px,
	// 1 frame = 1/60s, 1 frame = 16.7ms,
	// 9.8m/s^2
	// = 9.8*16px/60frames, gravity =
	// -2.613px/frame

	public static final float BLOCK_MINE_TIME = 10.0f;

	public ControllableCharacter(Image spritesheet, int sheetwidth, int sheetheight,
			Vector2f pos) {
		super(spritesheet, sheetwidth, sheetheight, pos);
		accel.y = GRAVITY;
	}

	/**
	 * Command to move the character
	 *
	 * @param isLeft
	 */
	public void move(boolean isLeft) {
		if (isLeft) {
			vel.x = -SPEED;
		} else {
			vel.x = SPEED;
		}
	}

	/**
	 * Stops the character from moving (if he were moving)
	 */
	public void stopMoving() {
		vel.x = 0;
	}

	public void jump() {
		vel.y = -JUMP;
	}

	@Override
	public void draw(Viewport vp) {
		super.draw(vp);
		if (Viewport.DEBUG_MODE) {
			String debugString = String.format("Character position: %f %f\n", pos.x,
					pos.y);
			vp.draw(debugString, 20, 30, Color.white);
			if (lastMovement != null) {
				vp.draw(String.format("Last movement: %f %f\n", lastMovement.getMinX(),
						lastMovement.getMinY()), 20, 50, Color.white);
			}
		}
	}
}