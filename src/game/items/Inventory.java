package game.items;

import java.awt.Point;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.Viewport;

public class Inventory {
	// number of slots wide and high the inventory display will be
	private InventoryConfig config = new InventoryConfig();
	private InventoryItem items[][] = new InventoryItem[config.numSlotsWide][config.numSlotsHigh];

	public Inventory() {
	}

	public void addItem(Item item) {
		for (InventoryItem[] item2 : items) {
			for (InventoryItem element : item2) {
				if (element == null) {
					continue;
				}
				if (element.addItem(item)) {
					return;
				}
			}
		}
		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < items[i].length; j++) {
				if (items[i][j] == null) {
					items[i][j] = new InventoryItem(item);
					items[i][j].addItem();
					return;
				}
			}
		}
	}

	/**
	 * Attempts to add item to specified position slot
	 *
	 * Note that this method will silently fail if the item to add does not match
	 * the item already in the slot
	 *
	 * @param item
	 * @param pos
	 */
	public void addItem(Item item, Point pos) {
		int i = pos.x, j = pos.y;
		if (items[i][j] == null) {
			items[i][j] = new InventoryItem(item);
			items[i][j].addItem();
		} else {
			items[i][j].addItem(item);
		}
	}

	/**
	 * Removes one item stored in the slot at pos
	 *
	 * @param pos
	 */
	public void removeItem(Point pos) {
		InventoryItem item = items[pos.x][pos.y];
		if (item != null) {
			item.remove();
			if (item.isEmpty()) {
				items[pos.x][pos.y] = null;
			}
		}
	}

	public void draw(Viewport vp) {
		Graphics g = vp.getGraphics();
		drawOverlay(g);
		drawItems(g);
	}

	private void drawOverlay(Graphics g) {
		g.setColor(new Color(0, 0, 0, .5f));
		g.fillRect(0, 0, 5000f, 5000f); // 5000 is the max dimension of a screen (I hope)
		// Draw inventory title
		g.setColor(config.titleColor);
		g.setFont(config.titleFont);
		g.drawString(config.title, config.titleLocation.x, config.titleLocation.y);
	}

	private void drawItems(Graphics g) {
		float blockSpace = config.slotSize + config.slotMargin;
		for (int i = 0; i < config.numSlotsWide; i++) {
			for (int j = 0; j < config.numSlotsHigh; j++) {
				Vector2f topLeft = config.location.copy()
						.add(new Vector2f(i, j).scale(blockSpace));
				Rectangle itemBackground = new Rectangle(
						topLeft.x, topLeft.y,
						config.slotSize, config.slotSize);
				g.setColor(config.backgroundColor);
				g.fill(itemBackground);

				if (items[i][j] != null) {
					Sprite sprite = items[i][j].getIcon();
					Image icon = sprite.getScaledImage((int) config.iconDimensions.x + 1,
							(int) config.iconDimensions.y + 1);
					Vector2f trueCenter = new Vector2f(config.slotSize, config.slotSize)
							.scale(.5f).add(topLeft);
					Vector2f imgMiddle = new Vector2f(icon.getWidth() / 2,
							icon.getHeight() / 2);
					Vector2f imgLoc = topLeft.copy().add(trueCenter)
							.sub(topLeft.copy().add(imgMiddle));
					g.drawImage(icon, imgLoc.x, imgLoc.y);

					g.setColor(config.numberColor);
					g.setFont(config.numberFont);
					Vector2f numPos = config.numberDisplacement.copy().add(topLeft);
					g.drawString(Integer.toString(items[i][j].getCount()),
							numPos.x, numPos.y);
				}
			}
		}
	}

	public Point convertScreenPosToInventoryItem(Vector2f pos) {
		Vector2f invPos = pos.copy().sub(config.location);
		float slotSpace = config.slotMargin + config.slotSize;
		int x = (int) Math.floor(invPos.x / slotSpace);
		int y = (int) Math.floor(invPos.y / slotSpace);
		if (x >= config.numSlotsWide || x < 0 || y >= config.numSlotsHigh || y < 0) {
			return null;
		}
		Vector2f slotPos = new Vector2f(invPos.x % slotSpace, invPos.y % slotSpace);
		if (Math.max(slotPos.x, slotPos.y) > config.slotSize) {
			return null;
		}
		return new Point(x, y);
	}

	public Item getItem(Point pos) {
		try {
			InventoryItem invItem = items[pos.x][pos.y];
			if (invItem == null) {
				return null;
			}
			return invItem.getItem();
		} catch (ArrayIndexOutOfBoundsException e) {
			// shady method to ensure the position is in bounds
			return null;
		}
	}
}
