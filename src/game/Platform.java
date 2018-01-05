package game;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Line;

public class Platform extends Block{

	public Shape hitbox;
	
	//if player jumps up through block, should go through
	//player should also be able to stand on top
	public boolean collideCond;
	
	public enum Type {
		
	}
	
	Type blockType;
	
	public Platform(int sx, int sy, float xpos, float ypos) {
		super(sx, sy, xpos, ypos);
		// TODO Auto-generated constructor stub
	}
	
}
