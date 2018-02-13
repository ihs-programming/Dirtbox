package game.world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.Viewport;
import game.blocks.Block;
import game.blocks.BlockType;
import game.blocks.SolidBlock;
import game.entities.CollectibleItem;
import game.entities.ControllableCharacter;
import game.entities.Entity;
import game.entities.creature.Bunny;
import game.entities.creature.Wolf;
import game.generation.RegionGenerator;
import game.items.BlockItem;
import game.utils.Geometry;

public class World {
	public static final double DAY_NIGHT_DURATION = 1200000.0;
	private static final Comparator<Point> pointComparer = (p1, p2) -> {
		if (p1.x == p2.x) {
			return p1.y - p2.y;
		}
		return p1.x - p2.x;
	};

	public ArrayList<Entity> entitiesToAdd;
	public static ArrayList<Entity> entities;
	public static ArrayList<Entity> backgroundsprites;
	private ControllableCharacter controlledCharacter;

	private static Image sunsprite;
	private Entity sun;

	private Input userInp = null; // used only for debugging purposes currently

	private TreeMap<Point, Block> blocks = new TreeMap<>(pointComparer);

	/**
	 * A set of blocks that have been changed, and thus require updating.
	 */
	private Set<Point> changedBlocks = new HashSet<>();

	public World() {
		entities = new ArrayList<>();
		entitiesToAdd = new ArrayList<>();
		backgroundsprites = new ArrayList<>();
		addDefaultEntities();
	}

