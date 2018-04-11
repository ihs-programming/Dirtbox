package game.blocks;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.MassType;

import game.physics.PhysicsBodyFactory;

public class SolidBlockBodyFactory implements PhysicsBodyFactory {

	private float xpos;
	private float ypos;

	public SolidBlockBodyFactory(float xpos, float ypos) {
		this.xpos = xpos;
		this.ypos = ypos;
	}

	@Override
	public Body createBody(Object bodyData) {
		Convex c = new org.dyn4j.geometry.Rectangle(Block.BLOCK_SPRITE_SIZE,
				Block.BLOCK_SPRITE_SIZE);
		Body body = new Body();
		body.addFixture(c);
		body.translateToOrigin();
		float disp = Block.BLOCK_SPRITE_SIZE / 2f;
		body.translate(xpos + disp, ypos + disp);
		body.setMass(MassType.INFINITE);
		body.setUserData(bodyData);
		return body;
	}
}
