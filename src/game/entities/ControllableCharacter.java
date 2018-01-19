package game.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import game.utils.DefaultKeyListener;

public class ControllableCharacter extends Entity {
	private int speed = 10;
	private int jumpStrength = 10;
	private int gravity = 1;

	private Input userInput;

	public ControllableCharacter(Image spritesheet, int sheetwidth, int sheetheight,
			Vector2f pos, Input inp) {
		super(spritesheet, sheetwidth, sheetheight, pos);
		userInput = inp;
		userInput.addKeyListener(new DefaultKeyListener() {

			@Override
			public void keyPressed(int key, char c) {
				if (key == Input.KEY_W) {
					vel.y = -jumpStrength;
				}
			}

			@Override
			public void keyReleased(int key, char c) {
			}
		});
	}

	@Override
	public void update(float frameTime) {
		if (userInput.isKeyDown(Input.KEY_W)) {
			vel.x = speed;
		}
		if (userInput.isKeyDown(Input.KEY_D)) {
			vel.x = -speed;
		}
		super.update(frameTime);
	}
}
