package game.blocks;

import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Shape;

public class EmptyBlock extends Block {
	private final static int EMPTY_SPRITE_X = 4;
	private final static int EMPTY_SPRITE_Y = 11;

	protected EmptyBlock(float xpos, float ypos) {
		super(EMPTY_SPRITE_X, EMPTY_SPRITE_Y, xpos, ypos);
	}

	@Override
	public Shape getHitbox() {
		return new Point(0, 0);
	}
}
