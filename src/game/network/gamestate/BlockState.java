package game.network.gamestate;

import java.awt.Point;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeMap;

import org.newdawn.slick.geom.Rectangle;

import game.blocks.Block;
import game.generation.RegionGenerator;
import game.save.Saver;

/**
 * Provide wrapper functions for querying and loading blocks.
 *
 * @author s-chenrob
 *
 */
public class BlockState {
	public static final Comparator<Point> pointComparer = (p1, p2) -> {
		if (p1.x == p2.x) {
			return p1.y - p2.y;
		}
		return p1.x - p2.x;
	};
	private TreeMap<Point, Block> blocks = new TreeMap<>(pointComparer);
	public RegionGenerator regionGenerator;

	public BlockState() {
		regionGenerator = new RegionGenerator(blocks);
	}

	/**
	 * Returns blocks serialized according to game.save.Saver.
	 *
	 * @return
	 */
	public byte[] getBlocks(Rectangle rect) {
		regionGenerator.generate(rect);
		TreeMap<Point, Block> allBlocks = getVisibleBlocks(rect);
		return Saver.serializeBlocks(allBlocks);
	}

	/**
	 * Copied from World.java. Don't know if that makes it bad code... Maybe have a
	 * Util method for this stuff?
	 *
	 * @param view
	 * @return
	 */
	public TreeMap<Point, Block> getVisibleBlocks(Rectangle view) {
		TreeMap<Point, Block> blockLocs = new TreeMap<>(BlockState.pointComparer);
		for (int i = (int) (view.getMinX() - 1); i <= view.getMaxX(); i++) {
			Point start = new Point(i, (int) (view.getMinY() - 1));
			Point end = new Point(i, (int) (view.getMaxY() + 1));
			NavigableSet<Point> existingBlocks = blocks.navigableKeySet().subSet(
					start,
					true, end, true);
			for (Point p : existingBlocks) {
				blockLocs.put(p, blocks.get(p));
			}
		}
		return blockLocs;
	}
}
