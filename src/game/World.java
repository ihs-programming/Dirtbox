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

	private ArrayList<Entity> characters;

	public World() {
		characters = new ArrayList<>();
		try {
			Image stalinsprite = new Image("data/characters/stalin.png");
			stalinsprite.setFilter(Image.FILTER_NEAREST);
			stalinsprite = stalinsprite.getScaledCopy(1, 2);
			Entity stalin = new Entity(stalinsprite, 1, 1, new Vector2f(0, 0));
			characters.add(stalin);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public void draw(Viewport vp) {

		Shape view = vp.getGameViewShape();
		Rectangle viewRect = new Rectangle(view.getMinX(), view.getMinY(),
				view.getWidth(),
				view.getHeight());
		RegionGenerator worldgenerationthread = new RegionGenerator(viewRect);
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
		for (Entity e : this.characters) {
			e.draw(vp);
		}

	}
}
