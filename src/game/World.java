package game;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.blocks.Block;
import game.blocks.LiquidBlock;
import game.blocks.SolidBlock;
import game.entities.ControllableCharacter;
import game.entities.Entity;
import game.generation.RegionGenerator;
import game.utils.Geometry;

public class World {

	static final double DAY_NIGHT_DURATION = 1200000.0;
	private static final Comparator<Point> pointComparer = (p1, p2) -> {
		if (p1.x == p2.x) {
			return p1.y - p2.y;
		}
		return p1.x - p2.x;
	};

	private ArrayList<Entity> characters;
	private ArrayList<Entity> backgroundsprites;
	private ControllableCharacter controlledCharacter;

	private static Image sunsprite;

	private boolean enableFOV = true;
	private Input userInp = null; // used only for debugging purposes currently

	public TreeMap<Point, Block> blocks = new TreeMap<>(pointComparer);

	public World() {
		characters = new ArrayList<>();
		backgroundsprites = new ArrayList<>();
		try {
			sunsprite = new Image("data/characters/sunsprite.png");
			sunsprite.setFilter(Image.FILTER_NEAREST);
			sunsprite = sunsprite.getScaledCopy(4, 4);
			Entity suns = new Entity(sunsprite, 1, 1, new Vector2f(0, 0));
			backgroundsprites.add(suns);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a world that contains elements that change with user input
	 *
	 * @param inp
	 */
	public World(Input inp) {
		this();
		userInp = inp;
		try {
			Image stalinsprite = new Image("data/characters/stalin.png");
			stalinsprite.setFilter(Image.FILTER_NEAREST);
			stalinsprite = stalinsprite.getScaledCopy(1, 2);
			ControllableCharacter stalin = new ControllableCharacter(stalinsprite, 1, 1,
					new Vector2f(0, 0), inp);
			characters.add(stalin);
			controlledCharacter = stalin;
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public void addEntity(Entity e) {
		characters.add(e);
		backgroundsprites.add(e);
	}

	public void updateEntities(Viewport vp) {
		updateSun(vp);
	}

	private void updateSun(Viewport vp) {
		Entity suns = new Entity(World.sunsprite, 1, 1, new Vector2f((float) -(Math
				.cos(2.0 * Math.PI * Viewport.globaltimer
						/ World.DAY_NIGHT_DURATION)
				* 15 - vp.getCenter().x + sunsprite.getScaledCopy(4, 4).getWidth() / 2),
				(float) -(Math
						.sin(2.0 * Math.PI * Viewport.globaltimer
								/ World.DAY_NIGHT_DURATION)
						* 15) + 30));
		backgroundsprites.set(0, suns);
	}

	public void draw(Viewport vp) {

		updateEntities(vp);

		for (Entity e : this.backgroundsprites) {
			e.draw(vp);
		}

		Shape view = vp.getGameViewShape();
		Rectangle viewRect = Geometry.getBoundingBox(view);

		doSunLighting((int) viewRect.getX() - 10,
				(int) (viewRect.getX() + view.getWidth()) + 10,
				(int) viewRect.getY() - 10,
				(int) (viewRect.getY() + view.getHeight()) + 10,
				63);

		new RegionGenerator(viewRect, blocks);

		/*
		 * The following three lines somehow randomly cause up to 1000 ms of lag This is
		 * a big issue, as the game otherwise runs quite smoothly. Please fix!
		 * "734.582767 ms for draw (!!!) 743.448732 ms for render"
		 */
		List<Point> visibleBlocks = getVisibleBlockLocations(viewRect);
		if (enableFOV) {
			visibleBlocks = calculateVisibleBlocks(visibleBlocks);
		}
		for (Point p : visibleBlocks) {
			blocks.get(p).draw(vp);
		}
		for (Point p : visibleBlocks) {
			blocks.get(p).drawShading(vp);
		}
		if (Viewport.DEBUG_MODE) {
			renderHitboxes(vp);
			renderMouseRaytrace(vp);
		}
		for (Entity e : this.characters) {
			e.draw(vp);
		}
	}

	private void renderMouseRaytrace(Viewport vp) {
		if (userInp == null) {
			System.out.println("Unable to render raytracing");
			return;
		}
		Vector2f mousePos = new Vector2f(userInp.getMouseX(), userInp.getMouseY());
		mousePos = vp.getInverseDrawTransform().transform(mousePos);
		List<Point> points = rayTrace(getCharacterPosition(), mousePos);
		vp.draw(Geometry.createCircle(getCharacterPosition(), .2f), Color.cyan);
		vp.draw(Geometry.createCircle(mousePos, .2f), Color.cyan);
		for (Point p : points) {
			vp.draw(new Rectangle(p.x, p.y, 1, 1), Color.red);
		}
	}

	private List<Point> calculateVisibleBlocks(List<Point> visiblePoints) {
		Queue<Point> pq = new ArrayDeque<>(visiblePoints);
		ArrayList<Point> visibleBlocks = new ArrayList<>();
		while (!pq.isEmpty()) {
			Point p = pq.poll();
			Rectangle blockRect = new Rectangle(
					p.x, p.y, Block.BLOCK_SPRITE_SIZE, Block.BLOCK_SPRITE_SIZE);
			boolean visible = false;
			for (int i = 0; i < blockRect.getPointCount(); i++) {
				Vector2f blockCorner = new Vector2f(blockRect.getPoint(i));
				List<Point> ray = rayTrace(getCharacterPosition(), blockCorner);
				boolean blockIsVisible = true;
				for (Point rp : ray) {
					if (!rp.equals(p) && blocks.get(rp) instanceof SolidBlock) {
						blockIsVisible = false;
						break;
					}
				}
				if (blockIsVisible) {
					visible = true;
					break;
				}
			}
			if (visible) {
				visibleBlocks.add(p);
			}
		}
		return visibleBlocks;
	}

	/**
	 * Return list of block locations that would be hit on the route of the line
	 * going from start and past end
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	private List<Point> rayTrace(Vector2f start, Vector2f end) {
		HashSet<Point> points = new HashSet<>();
		for (int x = (int) end.x; insideRange(x, start.x,
				end.x); x += end.x < start.x ? 1 : -1) {
			// calculates y value based off point slope formula
			float actualY = (end.y - start.y) / (end.x - start.x) * (x - start.x)
					+ start.y;
			int y = (int) actualY;
			points.add(new Point(x, y));
		}
		for (int y = (int) end.y; insideRange(y, start.y,
				end.y); y += end.y < start.y ? 1 : -1) {
			// calculates x value based off point slope formula
			float actualX = (y - start.y) * (end.x - start.x) / (end.y - start.y)
					+ start.x;
			int x = (int) actualX;
			points.add(new Point(x, y));
		}
		ArrayList<Point> pointList = new ArrayList<>(points);
		Collections.sort(pointList, (o1, o2) -> (int) Math
				.signum(o2.distance(start.x, start.y) - o1.distance(start.x, start.y)));
		return pointList;
	}

	private boolean insideRange(float x, float l, float r) {
		if (Math.abs(l - r) < 1) {
			return false;
		}
		if (l < r) {
			return x > l;
		} else {
			return x < l;
		}
	}

	/**
	 * Performs lighting updates from the "sun". Takes less than 30 ms.
	 *
	 * @param xStart
	 *            X-coordinate to start at
	 * @param xEnd
	 *            X-coordinate to end at
	 * @param strength
	 *            Strength of the light
	 */
	private void doSunLighting(int xStart, int xEnd, int yStart, int yEnd, int strength) {
		PriorityQueue<Point> sources = new PriorityQueue<>(
				(a, b) -> blocks.get(b).getLighting() - blocks.get(a).getLighting());

		for (int i = xStart; i <= xEnd; i++) {
			Point start = new Point(i, 0);
			Point end = new Point(i, yEnd);

			if (pointComparer.compare(start, end) > 0) {
				// apparently navigableKeySet().subset() crashes if start is
				// after end
				continue;
			}
			NavigableSet<Point> allBlocks = blocks.navigableKeySet()
					.subSet(start, true, end, true);

			for (Point p : allBlocks) {
				Block b = blocks.get(p);
				if (b instanceof SolidBlock || b instanceof LiquidBlock) {
					break;
				}
				b.setLighting(strength);

				sources.add(p);
			}
		}

		propagateLighting(sources, xStart, xEnd, yStart, yEnd);
	}

	private void propagateLighting(PriorityQueue<Point> lightSources, int xStart,
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
						if (blocks.get(curr) instanceof LiquidBlock) {
							str -= 2;
						}
						if (blocks.get(curr) instanceof SolidBlock) {
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

	public List<Point> getVisibleBlockLocations(Rectangle view) {
		ArrayList<Point> blockLocs = new ArrayList<>();
		for (int i = (int) (view.getMinX() - 1); i <= view.getMaxX(); i++) {
			Point start = new Point(i, (int) (view.getMinY() - 1));
			Point end = new Point(i, (int) (view.getMaxY() + 1));
			NavigableSet<Point> existingBlocks = blocks.navigableKeySet()
					.subSet(start, true, end, true);
			for (Point p : existingBlocks) {
				blockLocs.add(p);
			}
		}
		return blockLocs;
	}

	public Vector2f getCharacterPosition() {
		if (controlledCharacter != null) {
			return controlledCharacter.getLocation();
		}
		return new Vector2f();
	}

	public void update(int delta) {
		for (Entity e : characters) {
			e.update(delta);
		}

		// collision detection for main character
		Shape hitbox = controlledCharacter.getHitbox();
		Rectangle boundingBox = Geometry.getBoundingBox(hitbox);
		List<Point> collidingBlocks = getVisibleBlockLocations(boundingBox);
		for (Point p : collidingBlocks) {
			Block b = blocks.get(p);
			if (b instanceof SolidBlock) {
				controlledCharacter.collide(b.getHitbox());
			}
		}
	}

	/**
	 * May be useful for debugging hitbox locations
	 *
	 * @param vp
	 */
	private void renderHitboxes(Viewport vp) {
		vp.draw(controlledCharacter.getHitbox(), Color.red);
		Shape hitbox = controlledCharacter.getHitbox();
		Rectangle boundingBox = Geometry.getBoundingBox(hitbox);
		List<Point> collidingBlocks = getVisibleBlockLocations(boundingBox);
		for (Point p : collidingBlocks) {
			Block b = blocks.get(p);
			vp.draw(b.getHitbox(), Color.white);
			vp.draw(Geometry.createCircle(b.getPos(), .2f), Color.green);
		}
	}
}
