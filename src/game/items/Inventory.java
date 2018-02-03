package game.items;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tests.xml.Item;

import game.Viewport;

public class Inventory {
	// number of slots wide and high the inventory display will be
	private final int SLOT_WIDTH = 10;
	private final int SLOT_HEIGHT = 10;
	private final int MAX_SIZE = SLOT_WIDTH * SLOT_HEIGHT;
	private final Color INVENTORY_COLOR = new Color(63f / 255, 65f / 255f, 150f / 255f);
	private Map<Item, Integer> items = new HashMap<>();

	public Inventory() {
	}

	public void addItem(Item item) {
		items.put(item, items.getOrDefault(item, 0));
	}

	public void draw(Viewport vp) {
		drawBackground(vp);
	}

	private void drawBackground(Viewport vp) {
		Graphics g = vp.getGraphics();
		g.setColor(Color.red);
		Vector2f inventoryLocation = new Vector2f(50, 50);
		float blockWidth = 70;
		float blockMargin = 20;
		float blockSpace = blockWidth + blockMargin;
		for (int i = 0; i < SLOT_WIDTH; i++) {
			for (int j = 0; j < SLOT_HEIGHT; j++) {
				Rectangle rect = new Rectangle(inventoryLocation.x + i * blockSpace,
						inventoryLocation.y + j * blockSpace, blockWidth, blockWidth);
				g.setColor(INVENTORY_COLOR);
				g.fill(rect);
			}
		}
	}
}
