package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.NavigableSet;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import game.entities.Entity;

public class World {

	static final double DAY_NIGHT_DURATION = 6000.0;

	private ArrayList<Entity> characters;

	private static Image sunsprite;

	public World() {
		characters = new ArrayList<>();
		try {
			sunsprite = new Image("data/characters/sunsprite.png");
			sunsprite.setFilter(Image.FILTER_NEAREST);
			sunsprite = sunsprite.getScaledCopy(4, 4);
			Entity suns = new Entity(sunsprite, 1, 1, new Vector2f(0, 0));
			characters.add(suns);

			Image stalinsprite = new Image("data/characters/stalin.png");
			stalinsprite.setFilter(Image.FILTER_NEAREST);
			stalinsprite = stalinsprite.getScaledCopy(1, 2);
			Entity stalin = new Entity(stalinsprite, 1, 1, new Vector2f(0, 0));
			characters.add(stalin);
		} catch (SlickException e) {

		}

	}

	public void draw(Viewport vp) {

		Entity suns = new Entity(World.sunsprite, 1, 1, new Vector2f((float) -(Math
				.cos(2.0 * Math.PI * System.currentTimeMillis()
						/ World.DAY_NIGHT_DURATION)
				* 15 - Viewport.center.x + sunsprite.getScaledCopy(4, 4).getWidth() / 2),
				(float) -(Math
						.sin(2.0 * Math.PI * System.currentTimeMillis()
								/ World.DAY_NIGHT_DURATION)
						* 15) + 30));
		characters.set(0, suns);
		for (Entity e : this.characters) {
			e.draw(vp);
		}

		Shape view = vp.getGameViewShape();
		Rectangle viewRect = new Rectangle(view.getMinX(), view.getMinY(),
				view.getWidth(),
				view.getHeight());
		new RegionGenerator(viewRect);
		for (int i = (int) (viewRect.getMinX() - 1); i <= viewRect.getMaxX(); i++) {
			Point start = new Point(i, (int) (viewRect.getMinY() - 1));
			Point end = new Point(i, (int) (viewRect.getMaxY() + 1));
			NavigableSet<Point> existingBlocks = RegionGenerator.blocks.navigableKeySet()
					.subSet(start, true, end, true);
			/*
			 * The following three lines somehow randomly cause up to 1000 ms of lag This
			 * is a big issue, as the game otherwise runs quite smoothly. Please fix!
			 * "734.582767 ms for draw (!!!) 743.448732 ms for render"
			 */
			for (Point p : existingBlocks) {
				RegionGenerator.blocks.get(p).draw(vp);
			}

		}

	}
}
