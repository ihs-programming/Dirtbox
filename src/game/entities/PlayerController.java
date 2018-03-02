package game.entities;

import java.awt.Point;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import game.Viewport;
import game.ViewportController;
import game.items.Inventory;
import game.items.Item;
import game.utils.DefaultKeyListener;
import game.utils.DefaultMouseListener;
import game.world.World;

public class PlayerController implements DefaultKeyListener, DefaultMouseListener {
	private ControllableCharacter character;
	private Inventory inventory = new Inventory();
	private Input userInput;
	private Viewport vp;
	private World world;

	private boolean showInventory = false;
	private Item heldItem;
	private final Point heldItemSize = new Point(25, 25);
	private float pickupRange = 2f;

	public PlayerController(ControllableCharacter character, Input inp, Viewport vp,
			World w) {
		this.character = character;
		userInput = inp;
		userInput.addKeyListener(this);
		userInput.addMouseListener(this);
		this.vp = vp;
		world = w;
	}

	public void draw(Viewport vp) {
		if (showInventory) {
			Graphics g = vp.getGraphics();
			inventory.draw(vp);
			if (heldItem != null) {
				g.drawImage(
						heldItem.getIcon().getScaledImage(heldItemSize.x, heldItemSize.y),
						userInput.getMouseX(),
						userInput.getMouseY());
			}
		} else {
			inventory.drawHotbar(vp);
		}
	}

	public void update(int delta) {
		character.stopMoving();
		if (showInventory) {
			return;
		}
		if (!ViewportController.inChat) {
			if (userInput.isKeyDown(Input.KEY_A)) {
				character.move(true);
			}
			if (userInput.isKeyDown(Input.KEY_D)) {
				character.move(false);
			}
		}
		if (userInput.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			Vector2f mousePos = convertMousePos(userInput.getMouseX(),
					userInput.getMouseY());
			character.interact(mousePos);
		} else {
			character.stopInteracting();
		}

		// pick up blocks
		List<Entity> entities = world.getEntities();
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (e instanceof CollectibleItem) {
				if (e.getLocation().distance(character.getLocation()) > pickupRange) {
					continue;
				}
				world.removeEntity(e);
				CollectibleItem item = (CollectibleItem) e;
				inventory.addItem(item.getItem());
			}
		}
	}

	private void dropHeldItem() {
		if (heldItem == null) {
			return;
		}
		CollectibleItem dropped = new CollectibleItem(heldItem,
				character.getLocation().add(new Vector2f(-4, -4)), world);
		world.addEntity(dropped);
		heldItem = null;
	}

	private Vector2f convertMousePos(int x, int y) {
		return vp.getInverseDrawTransform().transform(new Vector2f(x, y));
	}

	@Override
	public void keyPressed(int key, char c) {
		if (!ViewportController.inChat) {
			switch (key) {
			case Input.KEY_I:
				showInventory = !showInventory;
				if (!showInventory) {
					dropHeldItem();
				}
				break;
			case Input.KEY_W:
				if (!showInventory) {
					character.jump();
				}
				break;
			}
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
			if (invLoc == null) {
				// ensure that invLoc is nonnull in rest of the branches
			} else if (heldItem == null) {
				if (invLoc != null) {
					heldItem = inventory.getItem(invLoc);
					inventory.removeItem(invLoc);
				}
			} else {
				inventory.addItem(heldItem, invLoc);
				heldItem = null;
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
