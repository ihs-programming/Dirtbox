package game;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class Dirtbox {
	private static final Dimension screenSize = Toolkit.getDefaultToolkit()
			.getScreenSize();
	private static final int DEFAULT_WIDTH = (int) screenSize.getWidth();
	private static final int DEFAULT_HEIGHT = (int) screenSize.getHeight();

	private static final int DEFAULT_FRAME_RATE = 60;
	private static boolean DEFAULT_FULLSCREEN = true;

	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Game("Dirtbox"));
			app.setDisplayMode(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_FULLSCREEN);
			app.setTargetFrameRate(DEFAULT_FRAME_RATE);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
