package game.utils;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Input;

import game.Viewport;

public class Chat {
	private String curr = "";
	public ArrayList<String> chat = new ArrayList<>();
	public ArrayList<Long> timeofmessage = new ArrayList<>();
	public boolean displaychat = false;
	public Console console;

	public Chat(Console console) {
		this.console = console;
	}

	public void chatAddLine(String chatstring) {
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
			String result = console.doCommand(curr);
			chatAddLine(result);
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

	public void draw(Viewport vp) {
		int chatlength = chat.size();
		int totalDisp = 0;
		for (int i = 1; i <= 10; i++) {
			if (i <= chatlength
					&& (System.currentTimeMillis()
							- timeofmessage.get(chatlength - i) <= 5000
							|| displaychat)) {
				int lines = chat.get(chatlength - i).split("\n").length;
				totalDisp += lines;
				vp.draw(chat.get(chatlength - i), 5,
						(int) vp.getViewShape().getHeight() - 25 - 20 * totalDisp,
						Color.white);
			}
		}
		vp.draw(getMessage(), 5, (int) vp.getViewShape().getHeight() - 25, Color.white);
	}
}
