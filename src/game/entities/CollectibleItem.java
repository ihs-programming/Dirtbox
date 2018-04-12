package game.entities;

import org.newdawn.slick.geom.Vector2f;

import game.Viewport;
import game.items.Item;
import game.world.GameWorld;

/**
 * A dropped item on the ground
 */
public class CollectibleItem extends Entity {
	private final float GRAVITY = .00005f;
	private Item item;

	public CollectibleItem(Item item, Vector2f pos, GameWorld w) {
		super(item.getIcon(), pos, w);
		this.item = item;
		accel.y = GRAVITY;
	}

	@Override
	public void draw(Viewport vp) {
		super.draw(vp);
	}

	public Item getItem() {
		return item;
	}
}
