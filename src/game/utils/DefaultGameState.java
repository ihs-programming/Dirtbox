package game.utils;

import org.newdawn.slick.Input;
import org.newdawn.slick.state.GameState;

/**
 * Abstracts away all window callbacks for gamestate
 *
 * @author Peachball
 */
public interface DefaultGameState extends GameState {

	@Override
	default void mouseClicked(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	default void mouseDragged(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	default void mouseMoved(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	default void mousePressed(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	default void mouseReleased(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	default void mouseWheelMoved(int arg0) {
		// TODO Auto-generated method stub

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
	default boolean isAcceptingInput() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	default void setInput(Input arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	default void keyPressed(int arg0, char arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	default void keyReleased(int arg0, char arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	default void controllerButtonPressed(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	default void controllerButtonReleased(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	default void controllerDownPressed(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	default void controllerDownReleased(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	default void controllerLeftPressed(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	default void controllerLeftReleased(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	default void controllerRightPressed(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	default void controllerRightReleased(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	default void controllerUpPressed(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	default void controllerUpReleased(int arg0) {
		// TODO Auto-generated method stub

	}
}
