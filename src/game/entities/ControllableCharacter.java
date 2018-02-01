package game.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import game.Viewport;
import game.World;
import game.blocks.Block;

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
	private float reach = 5f; // distance (in game units) in which the player can interact
								// with items in the game
	private float mineTime = 0;
	private Block currentBlock;

	private World world;

	public ControllableCharacter(World w, Image spritesheet, int sheetwidth,
			int sheetheight,
			Vector2f pos) {
		super(spritesheet, sheetwidth, sheetheight, pos);
		accel.y = GRAVITY;
		world = w;
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

	public void mineBlock(Vector2f position) {
		Block newBlock = world.getBlockAtPosition(position);
		if (newBlock == null || newBlock.getPos().distance(pos) > reach) {
			stopMining();
			return;
		}
		if (newBlock != currentBlock) {
			mineTime = 0;
		}
		currentBlock = newBlock;
	}

	public void stopMining() {
		currentBlock = null;
		mineTime = 0;
	}

	@Override
	public void draw(Viewport vp) {
		super.draw(vp);
		if (Viewport.DEBUG_MODE) {
			String debugString = String.format("Character position: %f %f\n", pos.x,
					pos.y);
			vp.draw(debugString, 20, 30, Color.white);
		}
	}

	@Override
	public void update(World w, float frametime) {
		super.update(w, frametime);

		if (currentBlock != null) {
			mineTime += frametime;
			if (mineTime > ControllableCharacter.BLOCK_MINE_TIME) {
				w.removeBlock(currentBlock);
				stopMining();
			}
		}
	}
}