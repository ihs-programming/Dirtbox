package game.utils;

import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

/*
 * Hides away keylistener methods that aren't commonly used
 */
public interface DefaultKeyListener extends KeyListener {
	default void inputStarted() {
	}

	default void inputEnded() {
	}
	
	default void setInput(Input inp) {
		
	}
	
	default boolean isAcceptingInput() {
		return true;
	}
}
