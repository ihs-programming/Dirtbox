package game.items;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Vector2f;

import game.utils.FontUtil;

/**
 * Various parameters that define how the inventory looks on screen
 */
public class InventoryConfig {
	public int numSlotsWide = 10;
	public int numSlotsHigh = 5;
	public int inventory_items = numSlotsWide * numSlotsHigh;
	public float slotSize = 70;
	public float slotMargin = 20;
	public Color backgroundColor = new Color(63f / 255, 65f / 255f, 150f / 255f);
	public Vector2f location = new Vector2f(50, 100);

	public Color titleColor = new Color(255, 255, 255);
	public Vector2f titleLocation = new Vector2f(150, 30);
	public String title = "Inventory";
	public Font titleFont = FontUtil.getDefaultFont(36, true);

	public Vector2f iconDimensions = new Vector2f(50, 50);
	public Vector2f numberDisplacement = new Vector2f(55, 50);
	public Font numberFont = FontUtil.getDefaultFont(16, true);
	public Color numberColor = new Color(0xFFFFFFFF);
}
