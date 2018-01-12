package game;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;

public class GameUI {
	private final static float DEFAULT_BLOCKBUTTON_SIZE = 100;
	private GameContainer context;
	private List<AbstractComponent> components = new ArrayList<>();

	public GameUI(GameContainer context) {
		this.context = context;
		components.add(generateStoneCoalButton(
				new Vector2f(context.getWidth(), context.getHeight()).scale(.5f)));
	}

	private MouseOverArea generateStoneCoalButton(Vector2f location) {
		MouseOverArea button = generateButton(location, getBlockImg(0, 0, DEFAULT_BLOCKBUTTON_SIZE),
				null);
		button.setMouseOverImage(getBlockImg(2, 2, DEFAULT_BLOCKBUTTON_SIZE));
		return button;
	}

	private MouseOverArea generateButton(Vector2f center, Image img, ComponentListener listener) {
		MouseOverArea button = new MouseOverArea(context, img,
				(int) (center.x - img.getWidth() / 2), (int) (center.y - img.getWidth() / 2));
		button.addListener(listener);
		return button;
	}

	private Image getBlockImg(int sx, int sy, float size) {
		return SpriteSheetLoader.getBlockImage(sx, sy).getScaledCopy(size);
	}

	public void draw(Graphics g) {
		g.setColor(Color.white);
		for (AbstractComponent comp : components) {
			try {
				comp.render(context, g);
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
	}
}
