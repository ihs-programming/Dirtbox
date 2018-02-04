package game.utils;

import java.util.Scanner;

import game.Viewport;
import game.World;
import game.entities.ControllableCharacter;

public class Console extends Thread {

	private Scanner scanner;

	@Override
	public void run() {
		scanner = new Scanner(System.in);
		System.out.print("Console input: \n");
		String input = scanner.nextLine();

		// Use the following format for commands which take an input value

		if (input.startsWith("!settime ")) {
			input = input.replace("!settime ", "");
			try {
				Viewport.globaltimer = Long.parseLong(input);
				System.out.println("Time set to " + Viewport.globaltimer);
			} catch (NumberFormatException e) {
				System.out.println("\"" + input + "\" is not a valid time");
			}
		}

		// Use the following format for commands which require no input and
		// instead
		// return a value

		else if (input.equals("!time")) {
			System.out.println("Time is: " + Viewport.globaltimer);
		}

		else if (input.equals("!characters")) {
			System.out.println("Number of characters: " + World.characters.size());
		}

		else if (input.equals("!backgroundsprites")) {
			System.out.println(
					"Number of background sprites: " + World.backgroundsprites.size());
		} else if (input.equals("!fly")) {
			ControllableCharacter.flying = !ControllableCharacter.flying;
		}

		else {
			System.out.println("\"" + input + "\" is not a recognized command");
		}
		return;
	}

	public Console() {
	}

}
