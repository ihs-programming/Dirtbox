package game.utils;

import java.awt.Dimension;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

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

	private ControllableCharacter character;
	private World world;
	private Client client = new Client();
	private Server server;
	private Saver saver = new Saver();
	private Map<Integer, InetSocketAddress> serverUI = new HashMap<>();

	private JFrame frame;
	private JTextField commandLine;

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
				if ("!help".equals(s)) {
					continue;
				}
				ret.append(String.format("%s : %s\n", s, commands.get(s).command(null)));
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
	}
}