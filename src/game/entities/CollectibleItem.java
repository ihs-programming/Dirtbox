package game.entities;

import org.newdawn.slick.geom.Vector2f;

import game.items.Item;
import game.world.GameWorld;

/**
 * A dropped item on the ground
 */
public class CollectibleItem extends Entity {
	private Item item;

	public CollectibleItem(Item item, Vector2f pos, GameWorld w) {
		super(item.getIcon(), pos, w);
		this.item = item;
	}

	public Item getItem() {
		return item;
	}
}
