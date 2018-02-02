package game.utils;

import java.util.Scanner;

import game.Viewport;

public class Console extends Thread {

	private Scanner scanner;

	@Override
	public void run() {
		scanner = new Scanner(System.in);
		System.out.print("Console input: \n");
		String input = scanner.nextLine();
		if (input.startsWith("!settime ")) {
			input = input.replace("!settime ", "");
			try {
				Viewport.globaltimer = Long.parseLong(input);
				System.out.println("Time set to " + Viewport.globaltimer);
			} catch (NumberFormatException e) {
				System.out.println("\"" + input + "\" is not a valid time");
			}
		} else {
			System.out.println("\"" + input + "\" is not a recognized command");
		}
		return;
	}

	public Console() {
	}

}
