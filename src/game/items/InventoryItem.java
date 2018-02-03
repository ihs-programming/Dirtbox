package game.items;

import org.newdawn.slick.Image;

/**
 * Specific details about the item that the inventory keeps track of
 *
 */
public class InventoryItem {
	private int number;
	private Item item;

	public InventoryItem(int number, Item item) {
		this.number = number;
		this.item = item;
	}

	public InventoryItem(Item item) {
		this(0, item);
	}

	/**
	 * @return whether or not the item was added
	 */
	public boolean addItem(Item item) {
		if (this.item.equals(item)) {
			number++;
			return true;
		}
		return false;
	}

	public Image getIcon() {
		return item.getIcon();
	}

	public int getCount() {
		return number;
	}
}
