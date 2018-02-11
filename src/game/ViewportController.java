package game;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import game.utils.Chat;
import game.utils.Console;
import game.utils.DefaultKeyListener;
import game.utils.DefaultMouseListener;

public class ViewportController implements DefaultKeyListener, DefaultMouseListener {
	private static final float MOVEMENT_FACTOR = 1f;
	private static final float SCALE_INCREASE = 1.2f;
	private static final float SCALE_DECREASE = 1.0f / 1.2f;

	private Input userInput;
	private Viewport vp;
	private Chat chat;

	public ViewportController(Input inp, Viewport vp) {
		userInput = inp;
		this.vp = vp;
		userInput.addKeyListener(this);
		userInput.addMouseListener(this);
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public void update(float frametime) {
		int[] directions = { Input.KEY_UP, Input.KEY_RIGHT, Input.KEY_DOWN,
				Input.KEY_LEFT };
		int[][] directionalMovement = { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };
		int[] resultMovement = new int[2];
		for (int i = 0; i < directions.length; i++) {
			if (userInput.isKeyDown(directions[i])) {
				resultMovement[0] += directionalMovement[i][0];
				resultMovement[1] += directionalMovement[i][1];
			}
		}
		Vector2f movement = new Vector2f(resultMovement[0], resultMovement[1]);
		movement.scale(MOVEMENT_FACTOR);
		vp.move(movement);
	}

	public static boolean inChat = false;

	@Override
	public void keyPressed(int key, char c) {
		if (inChat) {
			inChat = chat.keyPressed(key, c);
		} else {
			switch (key) {
			case Input.KEY_MINUS:
				vp.zoom(SCALE_DECREASE);
				break;
			case Input.KEY_EQUALS:
				vp.zoom(SCALE_INCREASE);
				break;
			case Input.KEY_P:
				vp.printDebugInfo();
				break;
			case Input.KEY_T:
				if (chat != null) {
					chat.displaychat = true;
				}
				inChat = true;
				break;
			case Input.KEY_F1:
				Thread console = new Console();
				console.start();
				break;
			case Input.KEY_M:
				MainGameState.playMusic = !MainGameState.playMusic;
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void keyReleased(int key, char c) {
	}

	@Override
	public void mouseWheelMoved(int change) {
		if (change < 0) {
			vp.zoom(SCALE_DECREASE);
		} else {
			vp.zoom(SCALE_INCREASE);
		}
	}

	@Override
	public void inputEnded() {
	}

	@Override
	public boolean isAcceptingInput() {
		return true;
	}

	@Override
	public void inputStarted() {
	}

	@Override
	public void setInput(Input input) {
	}
}
