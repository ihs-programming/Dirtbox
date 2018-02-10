package game.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.Viewport;
import game.blocks.Block;
import game.world.World;

public class ControllableCharacter extends Creature {
	public static boolean flying = false;
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
	private float reach = 5f; // distance (in game units) in which the player
								// can interact
								// with items in the game
	private float mineTime = 0;
	private Block currentBlock;
	private int damage = 1;

	private float totalAttackTime = 250;
	private float attackCharge = 0f;

	protected World world;

	public ControllableCharacter(World w, Image img, Vector2f pos) {
		super(new Sprite(img), pos);

		accel.y = GRAVITY;
		world = w;
	}

	/**
	 * Command to move the character
	 *
	 * @param isLeft
	 */
	public void move(boolean isLeft) {
		float move = (flying ? 10 : 1) * SPEED;
		if (isLeft) {
			vel.x = -move;
		} else {
			vel.x = move;
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

	public void interact(Vector2f position) {
		Entity attackedEntity = null;
		Line characterClick = new Line(pos, position);
		for (Entity e : world.getEntities()) {
			if (e == this) {
				continue;
			}
			if (e.getHitbox().intersects(characterClick) &&
					(attackedEntity == null ||
							attackedEntity.getLocation().distance(pos) > e.getLocation()
									.distance(pos))) {
				attackedEntity = e;
			}
		}
		Block newBlock = world.getBlockAtPosition(position);
		if (newBlock == null || attackedEntity == null) {
			if (newBlock == null && attackedEntity == null) {
			} else if (newBlock == null) {
				if (attackedEntity instanceof Creature) {
					attack((Creature) attackedEntity);
				}
			} else if (attackedEntity == null) {
				mineBlock(newBlock);
			}
			return;
		}
		Vector2f blockCenter = new Vector2f(newBlock.getHitbox().getCenter());
		if (blockCenter.distance(pos) < attackedEntity.getLocation().distance(pos)) {
			mineBlock(newBlock);
		} else if (attackedEntity instanceof Creature) {
			attack((Creature) attackedEntity);
		}
	}

	public void attack(Creature c) {
		if (attackCharge > 1) {
			c.doHit(this, damage);
			attackCharge -= 1;
		}
	}

	public void stopInteracting() {
		stopMining();
	}

	public void mineBlock(Block newBlock) {
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
				w.breakBlock(currentBlock.getPointPos());
				stopMining();
			}
		}
		attackCharge += frametime / totalAttackTime;
		attackCharge = Math.min(2, attackCharge);
	}
}