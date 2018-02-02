package game.blocks;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

public class SolidBlock extends Block {
	public Shape hitbox;

	BlockType type;

	public SolidBlock(BlockType t, float xpos, float ypos) {
		super(t.sx, t.sy, xpos, ypos);

		type = t;

		// change size later
		hitbox = new Rectangle(super.getPos().x, super.getPos().y, 1, 1);
	}

	@Override
	public String toString() {
		String s = "";
		s += type.name() + " ";
		s += super.getPos().x + " ";
		s += super.getPos().y + " ";
		return s;
	}
}
