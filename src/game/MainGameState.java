package game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import game.utils.DefaultGameState;

public class MainGameState implements DefaultGameState {
	private World world = new World();
	private Viewport vp = new Viewport();
	private boolean inGame = true;
	private GameUI ui;
	private boolean worldrendered = false;

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		gc.getInput().addKeyListener(vp);
		gc.getInput().addMouseListener(vp);
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		vp.setScreenCenter(new Vector2f(gc.getWidth() / 2, gc.getHeight() / 2));
		vp.zoom(10f);
		ui = new GameUI(gc, source -> {
			inGame = true;
		});
	}

	@Override
	public void leave(GameContainer arg0, StateBasedGame arg1) throws SlickException {
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g)
			throws SlickException {
		if (inGame) {
			vp.setGraphics(g);
			world.draw(vp);
		} else {
			// Display ui
			ui.draw(g);
		}
		if (!worldrendered) {
			worldrendered = true;
			Viewport.timerupdate = System.currentTimeMillis();
		}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		if (worldrendered) {
			vp.update(delta);
		}
	}

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public void keyPressed(int keycode, char c) {
		if (keycode == Input.KEY_ESCAPE) {
			inGame = !inGame;
		}
	}
}