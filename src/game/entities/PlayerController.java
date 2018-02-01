package game.entities;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import game.Viewport;
import game.World;
import game.utils.DefaultKeyListener;

public class PlayerController {
	private ControllableCharacter character;
	private Input userInput;
	private World world;
	private Viewport vp;

	public PlayerController(ControllableCharacter character, Input inp, Viewport vp,
			World world) {
		this.character = character;
		this.world = world;
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
		this.vp = vp;
	}

	public void draw(Viewport vp) {
	}

	public void update(int delta) {
		character.stopMoving();
		if (userInput.isKeyDown(Input.KEY_A)) {
			character.move(true);
		}
		if (userInput.isKeyDown(Input.KEY_D)) {
			character.move(false);
		}
		if (userInput.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			Vector2f blockPos = convertMousePos(userInput.getMouseX(),
					userInput.getMouseY());
			character.mineBlock(blockPos);
		} else {
			character.stopMining();
		}
	}

	private Vector2f convertMousePos(int x, int y) {
		return vp.getInverseDrawTransform().transform(new Vector2f(x, y));
	}
}
