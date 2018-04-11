package game;

import org.dyn4j.dynamics.Body;

public interface PhysicsBodyFactory {
	Body createBody(Object data);
}
