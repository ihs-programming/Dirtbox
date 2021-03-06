package game.world;

import java.awt.Point;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
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
import game.items.BlockItem;
import game.network.Client;
import game.network.ServerThread;
import game.network.SocketListener;
import game.network.event.BlockBreakEvent;
import game.network.event.ChatEvent;
import game.network.event.Event;
import game.network.event.EventProcessor;
import game.network.gamestate.BlockState;
import game.physics.PhysicsBody;
import game.save.Saver;
import game.utils.Chat;
import game.utils.Geometry;

public class GameWorld implements EventProcessor {
	public static final double DAY_NIGHT_DURATION = 1200000.0;
	private static final double GRAVITY_STRENGTH = 5;

	private static final int VIEW_DISTANCE = 32;

	private ArrayList<Entity> entitiesToAdd;
	private ArrayList<Entity> entities;
	private ArrayList<Entity> backgroundsprites;
	private ControllableCharacter controlledCharacter;

	private Client c;
	private Chat chat;

	private Image sunsprite;
	private Entity sun;

	public TreeMap<Point, Block> blocks = new TreeMap<>(BlockState.pointComparer);

	private World dynWorld = new World();

	private Queue<Event> eventQueue = new ConcurrentLinkedQueue<>();
	private Queue<byte[]> blockQueue = new LinkedList<>();
	/**
	 * A set of blocks that have been changed, and thus require updating.
	 */
	private Set<Point> changedBlocks = new HashSet<>();

