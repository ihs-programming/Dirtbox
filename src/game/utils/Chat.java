package game.utils;

import org.newdawn.slick.Input;

public class Chat {
	private String curr = "";

	/**
	 *
	 * @param key
	 * @param c
	 * @return Still in chat-mode
	 */
	public boolean keyPressed(int key, char c) {
		switch (key) {
		case Input.KEY_ENTER:
			Console.doCommand(curr);
			curr = "";
			return false;
		case Input.KEY_BACKSLASH:
			if (curr.length() > 0) {
				curr = curr.substring(0, curr.length() - 1);
			}
			break;
		default:
			if (' ' <= c && c <= '~') {
				curr += c;
			}
			break;
		}
		return true;
	}

	public String getMessage() {
		return curr;
	}
}
