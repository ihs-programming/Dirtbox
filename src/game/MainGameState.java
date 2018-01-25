package game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import game.entities.PlayerController;
import game.utils.DefaultGameState;

public class MainGameState implements DefaultGameState {
	private World world;
	private Viewport vp = new Viewport();
	public static boolean inGame = true; // whether or not to display the escape menu
	private boolean lockCharacter = true; // whether to follow the character
	private GameUI ui;
	private boolean worldrendered = false;

	private PlayerController playerController;

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		Input gcInput = gc.getInput();
		gcInput.addKeyListener(vp);
		gcInput.addMouseListener(vp);
		world = new World(gcInput);
		playerController = new PlayerController(world.getMainCharacter(), gcInput, vp,
				world);
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
			if (lockCharacter) {
				vp.setCenter(world.getCharacterPosition());
			}
			playerController.draw(vp);
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
		playerController.update(delta);
		world.update(delta);
	}

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public void keyPressed(int keycode, char c) {
		if (keycode == Input.KEY_ESCAPE) {
			// open exit menu
			inGame = !inGame;
		}
		if (keycode == Input.KEY_L) {
			// toggle whether viewport will center on character
			lockCharacter = !lockCharacter;
		}
	}
}