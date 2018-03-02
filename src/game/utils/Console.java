package game.utils;

import java.awt.Dimension;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import game.entities.ControllableCharacter;
import game.network.Client;
import game.network.Server;
import game.save.Saver;
import game.world.World;

/**
 * Handles various commands in the game
 */
public class Console extends Thread {
	public static final String ERROR = "Unknown command";
	private static HashMap<String, CommandParser> commands = new HashMap<>();
	static {
		addCommand("!ping", args -> "Pong");
		addCommand("!pong", args -> {
			if (args != null && args[1].equals("a")) {
				return "yay";
			}
			return "Usage !pong. Stuff";
		});
		addCommand("!help", args -> {
			StringBuilder ret = new StringBuilder();

			for (String s : commands.keySet()) {
				if (s.equals("!help")) {
					continue;
				}
				ret.append(String.format("!%s : %s\n", s, commands.get(s).command(null)));
			}
			return ret.toString();
		});

		// You can add commands from other places as well!
	}

	private static interface CommandParser {
		/**
		 * Do a command.
		 *
		 * @param args
		 *            is null if we want the debug message.
		 * @return
		 */
		public String command(String args[]);
	}

	private ControllableCharacter character;
	private World world;
	private Client client = new Client();
	private Server server;
	private Saver saver = new Saver();
	private Map<Integer, InetSocketAddress> serverUI = new HashMap<>();

	private JFrame frame;
	private JTextField commandLine;

	public Console(ControllableCharacter character, World world) {
		this.character = character;
		this.world = world;
	}

	@Override
	public void run() {
		frame.setVisible(true);
		return;
	}

	public Console() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		commandLine = new JTextField();
		commandLine.addActionListener((e) -> doCommand(commandLine.getText()));

