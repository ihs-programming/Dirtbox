package game.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;
import game.DefaultKeyListener;

public class PlayerEntity extends Entity implements DefaultKeyListener{
	
	

	public PlayerEntity(Image spritesheet, int sheetwidth, int sheetheight, float hitwidth, float hitheight, Vector2f pos) {
		super(spritesheet, sheetwidth, sheetheight, hitwidth, hitheight, pos);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void keyPressed(int key, char c) {
		switch (key) {
		case Input.KEY_UP:
			super.getVel();
			break;
		case Input.KEY_DOWN:
			//this should let player go through platforms
			break;
		case Input.KEY_RIGHT:
			Vector2f accel = super.getAccel();
			break;
		case Input.KEY_LEFT:
			Vector2f accel = super.getAccel();
			break;
		}
		
	}

	@Override
	public void keyReleased(int arg0, char arg1) {
		// TODO Auto-generated method stub
		
	}
	
	

}
