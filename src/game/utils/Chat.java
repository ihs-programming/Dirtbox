package game.utils;

import java.util.ArrayList;

import org.newdawn.slick.Input;

public class Chat {
	private String curr = "";
	public static ArrayList<String> chat = new ArrayList<>();
	public static ArrayList<Long> timeofmessage = new ArrayList<>();
	public static boolean displaychat = false;

	public static void chatAddLine(String chatstring) {
		chat.add(chatstring);
		timeofmessage.add(System.currentTimeMillis());
	}

	/**
	 *
	 * @param key
	 * @param c
	 * @return Still in chat-mode
	 */
	public boolean keyPressed(int key, char c) {
		switch (key) {
		case Input.KEY_ENTER:
			chatAddLine(curr);
			Console.doCommand(curr);
			displaychat = false;
			curr = "";
			return false;
		case Input.KEY_BACK:
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
