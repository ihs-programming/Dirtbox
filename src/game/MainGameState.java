package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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

	public File gameSave;

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		gc.getInput().addKeyListener(vp);
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		vp.setScreenCenter(new Vector2f(gc.getWidth() / 2, gc.getHeight() / 2));
		vp.zoom(10f);
		ui = new GameUI(gc);
	}

	@Override
	public void leave(GameContainer arg0, StateBasedGame arg1) throws SlickException {
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		if (inGame) {
			vp.setGraphics(g);
			world.draw(vp);
		} else {
			// Display ui
			ui.draw(g);
		}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		vp.update(delta);
	}

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public void keyPressed(int keycode, char c) {
		if (keycode == Input.KEY_ESCAPE) {
			inGame = false;
		}
	}

	public void save() throws FileNotFoundException {
		Scanner scan = new Scanner(gameSave);
		String worldString = world.toString();
		// String playerString
	}

	private void load(File f) {

	}
}