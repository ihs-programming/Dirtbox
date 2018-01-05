package game;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Rectangle;

public class SolidBlock extends Block {	
	public Shape hitbox;
	
	
	BlockType type;
	
	public SolidBlock(BlockType t, float xpos, float ypos) {
		super(t.sx, t.sy, xpos, ypos);
		
		type = t;
		
		//change size later
		hitbox = new Rectangle(super.getPos().x, super.getPos().y, 1, 1);
	}
	
}
