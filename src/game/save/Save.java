package game.save;

import java.awt.Point;
import java.util.ArrayList;
import java.util.TreeMap;

import org.newdawn.slick.geom.Rectangle;

import game.blocks.Block;
import game.generation.RegionGenerator;
import game.world.World;

public class Save {
	public static void save() {
		ArrayList<String> arraylist = new ArrayList<>();
		BlocksToArrayList(arraylist, World.getBlocks(), RegionGenerator.generatedblocks);
	}

	private static String BlockToString(Block block) {
		return block.type.toString() + " " + String.valueOf(block.getPos().x) + " "
				+ String.valueOf(block.getPos().y);
	}

	private static ArrayList<String> BlocksToArrayList(ArrayList<String> savearraylist,
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
