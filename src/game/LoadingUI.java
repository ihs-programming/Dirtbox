package game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.gui.ComponentListener;

public class LoadingUI {
	public LoadingUI(GameContainer context, ComponentListener returnCallback) {
		this.context = context;

		LabelButton exitButton = generateStoneCoalButton(
				new Vector2f(context.getWidth(), context.getHeight()).scale(.5f));
		exitButton.setText("Exit game");
		exitButton.addListener(source -> {
			context.exit();
		});
		components.add(exitButton);

		LabelButton returnButton = generateStoneCoalButton(new Vector2f(100, 100));
		returnButton.setText("Return to game");
		returnButton.addListener(returnCallback);
		components.add(returnButton);
	}
}
