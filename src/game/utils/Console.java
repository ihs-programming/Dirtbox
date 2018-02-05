package game.utils;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import game.Viewport;
import game.entities.ControllableCharacter;
import game.world.World;

public class Console extends Thread {

	@Override
	public void run() {
		frame.setVisible(true);

		return;
	}

	private JFrame frame;
	private JTextField commandLine;

	public Console() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		commandLine = new JTextField();
		commandLine.addActionListener((e) -> doCommand(commandLine.getText()));

		commandLine.setPreferredSize(new Dimension(100, 20));
		frame.add(commandLine);
		frame.pack();
	}

	public static void doCommand(String input) {

		// List of all commands and what they do
		ArrayList<String> commandhelp = new ArrayList<>();
		commandhelp.add("!help, !h, !? : returns a list of commands");
		commandhelp.add(
				"!time, !time set [time] : !time returns the time, !time set [time] sets the current time to [time]");
		commandhelp.add("!characters : returns the total number of chracters");
		commandhelp.add("!fly : increases movement speed tenfold");

		if (input.indexOf(' ') > -1) { // Check if there is more than one word.
			String command[] = input.split(" ");
			executeCommand(command, commandhelp);
		} else {
			String command = input; // Text is the first word itself.
			executeCommand(command, commandhelp);
		}
	}

	public static void executeCommand(String command[], ArrayList<String> commandhelp) {

		// return help value
		if (command[1].equals("?")) {
			for (int i = 0; i < commandhelp.size(); i++) {
				if (commandhelp.get(i).startsWith(command[0])) {
					System.out.println(commandhelp.get(i));
					return;
				}
			}
		}

		switch (command[0]) {

		// "!time" command, sets and checks time
		case "!time":
			if (command[1].equals("set")) {
				try {
					Viewport.globaltimer = Long.parseLong(command[2]);
					System.out.println("Time set to " + Viewport.globaltimer);
				} catch (NumberFormatException e) {
					System.out.println("\"" + command[2] + "\" is not a valid time");
				}
			}
			break;

		// if command doesn't work, return this
		default:
			System.out.println("\"" + command[0] + "\" is not a recognized command");
			break;
		}
	}

	public static void executeCommand(String command, ArrayList<String> commandhelp) {
		switch (command) {

		case "!h":
		case "!help":
		case "!?":
			for (int i = 0; i < commandhelp.size(); i++) {
				System.out.println(commandhelp.get(i));
			}
			break;

		// "!time" command, sets and returns time
		case "!time":
			System.out.println("Time is: " + Viewport.globaltimer);
			break;

		// "!characters" command, returns number of characters
		case "!characters":
			System.out.println("Number of characters: " + World.characters.size());
			break;

		// "!fly" command, changes flying state
		case "!fly":
			ControllableCharacter.flying = !ControllableCharacter.flying;
			break;

		// if command doesn't work, return this
		default:
			System.out.println("\"" + command + "\" is not a recognized command");
			break;
		}
	}

}
