package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class Dirtbox {
	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Game("Dirtbox"));
			app.setDisplayMode(640, 480, false);
			app.setTargetFrameRate(60);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
