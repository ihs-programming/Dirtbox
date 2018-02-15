package game.utils;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import game.Viewport;
import game.entities.ControllableCharacter;
import game.network.Client;
import game.network.Server;
import game.world.World;

/**
 * Handles various commands in the game
 */
public class Console extends Thread {
	private ControllableCharacter character;
	private World world;
	private Client client = new Client();
	private Server server;

	public Console(ControllableCharacter character, World world) {
		this.character = character;
		this.world = world;
	}

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

	public String doCommand(String input) {

		// List of all commands and what they do
		ArrayList<String> commandhelp = new ArrayList<>();
		commandhelp.add("!help, !h, !? : returns a list of commands");
		commandhelp.add("!time : returns the time");
		commandhelp.add("!time set [time] : sets the current time to [time]");
		commandhelp.add("!characters : returns the total number of chracters");
		commandhelp.add("!fly : increases movement speed tenfold");
		commandhelp.add("!listservers : lists availible servers");
		commandhelp.add("!host : starts hosting server on computer");
		commandhelp.add("!stophosting: stops the current server (if running)");
		if (input.startsWith("!")) {
			String command[] = input.split(" ");
			return executeCommand(command, commandhelp);
		}
		return "";
	}

	public String executeCommand(String command[], ArrayList<String> commandhelp) {
		String output = "";
		// return help value
		if (command.length > 1 && command[1].equals("?")) {
			for (int i = 0; i < commandhelp.size(); i++) {
				if (commandhelp.get(i).startsWith(command[0])) {
					output += commandhelp.get(i) + "\n";
				}
			}
			return output;
		}

		switch (command[0]) {

		case "!h":
		case "!help":
		case "!?":
			for (int i = 0; i < commandhelp.size(); i++) {
				output += commandhelp.get(i) + "\n";
			}
			break;

		// "!time" command, sets and returns time
		case "!time":
			if (command.length < 2) {
				output += "Time is: " + Viewport.globaltimer + "\n";
			} else {
				if (command.length > 1 && command[1].equals("set")) {
					try {
						Viewport.globaltimer = Long.parseLong(command[2]);
						output += "Time set to " + Viewport.globaltimer + "\n";
					} catch (NumberFormatException e) {
						output += "\"" + command[2]
								+ "\" is not a valid time. Use \"!time ?\" for help\n";
					}
				}
			}
			break;

		// "!characters" command, returns number of characters
		case "!characters":
			output += "Number of characters: " + World.entities.size() + "\n";
			break;

		// "!fly" command, changes flying state
		case "!f":
		case "!fly":
			ControllableCharacter.flying = !ControllableCharacter.flying;
			break;
		case "!addhealth":
			character.doHit(-10000);
			break;
		case "!explode":
			Point p = new Point((int) character.getHitbox().getX(),
					(int) character.getHitbox().getY());
			world.explode(p, 20);
			break;
		case "!listservers":
			Collection<String> hosts = client.getHostInfo().values();
			if (hosts.isEmpty()) {
				output += "No hosts found...";
			} else {
				output += String.join("\n", hosts);
			}
			break;
		case "!host":
			server = new Server();
			break;
		case "!hoststatus":
			if (server == null) {
				output += "Not currently hosting a server";
			} else {
				output += "Server is currently active";
			}
			break;
		case "!stophosting":
			if (server != null) {
				server.stop();
				server = null;
			}
			break;
		case "!connect":
			break;
		// if command doesn't work, return this
		default:
			output += "\"" + command[0]
					+ "\" is not a recognized command. Use \"!help\" for help\n";
			break;
		}
		return output;
	}

}
