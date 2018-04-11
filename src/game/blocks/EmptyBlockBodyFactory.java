package game.blocks;

import org.dyn4j.dynamics.Body;

import game.physics.PhysicsBodyFactory;

public class EmptyBlockBodyFactory implements PhysicsBodyFactory {

	@Override
	public Body createBody(Object data) {
		Body b = new Body();
		b.setUserData(data);
		return b;
	}

}
