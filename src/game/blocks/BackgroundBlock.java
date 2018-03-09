package game.blocks;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

/**
 * For blocks that aren't empty but should have different collision stuff.
 *
 * @author s-weia
 *
 */
public class BackgroundBlock extends Block {
	private Shape hitbox;

	public BackgroundBlock(BlockType t, float xpos, float ypos) {
		this(t, xpos, ypos, true);
	}

	public BackgroundBlock(BlockType t, float xpos, float ypos, boolean b) {
		super(t, t.sx, t.sy, xpos, ypos, b);

		// change size later
		hitbox = new Rectangle(super.getPos().x, super.getPos().y, 1, 1);
	}

	@Override
	public Shape getHitbox() {
		return this.hitbox;
	}
}
