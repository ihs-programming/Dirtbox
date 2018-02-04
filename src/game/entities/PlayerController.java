package game.entities;

import java.awt.Point;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import game.Viewport;
import game.items.Inventory;
import game.items.Item;
import game.utils.DefaultKeyListener;
import game.utils.DefaultMouseListener;

public class PlayerController implements DefaultKeyListener, DefaultMouseListener {
	private ControllableCharacter character;
	private Inventory inventory = new Inventory();
	private Input userInput;
	private Viewport vp;

	private boolean showInventory = false;
	private Item heldItem;

	public PlayerController(ControllableCharacter character, Input inp, Viewport vp) {
		this.character = character;
		userInput = inp;
		userInput.addKeyListener(this);
		userInput.addMouseListener(this);
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

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		if (button == Input.MOUSE_LEFT_BUTTON && showInventory) {
			Point invLoc = inventory
					.convertScreenPosToInventoryItem(new Vector2f(x, y));
			System.out.println(invLoc);
			if (invLoc == null) {
				// ensure that invLoc is nonnull in rest of the branches
			} else if (heldItem == null) {
				if (invLoc != null) {
					heldItem = inventory.getItem(invLoc);
					inventory.removeItem(invLoc);
				}
			} else {
				inventory.addItem(heldItem, invLoc);
			}
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
	public void setInput(Input inp) {
	}
}
