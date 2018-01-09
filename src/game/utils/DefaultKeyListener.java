package game.utils;

import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

/*
 * Hides away keylistener methods that aren't commonly used
 */
public interface DefaultKeyListener extends KeyListener {
	@Override
	default void inputStarted() {
	}

	@Override
	default void inputEnded() {
	}

	@Override
	default void setInput(Input inp) {

	}

	@Override
	default boolean isAcceptingInput() {
		return true;
	}
}
