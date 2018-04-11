package game.blocks;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

public class SolidBlock extends Block {
	private Shape hitbox;

	/**
	 * Note: if t is "EMPTY", the block created will still collide with objects!
	 *
	 * Use Block.createBlock to avoid the above behavior
	 *
	 * @param t
	 * @param xpos
	 * @param ypos
	 */
	protected SolidBlock(BlockType t, float xpos, float ypos) {
		this(t, xpos, ypos, true);
	}

	protected SolidBlock(BlockType t, float xpos, float ypos, boolean b) {
		super(t, t.sx, t.sy, xpos, ypos, b, new SolidBlockBodyFactory(xpos, ypos));

		// change size later
		hitbox = new Rectangle(super.getPos().x, super.getPos().y, 1, 1);
	}

	@Override
	public Shape getHitbox() {
		return this.hitbox;
	}
}
