package game.world;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import org.newdawn.slick.geom.Vector2f;

import game.blocks.Block;
import game.blocks.BlockType;
import game.blocks.LiquidBlock;

/**
 * Provides utility methods for World
 *
 * @author rober_000
 *
 */
public class BlockUpdates {
	private static void propagateFallingBlocks(Set<Point> changedBlocks,
			TreeMap<Point, Block> blocks,
			HashSet<Point> queue) {

		Iterator<Point> iter = changedBlocks.iterator();
		while (iter.hasNext()) {
			Point p = iter.next();
			Point above = new Point(p.x, p.y - 1);
			if (blocks.containsKey(above) && blocks.containsKey(p)) {
				if (blocks.get(p).type == BlockType.EMPTY
						|| blocks.get(p) instanceof LiquidBlock) {
					Block onTop = blocks.get(above);
					if (onTop.type == BlockType.WATER || onTop.type == BlockType.SAND) {
						swapBlocks(blocks, p, above);

						addAround(queue, p);
					}
				}
			}
		}
	}

	static void propagateLiquids(Set<Point> changedBlocks,
			TreeMap<Point, Block> blocks) {
		HashSet<Point> queue = new HashSet<>();
		propagateFallingBlocks(changedBlocks, blocks, queue);

		HashSet<Point> possibleLiquids = new HashSet<>();
		for (Point p : changedBlocks) {
			addAround(possibleLiquids, p);
		}
		for (Point p : possibleLiquids) {
			Block b = blocks.get(p);
			if (b instanceof LiquidBlock) {
				// Hmm.. Might as well use it? This probably isn't how you're
				// supposed to tho.
				Block under = blocks.get(new Point(p.x, p.y + 1));
				// First try flowing under
				if (under != null && under.type == BlockType.EMPTY) {
					swapBlocks(blocks, p, new Point(p.x, p.y + 1));
					addAllAdjacentWater(blocks, new Point(p.x, p.y), queue);
				} else {
					// Flow to the left
					Point curr = new Point(p.x - 1, p.y);
					Point currBot = new Point(p.x - 1, p.y + 1);
					while (blocks.containsKey(curr) && blocks.containsKey(currBot)) {
						if (!flowThrough(blocks.get(curr))) {
							break;
						}
						if (blocks.get(currBot).type == BlockType.EMPTY) {
							break;
						}
						curr.x--;
						currBot.x--;
					}
					if (blocks.containsKey(curr) && flowThrough(blocks.get(curr))) {
						swapBlocks(blocks, p, curr);
						addAllAdjacentWater(blocks, new Point(p.x, p.y), queue);
					} else {
						// Try flowing to the right
						curr = new Point(p.x + 1, p.y);
						currBot = new Point(p.x + 1, p.y + 1);
						while (blocks.containsKey(curr) && blocks.containsKey(currBot)) {
							if (!flowThrough(blocks.get(curr))) {
								break;
							}
							if (blocks.get(currBot).type == BlockType.EMPTY) {
								break;
							}
							curr.x++;
							currBot.x++;
						}
						if (blocks.containsKey(curr) && flowThrough(blocks.get(curr))) {
							swapBlocks(blocks, p, curr);
							addAllAdjacentWater(blocks, new Point(p.x, p.y), queue);
						}
					}
				}
			}
		}

		changedBlocks.clear();
		changedBlocks.addAll(queue);
	}

	private static boolean flowThrough(Block b) {
		return b.type == BlockType.EMPTY;
	}

	private static void swapBlocks(TreeMap<Point, Block> blocks, Point a, Point b) {
		if (blocks.containsKey(a) && blocks.containsKey(b)) {
			Block first = blocks.get(a);
			Block second = blocks.get(b);
			Vector2f oriPos = first.getPos();
			Vector2f newPos = second.getPos();
			blocks.put(a, Block.createBlock(second.type, oriPos.x, oriPos.y));
			blocks.put(b, Block.createBlock(first.type, newPos.x, newPos.y));
		}
	}

	private static void addAllAdjacentWater(TreeMap<Point, Block> blocks, Point start,
			Set<Point> queue) {
		Queue<Point> all = new LinkedList<>();
		HashSet<Point> ret = new HashSet<>();

		all.add(start);
		while (!all.isEmpty()) {
			Point curr = all.poll();
			int[][] cardinalDirections = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
			for (int[] dir : cardinalDirections) {
				Point next = new Point(curr.x + dir[0], curr.y + dir[1]);

				if (!ret.contains(next)) {
					if (blocks.containsKey(next)) {
						if (blocks.get(next) instanceof LiquidBlock) {
							all.add(next);
							ret.add(next);
						}
					}
				}
			}
		}
		queue.addAll(ret);
	}

	private static void addAround(Set<Point> queue, Point p) {
		for (int i = -1; i <= 1; i++) {
			for (int z = -1; z <= 1; z++) {
				queue.add(new Point(p.x + i, p.y + z));
			}
		}
	}
}