	private void addDefaultEntities() {
		try {
			sunsprite = new Image("data/characters/sunsprite.png");
			sunsprite.setFilter(Image.FILTER_NEAREST);
			sunsprite = sunsprite.getScaledCopy(4, 4);
			Image stalinsprite = new Image("data/characters/stalin.png");
			stalinsprite.setFilter(Image.FILTER_NEAREST);
			stalinsprite = stalinsprite.getScaledCopy(1, 2);
			ControllableCharacter stalin = new ControllableCharacter(this, stalinsprite,
					new Vector2f(0, 0));
			addEntity(stalin);

			for (int i = 0; i < 10; i++) {
				Image bunny = new Image("data/characters/rabbit.png");
				addEntity(new Bunny(new Sprite(bunny).scale(1f / bunny.getWidth()),
						new Vector2f(10 * i, 0)));
			}
			Sprite wolf = new Sprite("data/characters/woof.png");
			wolf.scale(2f / wolf.getWidth());
			addEntity(new Wolf(wolf, new Vector2f(0, 0)));
			controlledCharacter = stalin;
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Construct world for debugging purposes
	 *
	 * @param inp
	 */
	public World(Input inp) {
		this();
		userInp = inp;
	}

	public void addEntity(Entity e) {
		entitiesToAdd.add(e);
	}

	private void updateEntityList() {
		entities.addAll(entitiesToAdd);
		entitiesToAdd.clear();
	}

	private void updateSun(Viewport vp) {
		sun = new Entity(World.sunsprite, new Vector2f(
				(float) -(Math
						.cos(2.0 * Math.PI * Viewport.globaltimer
								/ World.DAY_NIGHT_DURATION)
						* 15 - vp.getCenter().x
						+ sunsprite.getScaledCopy(4, 4).getWidth() / 2),
				(float) -(Math.sin(
						2.0 * Math.PI * Viewport.globaltimer / World.DAY_NIGHT_DURATION)
						* 15) + 30));
	}

	public void draw(Viewport vp) {
		updateSun(vp);

		if (Viewport.day) {
			sun.draw(vp);
		}

		for (Entity e : World.backgroundsprites) {
			e.draw(vp);
		}

		Shape view = vp.getGameViewShape();
		Rectangle viewRect = Geometry.getBoundingBox(view);

		long time = System.currentTimeMillis();
		Lighting.doSunLighting(blocks, (int) viewRect.getX() - 10,
				(int) (viewRect.getX() + view.getWidth()) + 10,
				(int) viewRect.getY() - 10,
				(int) (viewRect.getY() + view.getHeight()) + 10, 63);
		if (Viewport.DEBUG_MODE) {
			System.out.printf("%d ms for sun lighting calculations.\n",
					System.currentTimeMillis() - time);
		}

		new RegionGenerator(viewRect, blocks);

		/*
		 * The following three lines somehow randomly cause up to 1000 ms of lag This is
		 * a big issue, as the game otherwise runs quite smoothly. Please fix!
		 * "734.582767 ms for draw (!!!) 743.448732 ms for render"
		 */
		List<Point> visibleBlocks = getVisibleBlockLocations(viewRect);
		time = System.currentTimeMillis();
		Block.draw_hit_count = 0;
		for (Point p : visibleBlocks) {
			blocks.get(p).draw(vp);
		}
		if (Viewport.DEBUG_MODE) {
			System.out.printf(
					"%d ms for visible | %d out of %d blocks rendered.\n",
					System.currentTimeMillis() - time, Block.draw_hit_count,
					visibleBlocks.size());
		}
		time = System.currentTimeMillis();
		for (Point p : visibleBlocks) {
			blocks.get(p).drawShading(vp);
		}
		synchronized (entities) {
			for (Entity e : entities) {
				e.draw(vp);
			}
		}
		if (Viewport.DEBUG_MODE) {
			System.out.printf("%d ms for shading\n",
					System.currentTimeMillis() - time);
			// renderHitboxes(vp);
			// renderMouseRaytrace(vp);
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
		vp.draw(new Line(getCharacterPosition(), mousePos), Color.green);
		for (Point p : points) {
			vp.draw(new Rectangle(p.x, p.y, 1, 1), Color.pink);
		}
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
		boolean increasing = end.x > start.x;
		Line viewLine = new Line(start, end);
		int delta = increasing ? 1 : -1;
		for (int x = floor(start.x) + delta; x < end.x ^ !increasing; x += delta) {
			// calculates y value based off point slope formula
			float actualY = (end.y - start.y) / (end.x - start.x) * (x - start.x)
					+ start.y;

			// Too lazy to figure out actual logic, so I'll just guess and check
			// around
			// the block to avoid edge cases
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					int nx = x + dx;
					int ny = (int) actualY + dy;
					Rectangle blockRect = new Rectangle(nx, ny, 1, 1);
					if (blockRect.intersects(viewLine)) {
						points.add(new Point(nx, ny));
					}
				}
			}
		}
		increasing = end.y > start.y;
		delta = increasing ? 1 : -1;
		for (int y = floor(start.y) + delta; y < end.y ^ !increasing; y += delta) {
			// calculates y value based off point slope formula
			float actualX = (y - start.y) * (end.x - start.x) / (end.y - start.y)
					+ start.x;

			// Too lazy to figure out actual logic, so I'll just guess and check
			// the block above and below the point as well
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					int nx = (int) actualX + dx;
					int ny = y + dy;
					Rectangle blockRect = new Rectangle(nx, ny, 1, 1);
					if (blockRect.intersects(viewLine)) {
						points.add(new Point(nx, ny));
					}
				}
			}
		}
		points.add(new Point(floor(start.x), floor(start.y)));
		points.add(new Point(floor(end.x), floor(end.y)));
		ArrayList<Point> pointList = new ArrayList<>(points);
		Collections.sort(pointList, (o1, o2) -> (int) -Math
				.signum(getMiddle(o2).distance(start) - getMiddle(o1).distance(start)));
		return pointList;
	}

	private Vector2f getMiddle(Point p) {
		return new Vector2f(p.x + .5f, p.y + .5f);
	}

	/**
	 * floor is faster to type than (int) Math.floor(x)
	 *
	 * @param x
	 * @return
	 */
	private int floor(float x) {
		return (int) Math.floor(x);
	}

	public List<Point> getVisibleBlockLocations(Rectangle view) {
		ArrayList<Point> blockLocs = new ArrayList<>();
		for (int i = (int) (view.getMinX() - 1); i <= view.getMaxX(); i++) {
			Point start = new Point(i, (int) (view.getMinY() - 1));
			Point end = new Point(i, (int) (view.getMaxY() + 1));
			NavigableSet<Point> existingBlocks = blocks.navigableKeySet().subSet(start,
					true, end, true);
			for (Point p : existingBlocks) {
				blockLocs.add(p);
			}
		}
		return blockLocs;
	}

	public ControllableCharacter getMainCharacter() {
		return controlledCharacter;
	}

	public Vector2f getCharacterPosition() {
		if (controlledCharacter != null) {
			return controlledCharacter.getLocation();
		}
		return new Vector2f();
	}

	public void update(int delta) {
		BlockUpdates.propagateLiquids(changedBlocks, blocks);
		updateEntityList();
		Iterator<Entity> iter = entities.iterator();
		while (iter.hasNext()) {
			Entity e = iter.next();
			e.update(this, delta);

			if (!e.alive()) {
				iter.remove();
			}
		}

		// Collision Detection with surroundings
		for (Entity e : entities) {
			Shape hitbox = e.getHitbox();
			Rectangle boundingBox = Geometry.getBoundingBox(hitbox);
			List<Point> collidingBlocks = getVisibleBlockLocations(boundingBox);
			for (Point p : collidingBlocks) {
				Block b = blocks.get(p);
				if (b instanceof SolidBlock) {
					e.collide(b.getHitbox());
				}
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

	/**
	 * Gets the first block that could be mined by the player
	 *
	 * Returns null if no block is found
	 *
	 * @param gameMouseLocation
	 * @return
	 */
	public Block getBlockAtPosition(Vector2f gameMouseLocation) {
		List<Point> clickLine = rayTrace(getCharacterPosition(), gameMouseLocation);
		for (Point p : clickLine) {
			Block b = blocks.get(p);
			if (b instanceof SolidBlock) {
				return b;
			}
		}
		return null;
	}

	public Set<Entity> getEntities(Vector2f pos, float radius) {
		HashSet<Entity> ret = new HashSet<>();
		for (Entity e : entities) {
			if (e.getLocation().distance(pos) < radius) {
				ret.add(e);
			}
		}
		return ret;
	}

	public void breakBlock(Point pos) {
		Block prevBlock = blocks.get(pos);
		blocks.put(pos, Block.createBlock(BlockType.EMPTY, pos.x, pos.y));

		if (prevBlock != null && prevBlock.type != BlockType.EMPTY) {
			Vector2f newPos = prevBlock.getPos();
			newPos.add(new Vector2f((float) Math.random(), (float) Math.random() / 2));
			addEntity(new CollectibleItem(new BlockItem(prevBlock), newPos));
		}
		changedBlocks.add(pos);
	}

	public void removeEntity(Entity e) {
		entities.remove(e);
	}

	public List<Entity> getEntities() {
		return entities;
	}
}
