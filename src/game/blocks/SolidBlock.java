package game.blocks;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

public class SolidBlock extends Block {
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
		super(t, t.sx, t.sy, xpos, ypos, new SolidBlockBodyFactory(xpos, ypos));
	}
}
