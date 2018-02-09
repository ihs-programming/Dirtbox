package game.entities;

import org.newdawn.slick.geom.Vector2f;

import game.Viewport;
import game.items.Item;

/**
 * A dropped item on the ground
 */
public class CollectibleItem extends Entity {
	private Item item;

	public CollectibleItem(Item item, Vector2f pos) {
		super(item.getIcon(), pos);
		this.item = item;
	}

	@Override
	public void draw(Viewport vp) {
		super.draw(vp);
		System.out.println(sprite.img.getWidth());
	}
}
