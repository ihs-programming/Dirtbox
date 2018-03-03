package game.world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
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
import game.entities.Creature;
import game.entities.Entity;
import game.entities.creature.Bunny;
import game.entities.creature.Wolf;
import game.generation.RegionGenerator;
import game.items.BlockItem;
import game.utils.Geometry;

public class World {
	public static final double DAY_NIGHT_DURATION = 1200000.0;
	private static final double GRAVITY_STRENGTH = 5;
	private static final Comparator<Point> pointComparer = (p1, p2) -> {
		if (p1.x == p2.x) {
			return p1.y - p2.y;
		}
		return p1.x - p2.x;
	};

	private ArrayList<Entity> entitiesToAdd;
	private ArrayList<Entity> entities;
	private ArrayList<Entity> backgroundsprites;
	private ControllableCharacter controlledCharacter;

	private Image sunsprite;
	private Entity sun;

	private Input userInp = null; // used only for debugging purposes currently

	private TreeMap<Point, Block> blocks = new TreeMap<>(pointComparer);
	public RegionGenerator regionGenerator;

	private Set<Body> blockBodies = new HashSet<>();
	private org.dyn4j.dynamics.World dynWorld = new org.dyn4j.dynamics.World();

	/**
	 * A set of blocks that have been changed, and thus require updating.
	 */
	private Set<Point> changedBlocks = new HashSet<>();

	public World() {
		entities = new ArrayList<>();
		entitiesToAdd = new ArrayList<>();
		backgroundsprites = new ArrayList<>();
		regionGenerator = new RegionGenerator(blocks);
		dynWorld.setGravity(new Vector2(0, GRAVITY_STRENGTH));
		addDefaultEntities();
	}

	public Block getBlock(Point location) {
		return blocks.get(location);
	}