		commandLine.setPreferredSize(new Dimension(100, 20));
		frame.add(commandLine);
		frame.pack();
	}

	public static void addCommand(String text, CommandParser cp) {
		commands.put(text, cp);
	}

	public String doCommand(String input) {
		if (input.length() == 0 || input.charAt(0) != '!') {
			return "";
		}
		String[] args = input.split(" ");
		if (commands.containsKey(args[0])) {
			return commands.get(args[0]).command(args);
		}
		return ERROR;
		// List of all commands and what they do
		// note that commandhelp must be of the form [(command),(command)...] :
		// (helptext)
		/*
		 * ArrayList<String> commandhelp = new ArrayList<>();
		 * commandhelp.add("!help, !h, !? : returns a list of commands");
		 * commandhelp.add("!time : returns the time");
		 * commandhelp.add("!time set [time] : sets the current time to [time]");
		 * commandhelp.add("!fly : increases movement speed tenfold");
		 * commandhelp.add("!listservers : lists availible servers");
		 * commandhelp.add("!host : starts hosting server on computer");
		 * commandhelp.add("!stophosting: stops the current server (if running)");
		 * commandhelp.add(
		 * "!explode : Breaks all blocks in a large radius around the player");
		 * commandhelp.add(
		 * "!connect [number] : connects to server denoted by number created from !listservers"
		 * );
		 * commandhelp.add("!disconnect : Disconnects from currently connected server"
		 * ); commandhelp.add("!viewmessages : Views all messages from hosts");
		 * commandhelp.add(
		 * "!send [message] : sends a message to the currently connected server");
		 * commandhelp.add(
		 * "!constatus : prints information about the currently connected to server");
		 * if (input.startsWith("!")) { String command[] = input.split(" "); return
		 * executeCommand(command, commandhelp); } return null; }
		 *
		 * public String executeCommand(String command[], ArrayList<String> commandhelp)
		 * { String output = ""; // return help value if (command.length > 1) { if
		 * (command[1].equals("?")) { for (int i = 0; i < commandhelp.size(); i++) { if
		 * (commandhelp.get(i).startsWith(command[0])) { output += commandhelp.get(i) +
		 * "\n"; } } return output; } }
		 *
		 * boolean commandExists = false; for (String help : commandhelp) { if
		 * (help.split(":")[0].matches(".*\\b" + command[0].substring(1) + "\\b.*")) {
		 * commandExists = true; break; } } if (!commandExists) { output += "\"" +
		 * command[0] + "\" is not a recognized command. Use \"!help\" for help\n";
		 * return output; }
		 *
		 * output += runGameCommand(command); output += runNetworkCommand(command);
		 * output += getNetworkStatus(command);
		 *
		 * switch (command[0]) { case "!h": case "!help": case "!?": for (int i = 0; i <
		 * commandhelp.size(); i++) { output += commandhelp.get(i) + "\n"; } break; }
		 * return output; }
		 *
		 * private String runGameCommand(String[] command) { String output = "";
		 *
		 * switch (command[0]) { // "!time" command, sets and returns time case "!time":
		 * if (command.length < 2) { output += "Time is: " + Viewport.globaltimer +
		 * "\n"; } else { if (command.length > 1 && command[1].equals("set")) { try {
		 * Viewport.globaltimer = Long.parseLong(command[2]); output += "Time set to " +
		 * Viewport.globaltimer + "\n"; } catch (NumberFormatException e) { output +=
		 * "\"" + command[2] + "\" is not a valid time. Use \"!time ?\" for help\n"; } }
		 * } break;
		 *
		 * case "!save": saver.save(world, world.regionGenerator); output = null; break;
		 *
		 * case "!load": saver.load(world); output = null; break;
		 *
		 * // "!fly" command, changes flying state case "!f": case "!fly":
		 * character.flying = !character.flying; output = null; break; case
		 * "!addhealth": character.doHit(-10000); break;
		 *
		 * case "!explode": Point p = new Point((int) character.getHitbox().getX(),
		 * (int) character.getHitbox().getY()); world.explode(p, 20); output = null;
		 * break; } return output;
		 */

	}

	private String runNetworkCommand(String[] command) {
		String output = "";

		switch (command[0]) {
		case "!listservers":
			Map<InetSocketAddress, String> hostinfo = client.getHostInfo();
			serverUI.clear();
			int ind = 0;
			for (Map.Entry<InetSocketAddress, String> entry : hostinfo.entrySet()) {
				serverUI.put(ind, entry.getKey());
				output += Integer.toString(ind) + " : " + entry.getValue() + "\n";
				ind++;
			}
			if (hostinfo.isEmpty()) {
				output += "No hosts found...";
			}
			break;
		case "!host":
			server = new Server();
			break;
		case "!stophosting":
			if (server != null) {
				server.stop();
				server = null;
			}
			break;
		case "!connect":
			if (command.length < 2) {
				output += "Need to specify a server";
				break;
			} else {
				try {
					int connectInd = Integer.parseInt(command[1]);
					if (!serverUI.containsKey(connectInd)) {
						output += "Unable to find servers";
					} else {
						InetSocketAddress addr = serverUI.get(connectInd);
						client.disconnect();
						client.connect(addr);
					}
				} catch (IllegalFormatException e) {
					output += "Must specify number denoting server index";
				} catch (IOException e) {
					output += "Unable to connect to server";
					e.printStackTrace();
				}
			}
			break;

		case "!disconnect":
			client.disconnect();
			break;

		case "!send":
			if (command.length >= 2) {
				try {
					client.send(command[1]);
					output += "Sent message";
				} catch (IOException e) {
					output += "Unable to send message";
				}
			} else {
				output += "No message to send";
			}
			break;

		}
		return output;
	}

	private String getNetworkStatus(String[] command) {
		String output = "";
		switch (command[0]) {
		case "!hoststatus":
			if (server == null) {
				output += "Not currently hosting a server";
			} else {
				output += "Server is currently active";
			}
			break;
		case "!viewmessages":
			output += String.join("\n", client.getMessages());
			break;
		case "!constatus":
			Optional<InetSocketAddress> addr = client.getCurrentHost();
			if (addr.isPresent()) {
				output += addr.get().toString();
			} else {
				output += "Not currently connected to server";
			}
			break;
		}
		return output;
	}
}