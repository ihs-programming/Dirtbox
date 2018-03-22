package game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import game.entities.PlayerController;
import game.utils.Chat;
import game.utils.Console;
import game.utils.DefaultGameState;
import game.world.World;

public class MainGameState implements DefaultGameState {
	public static boolean playMusic = false;
	private World world;
	private Viewport vp = new Viewport();
	private ViewportController vpc;
	public static boolean inGame = true; // whether or not to display the escape
											// menu
	private boolean lockCharacter = true; // whether to follow the character
	private GameUI ui;
	private boolean worldrendered = false;

	private PlayerController playerController;
	private Chat chat;

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		Input gcInput = gc.getInput();
		vpc = new ViewportController(gcInput, vp);
		world = new World(gcInput);
		playerController = new PlayerController(world.getMainCharacter(), gcInput, vp,
				world);

		chat = new Chat(new Console(world.getMainCharacter(), world));
		vpc.setChat(chat);
		world.setChat(chat);
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
			world.draw(vp);
			playerController.draw(vp);
			chat.draw(vp);
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
		vpc.update(delta);
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
		if (!ViewportController.inChat && keycode == Input.KEY_L) {
			// toggle whether viewport will center on character
			lockCharacter = !lockCharacter;
		}
	}
}