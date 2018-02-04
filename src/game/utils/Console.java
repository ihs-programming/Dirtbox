package game.utils;

import java.awt.Dimension;

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

	private void doCommand(String input) {
		commandLine.setText("");
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
	}

}
