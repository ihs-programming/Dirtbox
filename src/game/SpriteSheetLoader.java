package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class SpriteSheetLoader {
	private static final SpriteSheet BLOCK_SHEET = loadSpriteSheet("data/blocks.png", 40, 40);

	private static SpriteSheet loadSpriteSheet(String filename, int spriteWidth, int spriteHeight) {
		Image spriteSheetImage;
		try {
			spriteSheetImage = new Image(filename);
			return new SpriteSheet(spriteSheetImage, spriteWidth, spriteHeight);
		} catch (SlickException e) {
			System.out.printf("Unable to load sprite sheet %s...\n", filename);
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	public static Image getBlockImage (int x, int y) {
		Image img = BLOCK_SHEET.getSprite(x, y);
		img.setFilter(Image.FILTER_NEAREST);
		return img.getScaledCopy(Block.BLOCK_SPRITE_SIZE, Block.BLOCK_SPRITE_SIZE);
	}
}
