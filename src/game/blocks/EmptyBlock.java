package game.blocks;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.MassType;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Shape;

public class EmptyBlock extends Block {
	private final static int EMPTY_SPRITE_X = 4;
	private final static int EMPTY_SPRITE_Y = 11;

	protected EmptyBlock(float xpos, float ypos) {
		this(xpos, ypos, true);
	}

	protected EmptyBlock(float xpos, float ypos, boolean b) {
		super(BlockType.EMPTY, EMPTY_SPRITE_X, EMPTY_SPRITE_Y, xpos, ypos, b);
	}

	@Override
	public Shape getHitbox() {
		return new Point(0, 0);
	}

	@Override
	public Body getBody() {
		if (physicsBody == null) {
			physicsBody = new Body();
			physicsBody.setMassType(MassType.INFINITE);
		}
		return physicsBody;
	}
}