	public static Point getCoordinates(Vector2f position) {
		Point coordinate = new Point();
		coordinate.setLocation(position.getX(), position.getY());
		return coordinate;
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
						new Vector2f(10 * i, 0), this));
			}
			Sprite wolf = new Sprite("data/characters/woof.png");
			wolf.scale(2f / wolf.getWidth());
			addEntity(new Wolf(wolf, new Vector2f(0, 0), this));
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
		for (Entity e : entitiesToAdd) {
			dynWorld.addBody(e.getBody());
			dynWorld.addListener(e.getPhysicsListener());
			entities.add(e);
		}
		entitiesToAdd.clear();
	}

	private void updateSun(Viewport vp) {
		sun = new Entity(sunsprite, new Vector2f(
				(float) -(Math
						.cos(2.0 * Math.PI * Viewport.globaltimer
								/ World.DAY_NIGHT_DURATION)
						* 15 - vp.getCenter().x
						+ sunsprite.getScaledCopy(4, 4).getWidth() / 2),
				(float) -(Math.sin(
						2.0 * Math.PI * Viewport.globaltimer / World.DAY_NIGHT_DURATION)
						* 15) + 30),
				this);
	}

	public void draw(Viewport vp) {
		updateSun(vp);

		if (Viewport.day) {
			sun.draw(vp);
		}

		for (Entity e : backgroundsprites) {
			e.draw(vp);
		}

		Shape view = vp.getGameViewShape();
		Rectangle viewRect = Geometry.getBoundingBox(view);

		long time = System.currentTimeMillis();
		Lighting.doSunLighting(getBlocks(), (int) viewRect.getX() - 10,
				(int) (viewRect.getX() + view.getWidth()) + 10,
				(int) viewRect.getY() - 10,
				(int) (viewRect.getY() + view.getHeight()) + 10, 63);
		if (Viewport.DEBUG_MODE) {
			System.out.printf("%d ms for sun lighting calculations.\n",
					System.currentTimeMillis() - time);
		}

		regionGenerator.generate(viewRect);

		/*
		 * The following three lines somehow randomly cause up to 1000 ms of lag This is
		 * a big issue, as the game otherwise runs quite smoothly. Please fix!
		 * "734.582767 ms for draw (!!!) 743.448732 ms for render"
		 */
		List<Point> visibleBlocks = getVisibleBlockLocations(viewRect);
		time = System.currentTimeMillis();
		Block.draw_hit_count = 0;
		for (Point p : visibleBlocks) {
			getBlocks().get(p).draw(vp);
		}
		if (Viewport.DEBUG_MODE) {
			System.out.printf(
					"%d ms for visible | %d out of %d blocks rendered.\n",
					System.currentTimeMillis() - time, Block.draw_hit_count,
					visibleBlocks.size());
		}
		time = System.currentTimeMillis();
		for (Point p : visibleBlocks) {
			getBlocks().get(p).drawShading(vp);
		}
		for (Entity e : entities) {
			e.draw(vp);
		}
		if (Viewport.DEBUG_MODE) {
			List<Point> locs = getVisibleBlockLocations(
					Geometry.getBoundingBox(vp.getGameViewShape()));
			for (Point p : locs) {
				vp.draw(blocks.get(p).getHitbox(), Color.green);
			}
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
			NavigableSet<Point> existingBlocks = getBlocks().navigableKeySet().subSet(
					start,
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
		BlockUpdates.propagateLiquids(changedBlocks, getBlocks());
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
		/*
		 * for (Body b : blockBodies) { dynWorld.removeBody(b); }
		 */
		// blockBodies.clear();
		for (Entity e : entities) {
			Rectangle boundingBox = Geometry.getBoundingBox(e.getHitbox());
			Vector2f boxPos = new Vector2f(boundingBox.getCenter());
			boundingBox.setWidth(boundingBox.getWidth() + 5);
			boundingBox.setHeight(boundingBox.getHeight() + 5);
			boundingBox.setCenterX(boxPos.x);
			boundingBox.setCenterY(boxPos.y);
			List<Point> locs = getVisibleBlockLocations(boundingBox);
			for (Point p : locs) {
				Body b = blocks.get(p).getBody();
				if (dynWorld.containsBody(b)) {
					continue;
				}
				dynWorld.addBody(b);
				blockBodies.add(b);
			}
		}
		dynWorld.update(delta);
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
			Block b = getBlocks().get(p);
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

	/**
	 * Breaks block and spawns a new block entity
	 *
	 * @param pos
	 */
	public void breakBlock(Point pos) {
		Block prevBlock = getBlocks().get(pos);
		if (prevBlock == null || prevBlock.type == BlockType.WATER) {
			return;
		}

		getBlocks().put(pos, Block.createBlock(BlockType.EMPTY, pos.x, pos.y));

		if (prevBlock != null && prevBlock.type != BlockType.EMPTY) {
			Vector2f newPos = prevBlock.getPos();
			newPos.add(new Vector2f((float) Math.random(), (float) Math.random() / 2));
			addEntity(new CollectibleItem(new BlockItem(prevBlock), newPos, this));
		}
		changedBlocks.add(pos);
	}

	public void explode(Point pos, int str) {
		for (int i = -str; i <= str; i++) {
			for (int z = -str; z <= str; z++) {
				if (i * i + z * z > str * str) {
					continue;
				}
				breakBlock(new Point(pos.x + i, pos.y + z));
			}
		}
		for (Entity e : entities) {
			Vector2f entityCenter = e.getLocation();
			if (pos.distance(entityCenter.x, entityCenter.y) < str) {
				if (e instanceof Creature) {
					((Creature) e).doHit(1);
				}
			}
		}
	}

	public Point getFirstBlock() {
		return blocks.firstKey();
	}

	public Point getLastBlock() {
		return blocks.lastKey();
	}

	public void removeEntity(Entity e) {
		entities.remove(e);
		dynWorld.removeBody(e.getBody());
		dynWorld.removeListener(e.getPhysicsListener());
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public TreeMap<Point, Block> getBlocks() {
		return blocks;
	}

	public void setBlock(Point p, Block b) {
		blocks.put(p, b);
	}

	public void setBlocks(TreeMap<Point, Block> blocks) {
		this.blocks.clear();
		for (Map.Entry<Point, Block> ent : blocks.entrySet()) {
			setBlock(ent.getKey(), ent.getValue());
		}
	}
}
