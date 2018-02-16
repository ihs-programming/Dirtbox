package game.save;

import java.awt.Point;
import java.util.ArrayList;
import java.util.TreeMap;

import org.newdawn.slick.geom.Rectangle;

import game.blocks.Block;
import game.blocks.BlockType;
import game.generation.RegionGenerator;
import game.world.World;

public class Saver {
	public void save(World w, RegionGenerator rg) {
		ArrayList<String> arraylist = new ArrayList<>();
		blocksToArrayList(arraylist, w.getBlocks(), rg.generatedblocks);
	}

	private String BlockToString(Block block) {
		return block.type.toString() + " " + String.valueOf(block.getPos().x) + " "
				+ String.valueOf(block.getPos().y);
	}

	private Block stringToBlock(String blockstring) {
		BlockType blocktype = BlockType
				.valueOf(blockstring.substring(0, blockstring.indexOf(" ")));
		float xpos = Float.parseFloat(blockstring.substring(1, blockstring.indexOf(" ")));
		float ypos = Float.parseFloat(blockstring.substring(2, blockstring.indexOf(" ")));
		return Block.createBlock(blocktype, xpos, ypos);
	}

	private TreeMap<Point, Block> arrayListToBlocks(ArrayList<String> savearraylist) {
		return null;
	}

	private ArrayList<String> blocksToArrayList(ArrayList<String> savearraylist,
			TreeMap<Point, Block> blocks, Rectangle generatedblocks) {
		Point curpos = new Point();
		for (int x = 0; x < generatedblocks.getWidth(); x++) {
			for (int y = 0; y < generatedblocks.getHeight(); y++) {
				curpos.setLocation(x, y);
				savearraylist.add(BlockToString(blocks.get(curpos)));
			}
		}
		savearraylist.add("END OF BLOCKS");
		return savearraylist;
	}
}
