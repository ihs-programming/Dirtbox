package game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

public class MainState extends DefaultGameState {

	@Override
	public void enter(GameContainer arg0, StateBasedGame arg1) throws SlickException {
	}

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
	}

	@Override
	public void leave(GameContainer arg0, StateBasedGame arg1) throws SlickException {
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.drawString("Hello world!", 100, 100);

	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2) throws SlickException {
		// TODO Auto-generated method stub

	}

}
