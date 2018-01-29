package game.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import game.Viewport;
import game.World;
import game.blocks.Block;
import game.utils.DefaultKeyListener;
import game.utils.DefaultMouseListener;
import game.utils.Geometry;

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

	public void draw(Viewport vp) {
		if (Viewport.DEBUG_MODE) {
			if (currentBlock != null) {
				vp.fill(Geometry.getBoundingBox(currentBlock.getHitbox()), Color.red);
			}
		}
	}

	public void update(int delta) {
		character.stopMoving();
		if (userInput.isKeyDown(Input.KEY_A)) {
			character.move(true);
		}
		if (userInput.isKeyDown(Input.KEY_D)) {
			character.move(false);
		}
		if (currentBlock == null
				&& userInput.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			currentBlock = getMinedBlock(userInput.getMouseX(),
					userInput.getMouseY());
		}
		if (currentBlock != null) {
			mineTime += delta;
			if (mineTime > ControllableCharacter.BLOCK_MINE_TIME) {
				mineBlock();
			}
		}
	}

	private void mineBlock() {
		world.removeBlock(currentBlock);
		currentBlock = null;
		mineTime = 0;
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		Vector2f mousePos = this.convertMousePos(x, y);
		currentBlock = world.getMinedBlock(mousePos);
	}

	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		Block nblock = this.getMinedBlock(oldx, oldy);
		if (nblock != currentBlock) {
			mineTime = 0;
			currentBlock = nblock;
		}
	}

	private Block getMinedBlock(int mouseX, int mouseY) {
		Vector2f mousePos = this.convertMousePos(mouseX, mouseY);
		return world.getMinedBlock(mousePos);
	}

	private Vector2f convertMousePos(int x, int y) {
		return vp.getInverseDrawTransform().transform(new Vector2f(x, y));
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		currentBlock = null;
	}
}
