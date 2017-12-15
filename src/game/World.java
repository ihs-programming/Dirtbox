package game;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class World implements Drawable {

	public World() {
		
	}
	
	@Override
	public void draw(Graphics g) {
		g.setColor(Color.green);
		g.drawImage(SpriteSheetLoader.getBlockImage(0, 0), 0, 0);
	}
}
