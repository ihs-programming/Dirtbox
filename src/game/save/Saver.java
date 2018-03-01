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

	public Rectangle generatedBlocks(World w) {
		return new Rectangle(w.getFirstBlock().x, w.getFirstBlock().y,
				w.getLastBlock().x - w.getFirstBlock().x,
				w.getLastBlock().y - w.getFirstBlock().y);
	}

	public void save(World w, RegionGenerator rg) {
		arraylist = blocksToArrayList(arraylist, w.getBlocks(), generatedBlocks(w));
	}

	public void load(World w) {
		w.setBlocks(arrayListToBlocks(arraylist));
	}

	private String BlockToString(Block block) {
		return block.type.toString() + " " + String.valueOf((int) block.getPos().x) + " "
				+ String.valueOf((int) block.getPos().y);
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
			if (blockstring.equals("END OF BLOCKS")) {
				break;
			}
			String[] stringarray = blockstring.split(" ");
			xpos = Float.parseFloat(stringarray[1]);
			ypos = Float.parseFloat(stringarray[2]);
			currentpoint = new Point(Math.round(xpos), Math.round(ypos));
			blocks.put(currentpoint, stringToBlock(blockstring));
		}
		return blocks;
	}

	private ArrayList<String> blocksToArrayList(ArrayList<String> savearraylist,
			TreeMap<Point, Block> blocks, Rectangle generatedblocks) {
		Point curpos = new Point();
		System.out
				.println(generatedblocks.getMinX() + " to " + generatedblocks.getMaxX());
		for (int x = (int) Math.floor(generatedblocks.getMinX()); x < generatedblocks
				.getMaxX(); x++) {
			for (int y = (int) Math.floor(generatedblocks.getMinY()); y < generatedblocks
					.getMaxY(); y++) {
				curpos.setLocation(x, y);
				savearraylist.add(BlockToString(blocks.get(curpos)));
			}
		}
		savearraylist.add("END OF BLOCKS");
		return savearraylist;
	}
}
