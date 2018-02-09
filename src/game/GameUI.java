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
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.gui.MouseOverArea;

public class GameUI {
	public final static float DEFAULT_BLOCKBUTTON_SIZE = 8;
	private GameContainer context;
	private List<AbstractComponent> components = new ArrayList<>();

	public GameUI(GameContainer context, ComponentListener returnCallback) {
		this.context = context;

		LabelButton exitButton = generateStoneCoalButton(
				new Vector2f(context.getWidth(), context.getHeight()).scale(.5f), 0, 0);
		exitButton.setText("");
		exitButton.addListener(source -> {
			context.exit();
		});
		components.add(exitButton);

		LabelButton returnButton = generateStoneCoalButton(
				new Vector2f(context.getWidth(), context.getHeight() + 300).scale(.5f), 0,
				1);
		returnButton.setText("");
		returnButton.addListener(returnCallback);
		components.add(returnButton);
	}

	/**
	 * Displays text with button
	 */
	private class LabelButton extends MouseOverArea {
		private final static float DEFAULT_TEXT_HEIGHT = 20;
		private String text = "";

		public LabelButton(GUIContext container, Image image, int x, int y) {
			super(container, image, x, y);
		}

		public void setText(String text) {
			this.text = text;
		}

		@Override
		public void render(GUIContext c, Graphics g) {
			super.render(c, g);
			g.drawString(text, getX(), getY() - DEFAULT_TEXT_HEIGHT);
		}
	}

	private LabelButton generateStoneCoalButton(Vector2f location, int x, int y) {
		LabelButton button = generateButton(location,
				getBlockImg(x, y, DEFAULT_BLOCKBUTTON_SIZE),
				null);
		button.setMouseOverImage(getBlockImg(x + 1, y, DEFAULT_BLOCKBUTTON_SIZE));
		return button;
	}

	private LabelButton generateButton(Vector2f center, Image img,
			ComponentListener listener) {
		LabelButton button = new LabelButton(context, img,
				(int) (center.x - img.getWidth() / 2),
				(int) (center.y - img.getWidth() / 2));
		if (listener != null) {
			button.addListener(listener);
		}
		return button;
	}

	private Image getBlockImg(int sx, int sy, float size) {
		return SpriteSheetLoader.getGuiImage(sx, sy).getScaledCopy(size);
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