package game.world;

import java.awt.Point;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.PriorityQueue;
import java.util.TreeMap;

import game.blocks.Block;
import game.blocks.BlockType;
import game.blocks.LiquidBlock;
import game.blocks.SolidBlock;

/**
 * More utility methods
 *
 * @author rober_000
 *
 */
public class Lighting {

	/**
	 * Performs lighting updates from the "sun".
	 *
	 * @param xStart
	 *            X-coordinate to start at
	 * @param xEnd
	 *            X-coordinate to end at
	 * @param strength
	 *            Strength of the light
	 */
	static void doSunLighting(TreeMap<Point, Block> blocks, int xStart, int xEnd,
			int yStart, int yEnd, int strength) {
		PriorityQueue<Point> sources = new PriorityQueue<>(
				(a, b) -> blocks.get(b).getLighting() - blocks.get(a).getLighting());

		for (int i = xStart; i <= xEnd; i++) {
			Point start = new Point(i, 0);
			Point end = new Point(i, yEnd);

			if (blocks.comparator().compare(start, end) > 0) {
				// apparently navigableKeySet().subset() crashes if start is
				// after end
				continue;
			}
			NavigableSet<Point> allBlocks = blocks.navigableKeySet()
					.subSet(start, true, end, true);
			HashSet<Point> visited = new HashSet<>();
			for (Point p : allBlocks) {
				blocks.get(p).setLighting(0);
			}
			for (Point p : allBlocks) {
				Block b = blocks.get(p);

				visited.add(p);
				if (BlockType.isSeeThrough(b.type)) {
					b.setLighting(strength);

					sources.add(p);
				} else {
					break;
				}
			}

			for (Point p : allBlocks) {
				Block b = blocks.get(p);
				if (BlockType.getLightValue(b) != -1
						&& BlockType.getLightValue(b) > b.getLighting()) {
					b.setLighting(BlockType.getLightValue(b));

					if (visited.contains(p)) {
						sources.remove(p);
					}
					sources.add(p);
				}
			}
		}

		propagateLighting(blocks, sources, xStart, xEnd, yStart, yEnd);
	}

	private static void propagateLighting(TreeMap<Point, Block> blocks,
			PriorityQueue<Point> lightSources, int xStart,
			int xEnd, int yStart, int yEnd) {
		HashSet<Point> visited = new HashSet<>();
		visited.addAll(lightSources);

		int[][] cardinalDirections = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
		while (!lightSources.isEmpty()) {
			Point curr = lightSources.poll();
			if (blocks.get(curr).getLighting() <= 0) {
				continue;
			}

			for (int[] dir : cardinalDirections) {
				Point next = new Point(curr.x + dir[0], curr.y + dir[1]);
				if (!visited.contains(next) && blocks.containsKey(next)) {
					if (next.x >= xStart && next.x <= xEnd && next.y >= yStart
							&& next.y <= yEnd) {

						int str = blocks.get(curr).getLighting() - 4;
						if (blocks.get(next) instanceof LiquidBlock) {
							str -= 2;
						}
						if (blocks.get(next) instanceof SolidBlock) {
							str -= 10;
						}
						str = Math.max(str, 0);

						blocks.get(next).setLighting(str);

						lightSources.add(next);
						visited.add(next);
					}
				}

			}
		}
	}
}
