package game.utils;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import game.Viewport;
import game.entities.ControllableCharacter;
import game.network.UDPBroadcast;
import game.network.event.ChatEvent;
import game.save.Saver;
import game.world.GameWorld;

/**
 * Handles various commands in the game
 */
public class Console extends Thread {

	private ControllableCharacter character;
	private GameWorld world;
	private Saver saver = new Saver();
	private Map<Integer, InetSocketAddress> serverUI = new HashMap<>();

	private JFrame frame;
	private JTextField commandLine;

	public static final String ERROR = "Unknown command";

	private static HashMap<String, CommandParser> commands = new HashMap<>();

	private void initializeCommandList() {
		addCommand(new CommandParser() {
			@Override
			public String getHelp() {
				return buildHelp();
			}

			@Override
			public String command(String[] args) {
				return buildHelp();
			}

			private String buildHelp() {
				StringBuilder ret = new StringBuilder();
				for (String s : commands.keySet()) {
					if ("!help".equals(s)) {
						continue;
					}
					ret.append(String.format("%s : %s\n", s, commands.get(s).getHelp()));
				}
				return ret.toString();
			}
		}, "!help");
		addCommand(new CommandParser() {
			@Override
			public String getHelp() {
				return "lists all availible servers to connect to";
			}

			@Override
			public String command(String[] args) {
				if (args == null) {
					return "Searches for avaliable servers";
				}
				try {
					UDPBroadcast broad = new UDPBroadcast(1000, 2000);
					Thread.sleep(2000);
					return "Active addresses : " + broad.getActiveAddresses().toString();
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
				return "";
			}

		}, "!list");
		addCommand(new CommandParser() {
			@Override
			public String getHelp() {
				return "connects to server";
			}

			@Override
			public String command(String[] args) {
				if (args == null) {
					return "Connects to an address";
				}
				if (args.length == 0) {
					return "Please provide an address";
				}
				return "Connecting to " + args[0];
			}
		}, "!connect");
		addGameCommands();
	}

	private void addGameCommands() {
		addCommand(command -> {
			character.doHit(-10000);
			return "";
		}, "!addhealth");
		addCommand(new CommandParser() {
			@Override
			public String getHelp() {

				return "Breaks all blocks in a large radius around the player";
			}

			@Override
			public String command(String[] command) {
				Point p = new Point((int) character.getHitbox().getX(),
						(int) character.getHitbox().getY());
				world.explode(p, 20);
				return "";
			}
		}, "!explode");
		addCommand(new CommandParser() {
			@Override
			public String getHelp() {

				return "increases movement speed tenfold";
			}

			@Override
			public String command(String[] command) {
				character.flying = !character.flying;
				return "";
			}
		}, "!fly");
		addCommand(new CommandParser() {
			@Override
			public String getHelp() {

				return "returns the time";
			}

			@Override
			public String command(String[] command) {
				String output = "";
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
				return output;
			}
		}, "!time");
	}

	private static interface CommandParser {
		/**
		 * Do a command.
		 *
		 * @param args
		 *            is null if we want the help message.
		 * @return
		 */
		String command(String args[]);

		default String getHelp() {
			return "not documented...";
		};
	}

	public Console(ControllableCharacter character, GameWorld world) {
		this.character = character;
		this.world = world;
		initializeCommandList();
	}

	@Override
	public void run() {
		frame.setVisible(true);
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

	public static void addCommand(CommandParser cp, String... texts) {
		Arrays.asList(texts).forEach(s -> commands.put(s, cp));
	}

	public void sendChatMessage(String s) {
		ChatEvent ce = new ChatEvent(s);
		world.getClient().sendEvent(ce);
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
	}
}