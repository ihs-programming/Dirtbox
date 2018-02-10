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
		commandhelp.add("!time : returns the time");
		commandhelp.add("!time set [time] : sets the current time to [time]");
		commandhelp.add("!characters : returns the total number of chracters");
		commandhelp.add("!fly : increases movement speed tenfold");
		if (input.startsWith("!")) {
			String command[] = input.split(" ");
			executeCommand(command, commandhelp);
		}
	}

	public static void executeCommand(String command[], ArrayList<String> commandhelp) {

		// return help value
		if (command.length > 1 && command[1].equals("?")) {
			for (int i = 0; i < commandhelp.size(); i++) {
				if (commandhelp.get(i).startsWith(command[0])) {
					Chat.chatAddLine(commandhelp.get(i));
				}
			}
			return;
		}

		switch (command[0]) {

		case "!h":
		case "!help":
		case "!?":
			for (int i = 0; i < commandhelp.size(); i++) {
				Chat.chatAddLine(commandhelp.get(i));
			}
			break;

		// "!time" command, sets and returns time
		case "!time":
			if (command.length < 1) {
				Chat.chatAddLine("Time is: " + Viewport.globaltimer);
			} else {
				if (command[1].equals("set")) {
					try {
						Viewport.globaltimer = Long.parseLong(command[2]);
						Chat.chatAddLine("Time set to " + Viewport.globaltimer);
					} catch (NumberFormatException e) {
						Chat.chatAddLine("\"" + command[2]
								+ "\" is not a valid time. Use \"!time ?\" for help");
					}
				}
			}
			break;

		// "!characters" command, returns number of characters
		case "!characters":
			synchronized (World.entities) {
				Chat.chatAddLine("Number of characters: " + World.entities.size());
			}
			break;

		// "!fly" command, changes flying state
		case "!f":
		case "!fly":
			ControllableCharacter.flying = !ControllableCharacter.flying;
			break;
		// if command doesn't work, return this
		default:
			Chat.chatAddLine("\"" + command[0]
					+ "\" is not a recognized command. Use \"!help\" for help");
			break;
		}
	}

}
