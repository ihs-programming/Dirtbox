package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class Dirtbox {
	private static final int DEFAULT_WIDTH = 640;
	private static final int DEFAULT_HEIGHT = 480;
	
	private static final int DEFAULT_FRAME_RATE = 60;
	private static boolean DEFAULT_FULLSCREEN = false;
	
	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Game("Dirtbox"));
			app.setDisplayMode(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_FULLSCREEN);
			app.setTargetFrameRate(DEFAULT_FRAME_RATE);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
