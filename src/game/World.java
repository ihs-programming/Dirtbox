package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.blocks.Block;
import game.blocks.SolidBlock;
import game.entities.ControllableCharacter;
import game.entities.Entity;
import game.utils.Geometry;

public class World {

	static final double DAY_NIGHT_DURATION = 1200000.0;

	private ArrayList<Entity> characters;
	private ControllableCharacter controlledCharacter;

	private static Image sunsprite;

	private boolean enableFOV = true;

	public TreeMap<Point, Block> blocks = new TreeMap<>((p1, p2) -> {
		if (p1.x == p2.x) {
			return p1.y - p2.y;
		}
		return p1.x - p2.x;
	});

	public World() {
		characters = new ArrayList<>();
		try {
			sunsprite = new Image("data/characters/sunsprite.png");
			sunsprite.setFilter(Image.FILTER_NEAREST);
			sunsprite = sunsprite.getScaledCopy(4, 4);
			Entity suns = new Entity(sunsprite, 1, 1, new Vector2f(0, 0));
			characters.add(suns);
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
	}

	public void draw(Viewport vp) {
		Entity suns = new Entity(World.sunsprite, 1, 1, new Vector2f((float) -(Math
				.cos(2.0 * Math.PI * Viewport.globaltimer
						/ World.DAY_NIGHT_DURATION)
				* 15 - vp.getCenter().x + sunsprite.getScaledCopy(4, 4).getWidth() / 2),
				(float) -(Math
						.sin(2.0 * Math.PI * Viewport.globaltimer
								/ World.DAY_NIGHT_DURATION)
						* 15) + 30));
		characters.set(0, suns);
		for (Entity e : this.characters) {
			e.draw(vp);
		}
		Shape view = vp.getGameViewShape();
		Rectangle viewRect = Geometry.getBoundingBox(view);

		new RegionGenerator(viewRect, blocks);

		/*
		 * The following three lines somehow randomly cause up to 1000 ms of lag This is
		 * a big issue, as the game otherwise runs quite smoothly. Please fix!
		 * "734.582767 ms for draw (!!!) 743.448732 ms for render"
		 */
		List<Point> visibleBlocks = getVisibleBlockLocations(viewRect);
		if (enableFOV) {
			List<Point> update = new ArrayList<>();
			Rectangle r = Geometry.getBoundingBox(this.controlledCharacter.getHitbox());
			while (!update.isEmpty()) {
			}
		}
		for (Point p : visibleBlocks) {
			blocks.get(p).draw(vp);
		}
		if (Viewport.DEBUG_MODE) {
			renderHitboxes(vp);
		}
	}

	private List<Point> getVisibleBlockLocations(Rectangle view) {
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
		}
	}
}
