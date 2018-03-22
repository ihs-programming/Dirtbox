package game.utils;

import java.awt.Dimension;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import game.entities.ControllableCharacter;
import game.network.UDPBroadcast;
import game.network.event.ChatEvent;
import game.save.Saver;
import game.world.World;

/**
 * Handles various commands in the game
 */
public class Console extends Thread {

	private ControllableCharacter character;
	private World world;
	private Saver saver = new Saver();
	private Map<Integer, InetSocketAddress> serverUI = new HashMap<>();

	private JFrame frame;
	private JTextField commandLine;

	public static final String ERROR = "Unknown command";

	private static HashMap<String, CommandParser> commands = new HashMap<>();
	static {
		addCommand(args -> "Pong", "!ping");
		addCommand(args -> {
			StringBuilder ret = new StringBuilder();

			for (String s : commands.keySet()) {
				if ("!help".equals(s)) {
					continue;
				}
				ret.append(String.format("%s : %s\n", s, commands.get(s).command(null)));
			}
			return ret.toString();
		}, "!help");
		addCommand(args -> {
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

		}, "!list");
		addCommand(args -> {
			if (args == null) {
				return "Connects to an address";
			}
			if (args.length == 0) {
				return "Please provide an address";
			}
			return "Connecting to " + args[0];
		}, "!connect");

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

	public Console(ControllableCharacter character, World world) {
		this.character = character;
		this.world = world;
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