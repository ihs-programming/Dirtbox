package game.items;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tests.xml.Item;

import game.Viewport;

public class Inventory {
	// number of slots wide and high the inventory display will be
	private InventoryConfig config = new InventoryConfig();
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
		g.setColor(new Color(0, 0, 0, .5f));
		g.fillRect(0, 0, 5000f, 5000f); // 5000 is the max dimension of a screen (I hope)
		// Draw inventory title
		g.setColor(config.titleColor);
		g.setFont(config.titleFont);
		g.drawString(config.title, config.titleLocation.x, config.titleLocation.y);

		g.setColor(Color.red);
		float blockWidth = 70;
		float blockMargin = 20;
		float blockSpace = blockWidth + blockMargin;
		for (int i = 0; i < config.slot_width; i++) {
			for (int j = 0; j < config.slot_height; j++) {
				Rectangle rect = new Rectangle(config.location.x + i * blockSpace,
						config.location.y + j * blockSpace, blockWidth, blockWidth);
				g.setColor(config.color);
				g.fill(rect);
			}
		}
	}
}
