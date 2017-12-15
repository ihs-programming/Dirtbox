package game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Game extends StateBasedGame {

	public Game(String name) {
		super(name);
	}

	@Override
	public void initStatesList(GameContainer gc) throws SlickException {
		this.addState(new MainGameState());
	}

}
