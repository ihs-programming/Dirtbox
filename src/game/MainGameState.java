package game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class MainGameState extends DefaultGameState {
	private World world;

	@Override
	public void enter(GameContainer arg0, StateBasedGame arg1) throws SlickException {
	}


	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
		world = new World();
	}

	@Override
	public void leave(GameContainer arg0, StateBasedGame arg1) throws SlickException {
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		world.draw(g);
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2) throws SlickException {
	}

	@Override
	public int getID() {
		return 0;
	}
}