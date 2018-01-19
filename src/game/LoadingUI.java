package game;

import java.awt.Image;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.ComponentListener;

public class LoadingUI {
	public LoadingUI(GameContainer context, ComponentListener returnCallback) {

	}

	public void draw(Graphics g) {
		Image image = new Image("/data/loadingscreen.jpg");
		g.drawImage(image, x, y, x2, y2, srcx, srcy, srcx2, srcy2, col);
	}
}
