package game.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.Viewport;
import game.blocks.Block;
import game.blocks.BlockType;
import game.world.World;

public class ControllableCharacter extends Creature {
	public boolean flying = false;
	private static final float MAX_SPEED = 10f;
	private static final float ACCELERATION = 10f;
	private static final float JUMP = 250f;
	private float reach = 5f; // distance (in game units) in which the player
								// can interact
								// with items in the game
	private float mineTime = 0;
	private Block currentBlock;
	private int damage = 1;
	private int jumplimit = 1;

	private float totalAttackTime = 250;
	private float attackCharge = 0f;

	protected World world;
	// Break times for different blocks
	private float blockMineTime = 100.0f;
	private BlockType[][] breakTime = {
			{ // 800f
					BlockType.COAL_ORE,
					BlockType.DIAMOND_ORE,
					BlockType.GOLD_ORE,
					BlockType.IRON_ORE,
					BlockType.REDSTONE_ORE
			},
			{ // 400f
					BlockType.STONE,
					BlockType.GRAVEL,
					BlockType.SANDSTONE
			},
			{ // 200f
					BlockType.DIRT,
					BlockType.GRASS,
					BlockType.SAND
			}
	};

	public ControllableCharacter(World w, Image img, Vector2f pos) {
		super(new Sprite(img), pos, w);

		accel.y = GRAVITY;
		world = w;
	}

	/**
	 * Command to move the character
	 *
	 * @param isLeft
	 */
	public void move(boolean isLeft) {
		float move = (flying ? 10 : 1) * MAX_SPEED;
		Vector2f velocity = getVelocity();
		if (isLeft) {
			velocity.x = -move;
		} else {
			velocity.x = move;
		}
		setVelocity(velocity);
	}

	/**
	 * Stops the character from moving (if he were moving)
	 */
	public void stopMoving() {
		Vector2f velocity = getVelocity();
		velocity.x = 0;
		setVelocity(velocity);
	}

	public void jump() {
		super.jump(JUMP, flying ? Integer.MAX_VALUE : jumplimit);
	}

	public void interact(Vector2f position) {
		Entity attackedEntity = null;
		Line characterClick = new Line(getLocation(), position);
		for (Entity e : world.getEntities()) {
			if (e == this) {
				continue;
			}
			if (e.getHitbox().intersects(characterClick) &&
					(attackedEntity == null ||
							attackedEntity.getLocation().distance(getLocation()) > e
									.getLocation()
									.distance(getLocation()))) {
				attackedEntity = e;
			}
		}
		Block newBlock = world.getBlockAtPosition(position);
		if (newBlock == null || attackedEntity == null) {
			if (newBlock == null) {
				if (attackedEntity instanceof Creature) {
					attack((Creature) attackedEntity);
				}
			} else if (attackedEntity == null) {
				mineBlock(newBlock);
			}
			return;
		}
		Vector2f blockCenter = new Vector2f(newBlock.getHitbox().getCenter());
		if (blockCenter.distance(getLocation()) < attackedEntity.getLocation()
				.distance(getLocation())) {
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
		if (newBlock == null || newBlock.getPos().distance(getLocation()) > reach) {
			stopMining();
			return;
		}
		if (!newBlock.equals(currentBlock)) {
			mineTime = 0;
		}
		BlockType type = newBlock.getBlockType();
		updateMineTime(type);
		currentBlock = newBlock;
	}

	public void stopMining() {
		currentBlock = null;
		mineTime = 0;
	}

	public boolean checkBlockType(BlockType[] b, BlockType block) {
		for (BlockType element : b) {
			if (element.equals(block)) {
				return true;
			}
		}
		return false;
	}

	// Checks what kind of block is being mined and changes how quickly it mines
	public void updateMineTime(BlockType block) {
		if (!flying) {
			for (int i = 0; i < breakTime.length; i++) {
				if (checkBlockType(breakTime[i], block)) {
					blockMineTime = 200f * (float) Math.pow(2f, breakTime.length - i - 1);
				}
			}
			if (block == BlockType.BEDROCK) {
				blockMineTime = Float.MAX_VALUE;
			}
		} else {
			blockMineTime = 10.0f;
		}
	}

	@Override
	public void draw(Viewport vp) {
		super.draw(vp);
		if (Viewport.DEBUG_MODE) {
			String debugString = String.format("Character position: %f %f\n",
					getLocation().x,
					getLocation().y);
			vp.draw(debugString, 20, 30, Color.white);
		}
	}

	@Override
	public void update(World w, float frametime) {
		super.update(w, frametime);

		if (currentBlock != null) {
			mineTime += frametime;
			if (mineTime > blockMineTime) {
				w.breakBlock(currentBlock.getPointPos());
				stopMining();
			}
		}
		attackCharge += frametime / totalAttackTime;
		attackCharge = Math.min(2, attackCharge);
	}
}
