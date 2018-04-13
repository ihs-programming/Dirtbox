package game.blocks;

/**
 * For blocks that aren't empty but should have different collision stuff.
 *
 * @author s-weia
 *
 */
public class LiquidBlock extends Block {
	protected LiquidBlock(BlockType t, float xpos, float ypos) {
		super(t, t.sx, t.sy, xpos, ypos, new EmptyBlockBodyFactory());
	}
}