	public GameWorld() {
		entities = new ArrayList<>();
		entitiesToAdd = new ArrayList<>();
		backgroundsprites = new ArrayList<>();
		intitializePhysicsEngine();
		addDefaultEntities();

		try {
			new Thread(new ServerThread(new SocketListener())).start();
			c = new Client(InetAddress.getLocalHost());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void intitializePhysicsEngine() {
		dynWorld.setGravity(new Vector2(0, GRAVITY_STRENGTH));
		Settings s = dynWorld.getSettings();
		s.setAutoSleepingEnabled(false);
		dynWorld.setSettings(s);
	}

	/**
	 * Please change this
	 */
	private boolean hasInited = false;

	private void init() {
		if (hasInited) {
			return;
		}
		hasInited = true;
		c.bindTo(this);
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
								/ GameWorld.DAY_NIGHT_DURATION)
						* 15 - vp.getCenter().x
						+ sunsprite.getScaledCopy(4, 4).getWidth() / 2),
				(float) -(Math.sin(
						2.0 * Math.PI * Viewport.globaltimer
								/ GameWorld.DAY_NIGHT_DURATION)
						* 15) + 30),
				this);
	}

	private int minGenLim = 0;
	private int maxGenLim = 0;

	/**
	 * TODO
	 *
	 * @param rect
	 */
	public boolean needToGenerate(Rectangle rect) {
		int min = (int) rect.getMinX();
		int max = (int) rect.getMaxX();

		if (minGenLim <= min && max <= maxGenLim) {
			return false;
		}
		if (min < minGenLim && maxGenLim < max) {
			minGenLim = Math.min(min, minGenLim);
			maxGenLim = Math.max(max, maxGenLim);
			return true;
		}
		if (min < minGenLim) {
			int width = minGenLim - min;
			rect.setX(min);
			rect.setWidth(width);
			minGenLim = Math.min(min, minGenLim);
			return true;
		}
		int width = max - maxGenLim;
		rect.setX(maxGenLim);
		rect.setWidth(width);
		maxGenLim = Math.max(max, maxGenLim);
		return true;
	}

	public void draw(Viewport vp) {
		init();
		updateSun(vp);

		if (Viewport.day) {
			sun.draw(vp);
		}

		for (Entity e : backgroundsprites) {
			e.draw(vp);
		}

		Shape view = vp.getGameViewShape();
		Rectangle viewRect = Geometry.getBoundingBox(view);

		Lighting.doSunLighting(blocks, (int) viewRect.getX() - 10,
				(int) (viewRect.getX() + view.getWidth()) + 10,
				(int) viewRect.getY() - 10,
				(int) (viewRect.getY() + view.getHeight()) + 10, 63);

		Rectangle genRect = new Rectangle(
				(int) (viewRect.getX() - VIEW_DISTANCE) / 16 * 16,
				viewRect.getY(),
				viewRect.getWidth() + 2 * VIEW_DISTANCE, 300);
		if (needToGenerate(genRect)) {
			c.requestBlocks(genRect);
		}

		/*
		 * The following three lines somehow randomly cause up to 1000 ms of lag This is
		 * a big issue, as the game otherwise runs quite smoothly. Please fix!
		 * "734.582767 ms for draw (!!!) 743.448732 ms for render"
		 */
		List<Point> visibleBlocks = getVisibleBlockLocations(viewRect);
		Block.draw_hit_count = 0;
		for (Point p : visibleBlocks) {
			getBlock(p).draw(vp);
		}
		for (Point p : visibleBlocks) {
			getBlock(p).drawShading(vp);
		}
		for (Entity e : entities) {
			e.draw(vp);
		}
		if (Viewport.DEBUG_MODE) {
			for (Body bb : dynWorld.getBodies()) {
				Shape[] v = Geometry.convertShape(bb);
				if (v.length > 0) {
					vp.draw(v[0], Color.green);
				}
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
			// around the block to avoid edge cases
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
			NavigableSet<Point> existingBlocks = blocks.navigableKeySet().subSet(
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

	public void recieveNewBlocks(byte[] data) {
		blockQueue.add(data);
	}

	public void update(int delta) {
		processEventQueue();

		BlockUpdates.propagateLiquids(changedBlocks, blocks);
		updateEntityList();
		List<Entity> deadEntities = new ArrayList<>();
		for (Entity e : entities) {
			e.update(this, delta);
			if (!e.alive()) {
				deadEntities.add(e);
			}
		}
		for (Entity e : deadEntities) {
			remove(e);
		}

		for (Entity e : entities) {
			Rectangle boundingBox = Geometry.getBoundingBox(e.getHitbox());
			Vector2f boxPos = new Vector2f(boundingBox.getCenter());
			boundingBox.setWidth(boundingBox.getWidth() + 5);
			boundingBox.setHeight(boundingBox.getHeight() + 5);
			boundingBox.setCenterX(boxPos.x);
			boundingBox.setCenterY(boxPos.y);
			List<Point> locs = getVisibleBlockLocations(boundingBox);
			for (Point p : locs) {
				Body b = getBlock(p).getBody();
				if (dynWorld.containsBody(b)) {
					continue;
				}
				dynWorld.addBody(b);
			}
		}
		dynWorld.update(delta);
	}

	/**
	 * Process all the events in the eventQueue.
	 *
	 */
	private void processEventQueue() {
		while (!eventQueue.isEmpty()) {
			Event e = eventQueue.poll();
			e.processIfPossible(this);
		}
		while (!blockQueue.isEmpty()) {
			byte[] blockData = blockQueue.poll();
			Set<Map.Entry<Point, Block>> es = Saver.load(blockData).entrySet();
			es.forEach(e -> setBlock(e.getKey(), e.getValue()));
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
			Block b = getBlock(p);
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
		Block prevBlock = getBlock(pos);
		if (prevBlock == null || prevBlock.type == BlockType.WATER) {
			return;
		}

		setBlock(pos, Block.createBlock(BlockType.EMPTY, pos.x, pos.y));

		if (prevBlock != null && prevBlock.type != BlockType.EMPTY) {
			Vector2f newPos = prevBlock.getPos();
			newPos.add(new Vector2f((float) Math.random(), (float) Math.random()));
			addEntity(new CollectibleItem(new BlockItem(prevBlock), newPos, this));
		}
		changedBlocks.add(pos);
		c.sendEvent(new BlockBreakEvent(pos.x, pos.y));
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

	public Client getClient() {
		return c;
	}

	public void setChat(Chat c) {
		this.chat = c;
	}

	public void addEvent(Event e) {
		eventQueue.offer(e);
	}

	@Override
	public void processEvent(Event e) {
		if (e instanceof ChatEvent) {
			processEvent((ChatEvent) e);
		}
		if (e instanceof BlockBreakEvent) {
			processEvent((BlockBreakEvent) e);
		}
	}

	private void processEvent(ChatEvent ce) {
		chat.chatAddLine(new String(ce.toBytes()));
	}

	private void processEvent(BlockBreakEvent bbe) {
		Point pos = bbe.getPos();
		Block prevBlock = getBlock(pos);
		if (prevBlock == null || prevBlock.type == BlockType.WATER) {
			return;
		}

		setBlock(pos, Block.createBlock(BlockType.EMPTY, pos.x, pos.y));

		if (prevBlock != null && prevBlock.type != BlockType.EMPTY) {
			Vector2f newPos = prevBlock.getPos();
			newPos.add(new Vector2f((float) Math.random(), (float) Math.random() / 2));
			addEntity(new CollectibleItem(new BlockItem(prevBlock), newPos, this));
		}
		changedBlocks.add(pos);
	}

	public Point getFirstBlock() {
		return blocks.firstKey();
	}

	public Point getLastBlock() {
		return blocks.lastKey();
	}

	public void remove(Entity e) {
		entities.remove(e);
		dynWorld.removeListener(e.getPhysicsListener());
		remove((PhysicsBody) e);
	}

	public void remove(PhysicsBody b) {
		dynWorld.removeBody(b.getBody());
	}

	public List<Entity> getEntities() {
		return Collections.unmodifiableList(entities);
	}

	public void setBlock(Point p, Block b) {
		if (blocks.containsKey(p)) {
			remove(blocks.get(p));
		}
		blocks.put(p, b);
	}

	public void setBlocks(TreeMap<Point, Block> blocks) {
		blocks.clear();
		for (Map.Entry<Point, Block> ent : blocks.entrySet()) {
			setBlock(ent.getKey(), ent.getValue());
		}
	}
}
