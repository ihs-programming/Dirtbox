package game.items;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Vector2f;

public class InventoryConfig {
	public int slot_width = 10;
	public int slot_height = 5;
	public int inventory_items = slot_width * slot_height;
	public Color color = new Color(63f / 255, 65f / 255f, 150f / 255f);
	public Vector2f location = new Vector2f(50, 100);

	public Color titleColor = new Color(255, 255, 255);
	public Vector2f titleLocation = new Vector2f(150, 30);
	public String title = "Inventory";
	public Font titleFont = new TrueTypeFont(
			new java.awt.Font("Times New Roman", java.awt.Font.BOLD, 36), false);
}
