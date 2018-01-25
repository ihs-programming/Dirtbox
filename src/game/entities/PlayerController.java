package game.entities;

import org.newdawn.slick.Input;

import game.utils.DefaultKeyListener;

public class PlayerController {
	private ControllableCharacter character;
	private Input userInput;

	public PlayerController(ControllableCharacter character, Input inp) {
		this.character = character;
		userInput = inp;
		userInput.addKeyListener(new DefaultKeyListener() {
			@Override
			public void keyPressed(int key, char c) {
				if (key == Input.KEY_W) {
					character.jump();
				}
			}

			@Override
			public void keyReleased(int key, char c) {
				// TODO Auto-generated method stub

			}

		});
	}

	public void update(int delta) {
		character.stopMoving();
		if (userInput.isKeyDown(Input.KEY_A)) {
			character.move(true);
		}
		if (userInput.isKeyDown(Input.KEY_D)) {
			character.move(false);
		}
		character.update(delta);
	}
}
