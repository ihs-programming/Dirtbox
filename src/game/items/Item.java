package game.items;

import org.newdawn.slick.Image;

/**
 * Represents the item in general
 */
public abstract class Item {
	public abstract Image getIcon();

	public abstract boolean equals(Item otherItem);
}
