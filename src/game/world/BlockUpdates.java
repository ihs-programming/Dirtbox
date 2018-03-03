package game.world;

import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
	private static final int FLOW_SEARCH_LIMIT = 500;

	private static void propagateFallingBlocks(Set<Point> changedBlocks,
			TreeMap<Point, Block> blocks,
			HashSet<Point> queue) {

		List<Point> toFall = changedBlocks.stream().filter(p -> blocks.containsKey(p))
				.filter(p -> isEmpty(blocks.get(p)))
				.filter(p -> shouldFall(blocks.get(above(p))))
				.collect(Collectors.toList());

		for (Point p : toFall) {
			swapBlocks(blocks, p, above(p));

			addAround(queue, p);
		}
	}

	private static Point above(Point p) {
		return new Point(p.x, p.y - 1);
	}

	private static Point under(Point p) {
		return new Point(p.x, p.y + 1);
	}

	private static boolean isEmpty(Block b) {
		if (b == null) {
			return false;
		}
		return b.type == BlockType.EMPTY || b instanceof LiquidBlock;
	}

	private static boolean shouldFall(Block b) {
		return b.type == BlockType.SAND;
	}

	protected static void propagateLiquids(Set<Point> changedBlocks,
			TreeMap<Point, Block> blocks) {
		HashSet<Point> queue = new HashSet<>();
		propagateFallingBlocks(changedBlocks, blocks, queue);

		HashSet<Point> possibleLiquids = new HashSet<>();

		changedBlocks.forEach(p -> addAround(possibleLiquids, p));

		possibleLiquids.stream().filter(p -> blocks.get(p) instanceof LiquidBlock);
		for (Point p : possibleLiquids) {
			Block b = blocks.get(p);
			if (b instanceof LiquidBlock) {
				Block under = blocks.get(new Point(p.x, p.y + 1));
				// First try flowing under
				if (under != null && under.type == BlockType.EMPTY) {
					swapBlocks(blocks, p, new Point(p.x, p.y + 1));
					addAllAdjacentWater(blocks, new Point(p.x, p.y), queue);
				} else {
					Point flowTo = canFlow(
							IntStream.iterate(-1, n -> n - 1).limit(FLOW_SEARCH_LIMIT),
							p, blocks);
					if (flowTo != null) {
						swapBlocks(blocks, p, flowTo);
						addAllAdjacentWater(blocks, new Point(p.x, p.y), queue);
					} else {
						flowTo = canFlow(
								IntStream.iterate(1, n -> n + 1).limit(FLOW_SEARCH_LIMIT),
								p,
								blocks);
						if (flowTo != null) {
							swapBlocks(blocks, p, flowTo);
							addAllAdjacentWater(blocks, new Point(p.x, p.y), queue);
						}
					}
				}
			}
		}

		changedBlocks.clear();
		changedBlocks.addAll(queue);

	}

	private static Point canFlow(IntStream offset, Point start,
			TreeMap<Point, Block> blocks) {
		Optional<Point> end = offset.mapToObj(n -> new Point(start.x + n, start.y))
				.filter(p -> blocks.containsKey(p) && blocks.containsKey(under(p)))
				.filter(p -> !flowThrough(blocks.get(p))
						|| flowThrough(blocks.get(under(p))))
				.findFirst();
		if (!end.isPresent() || !flowThrough(blocks.get(end.get()))) {
			return null;
		}
		return end.get();
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
		if (queue.contains(start)) {
			return;
		}

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
