package game.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Vector2f;

import game.items.Item;

/**
 * A dropped item on the ground
 */
public class CollectibleItem extends Entity {
	private Item item;

	public CollectibleItem(Item item, Vector2f pos) {
		super(getItemSpriteSheet(item), pos);
		this.item = item;
	}

	private static SpriteSheet getItemSpriteSheet(Item item) {
		Image icon = item.getIcon();
		return new SpriteSheet(icon, icon.getWidth(), icon.getHeight());
	}
}
