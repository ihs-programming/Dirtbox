package game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;

public class Viewport {
	private Graphics g;

	public Viewport(Graphics g) {
		this.g = g;
	}
	
	public void draw(Sprite s) {
		g.drawImage(s.img, s.loc.x, s.loc.y);
	}
	
	public void draw(Shape s) {
		g.draw(s);
	}
}
