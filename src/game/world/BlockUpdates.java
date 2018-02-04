package game.world;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
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
				if (blocks.get(p).type == BlockType.EMPTY) {
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
				Optional<Block> under = Optional.of(blocks.get(new Point(p.x, p.y + 1)));
				if (under.isPresent()) {
					swapBlocks(blocks, p, new Point(p.x, p.y + 1));
					addAround(queue, p);
				}
			}
		}

		changedBlocks.clear();
		changedBlocks.addAll(queue);
	}

	private static void swapBlocks(TreeMap<Point, Block> blocks, Point a, Point b) {
		if (blocks.containsKey(a) && blocks.containsKey(b)) {
			Block first = blocks.get(a);
			Block second = blocks.get(b);
			if (first.type == BlockType.EMPTY || second.type == BlockType.EMPTY) {
				Vector2f oriPos = first.getPos();
				Vector2f newPos = second.getPos();
				blocks.put(a, Block.createBlock(second.type, oriPos.x, oriPos.y));
				blocks.put(b, Block.createBlock(first.type, newPos.x, newPos.y));
			}
		}
	}

	private static void addAround(Set<Point> queue, Point p) {
		for (int i = -1; i <= 1; i++) {
			for (int z = -1; z <= 1; z++) {
				queue.add(new Point(p.x + i, p.y + z));
			}
		}
	}
}
