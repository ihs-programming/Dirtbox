package game.entities;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import game.Viewport;
import game.items.Inventory;
import game.utils.DefaultKeyListener;

public class PlayerController implements DefaultKeyListener {
	private ControllableCharacter character;
	private Inventory inventory = new Inventory();
	private Input userInput;
	private Viewport vp;

	private boolean showInventory = false;

	public PlayerController(ControllableCharacter character, Input inp, Viewport vp) {
		this.character = character;
		userInput = inp;
		userInput.addKeyListener(new DefaultKeyListener() {
			@Override
			public void keyPressed(int key, char c) {
			}

			@Override
			public void keyReleased(int key, char c) {
				// TODO Auto-generated method stub

			}

		});
		userInput.addKeyListener(this);
		this.vp = vp;
	}

	public void draw(Viewport vp) {
		if (showInventory) {
			inventory.draw(vp);
		}
	}

	public void update(int delta) {
		character.stopMoving();
		if (showInventory) {
			return;
		}
		if (userInput.isKeyDown(Input.KEY_A)) {
			character.move(true);
		}
		if (userInput.isKeyDown(Input.KEY_D)) {
			character.move(false);
		}
		if (userInput.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			Vector2f mousePos = convertMousePos(userInput.getMouseX(),
					userInput.getMouseY());
			character.interact(mousePos);
		} else {
			character.stopInteracting();
		}
	}

	private Vector2f convertMousePos(int x, int y) {
		return vp.getInverseDrawTransform().transform(new Vector2f(x, y));
	}

	@Override
	public void keyPressed(int key, char c) {
		switch (key) {
		case Input.KEY_I:
			showInventory = !showInventory;
			break;
		case Input.KEY_W:
			if (!showInventory) {
				character.jump();
			}
			break;
		}
	}

	@Override
	public void keyReleased(int key, char c) {
	}
}
