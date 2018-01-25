package game.entities;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import game.Viewport;
import game.World;
import game.blocks.Block;
import game.utils.DefaultKeyListener;
import game.utils.DefaultMouseListener;

public class PlayerController implements DefaultMouseListener {
	private ControllableCharacter character;
	private Input userInput;
	private World world;
	private Viewport vp;

	// block that the player is trying to mine
	private int mineTime = 0;
	private Block currentBlock;

	public PlayerController(ControllableCharacter character, Input inp, Viewport vp,
			World world) {
		this.character = character;
		this.world = world;
		userInput = inp;
		userInput.addKeyListener(new DefaultKeyListener() {
			@Override
			public void keyPressed(int key, char c) {
				if (key == Input.KEY_W) {
					character.jump();
				}
			}

			@Override
			public void keyReleased(int key, char c) {
				// TODO Auto-generated method stub

			}

		});
		userInput.addMouseListener(this);
		this.vp = vp;
	}

	public void update(int delta) {
		character.stopMoving();
		if (userInput.isKeyDown(Input.KEY_A)) {
			character.move(true);
		}
		if (userInput.isKeyDown(Input.KEY_D)) {
			character.move(false);
		}
		character.update(delta);
		mineTime += delta;
		if (currentBlock != null && mineTime > ControllableCharacter.BLOCK_MINE_TIME) {
			world.removeBlock(currentBlock);
		}
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		Vector2f mousePos = vp.getInverseDrawTransform().transform(new Vector2f(x, y));
		currentBlock = world.getMinedBlock(mousePos);
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		currentBlock = null;
	}
}
