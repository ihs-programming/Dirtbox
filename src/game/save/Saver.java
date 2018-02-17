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
	private ArrayList<String> arraylist = new ArrayList<>();

	public void save(World w, RegionGenerator rg) {
		arraylist = blocksToArrayList(arraylist, w.getBlocks(), rg.generatedblocks);
	}

	public void load(World w) {
		System.out.println(BlockToString(w.getBlock(new Point(1, 1))));
		System.out.println(w.getBlock(new Point(1, 1)));
		System.out.println(w.getBlock(new Point(1, 1)).getSprite());
		System.out.println(stringToBlock(BlockToString(w.getBlock(new Point(1, 1)))));
		System.out.println(
				stringToBlock(BlockToString(w.getBlock(new Point(1, 1)))).getSprite());
		w.setBlocks(arrayListToBlocks(arraylist));
	}

	private String BlockToString(Block block) {
		return block.type.toString() + " " + String.valueOf(block.getPos().x) + " "
				+ String.valueOf(block.getPos().y);
	}

	private Block stringToBlock(String blockstring) {
		String[] stringarray = blockstring.split(" ");
		BlockType blocktype = BlockType
				.valueOf(stringarray[0]);
		float xpos = Float.parseFloat(stringarray[1]);
		float ypos = Float.parseFloat(stringarray[2]);
		return Block.createBlock(blocktype, xpos, ypos);
	}

	private TreeMap<Point, Block> arrayListToBlocks(ArrayList<String> savearraylist) {
		TreeMap<Point, Block> blocks = new TreeMap<>((p1, p2) -> {
			if (p1.x == p2.x) {
				return p1.y - p2.y;
			}
			return p1.x - p2.x;
		});
		Point currentpoint = new Point();
		String blockstring = null;
		float xpos = 0f;
		float ypos = 0f;
		for (int index = 0; index < Integer.MAX_VALUE; index++) {
			blockstring = savearraylist.get(index);
			if (blockstring == "END OF BLOCKS") {
				break;
			}
			String[] stringarray = blockstring.split(" ");
			xpos = Float.parseFloat(stringarray[1]);
			ypos = Float.parseFloat(stringarray[2]);
			currentpoint.setLocation(xpos, ypos);
			blocks.put(currentpoint, stringToBlock(blockstring));
		}
		return blocks;
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
