package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import game.blocks.Block;

/**
 * Place where all resources should be loaded from
 */
public class SpriteSheetLoader {
	private static final SpriteSheet BLOCK_SHEET = loadSpriteSheet("data/Blocks.png", 16,
			16);

	private static SpriteSheet loadSpriteSheet(String filename, int spriteWidth,
			int spriteHeight) {
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

	public static Image getBlockImage(int x, int y) {
		Image img = BLOCK_SHEET.getSprite(x, y);
		img.setFilter(Image.FILTER_NEAREST);
		return img.getScaledCopy(Block.BLOCK_SPRITE_SIZE, Block.BLOCK_SPRITE_SIZE);
	}

	private static SpriteSheet loadGuiSheet(String filename, int spriteWidth,
			int spriteHeight) {
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

	private static final SpriteSheet GUI_SPRITE = loadGuiSheet("data/exitbutton.png", 47,
			17);
	private static final SpriteSheet GUI_H = loadGuiSheet("data/hotbar.png", 400,
			50);

	public static Image getGuiImage(int x, int y) {
		Image img = GUI_SPRITE.getSprite(x, y);
		img.setFilter(Image.FILTER_NEAREST);
		return img;
	}

	public static Image getHotbar(int x, int y) {
		Image img = GUI_H.getSprite(x, y);
		img.setFilter(Image.FILTER_NEAREST);
		return img;
	}
}
