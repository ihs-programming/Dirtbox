package game;

import org.newdawn.slick.Input;
import org.newdawn.slick.state.GameState;

/**
 * Abstracts away all window callbacks for gamestate
 * @author Peachball
 */
public interface DefaultGameState extends GameState {

	default void mouseClicked(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	default void mouseDragged(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	default void mouseMoved(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	default void mousePressed(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	default void mouseReleased(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	default void mouseWheelMoved(int arg0) {
		// TODO Auto-generated method stub
		
	}

	default void inputEnded() {
		// TODO Auto-generated method stub
		
	}

	default void inputStarted() {
		// TODO Auto-generated method stub
		
	}

	default boolean isAcceptingInput() {
		// TODO Auto-generated method stub
		return true;
	}

	default void setInput(Input arg0) {
		// TODO Auto-generated method stub
		
	}

	default void keyPressed(int arg0, char arg1) {
		// TODO Auto-generated method stub
		
	}

	default void keyReleased(int arg0, char arg1) {
		// TODO Auto-generated method stub
		
	}

	default void controllerButtonPressed(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	default void controllerButtonReleased(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	default void controllerDownPressed(int arg0) {
		// TODO Auto-generated method stub
		
	}

	default void controllerDownReleased(int arg0) {
		// TODO Auto-generated method stub
		
	}

	default void controllerLeftPressed(int arg0) {
		// TODO Auto-generated method stub
		
	}

	default void controllerLeftReleased(int arg0) {
		// TODO Auto-generated method stub
		
	}

	default void controllerRightPressed(int arg0) {
		// TODO Auto-generated method stub
		
	}

	default void controllerRightReleased(int arg0) {
		// TODO Auto-generated method stub
		
	}

	default void controllerUpPressed(int arg0) {
		// TODO Auto-generated method stub
		
	}

	default void controllerUpReleased(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
