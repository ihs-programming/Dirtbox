package game;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.MouseOverArea;

public class GameUI {
	private GameContainer context;
	private MouseOverArea exitButton;

	public GameUI(GameContainer context) {
		this.context = context;
		int blockSize = 100;
		exitButton = new MouseOverArea(context,
				SpriteSheetLoader.getBlockImage(0, 0).getScaledCopy(blockSize),
				context.getWidth() / 2 - blockSize / 2, context.getHeight() / 2);
		exitButton.setMouseOverImage(SpriteSheetLoader.getBlockImage(2, 2).getScaledCopy(100));
		exitButton.addListener(source -> {
			context.exit();
		});
	}

	public void draw(Graphics g) {
		g.setColor(Color.white);
		g.drawString("Exit game?", exitButton.getX(), exitButton.getY() - 25);
		exitButton.render(context, g);
	}
}
