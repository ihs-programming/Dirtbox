package game.utils;

import org.newdawn.slick.Font;
import org.newdawn.slick.TrueTypeFont;

public class FontUtil {
	public static Font getDefaultFont(int size, boolean bold) {
		int fontType = java.awt.Font.PLAIN;
		if (bold) {
			fontType = java.awt.Font.BOLD;
		}
		return new TrueTypeFont(
				new java.awt.Font("Times New Roman", fontType, size),
				false);
	}

	public static Font getDefaultFont(int size) {
		return getDefaultFont(size, false);
	}
}
