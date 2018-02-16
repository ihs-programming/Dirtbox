package game.items;

import game.Sprite;

/**
 * Represents the item in general
 */
public abstract class Item {
	public abstract Sprite getIcon();

	public abstract boolean equals(Item otherItem);
}
