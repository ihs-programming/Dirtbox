package game.utils;

import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;

public interface DefaultMouseListener extends MouseListener {

	@Override
	default void setInput(Input input) {
		// TODO Auto-generated method stub

	}

	@Override
	default boolean isAcceptingInput() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	default void inputEnded() {
		// TODO Auto-generated method stub

	}

	@Override
	default void inputStarted() {
		// TODO Auto-generated method stub

	}

	@Override
	default void mouseWheelMoved(int change) {
		// TODO Auto-generated method stub

	}

	@Override
	default void mouseClicked(int button, int x, int y, int clickCount) {
		// TODO Auto-generated method stub

	}

	@Override
	default void mousePressed(int button, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	default void mouseReleased(int button, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	default void mouseMoved(int oldx, int oldy, int newx, int newy) {
		// TODO Auto-generated method stub

	}

	@Override
	default void mouseDragged(int oldx, int oldy, int newx, int newy) {
		// TODO Auto-generated method stub

	}
}
