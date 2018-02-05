package game.items;

import org.newdawn.slick.Image;

import game.blocks.Block;
import game.blocks.BlockType;

public class BlockItem extends Item {
	private Block block;

	public BlockItem(Block b) {
		block = b;
	}

	@Override
	public Image getIcon() {
		return block.getImage();
	}

	@Override
	public boolean equals(Item otherItem) {
		if (otherItem instanceof BlockItem) {
			return block.type == ((BlockItem) otherItem).getBlockType();
		}
		return false;
	}

	public BlockType getBlockType() {
		return block.type;
	}
}
