package game.items;

import org.newdawn.slick.Image;

import game.blocks.Block;
import game.blocks.BlockType;

public class BlockItem extends Item {
	private final float ICON_SIZE = .5f;
	private Block block;

	public BlockItem(Block b) {
		block = b;
	}

	public BlockItem(BlockType type) {
		this(Block.createBlock(type, 0, 0));
	}

	@Override
	public Image getIcon() {
		return block.getImage().getScaledCopy(ICON_SIZE);
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
