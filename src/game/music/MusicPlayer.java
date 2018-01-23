package game.music;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import game.Dirtbox;
import game.MainGameState;

public class MusicPlayer extends Thread {

	public MusicPlayer() {
	}

	static Clip PlayFile(File file)
			throws UnsupportedAudioFileException, IOException, LineUnavailableException,
			InterruptedException {
		AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
		Clip audioclip = AudioSystem.getClip();
		audioclip.open(audioIn);
		return audioclip;
	}

	static double SongLength(File file)
			throws UnsupportedAudioFileException, IOException {
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
		AudioFormat format = audioInputStream.getFormat();
		long frames = audioInputStream.getFrameLength();
		double durationInSeconds = (frames + 0.0) / format.getFrameRate();
		return durationInSeconds;
	}

	// Sound Files
	File soundFile = new File("data/music/bossintro.wav");
	File pauseMusic = new File("data/music/pausemusic.wav");

	public void pauseMusic() throws InterruptedException {
		try {
			Clip audioclip = MusicPlayer.PlayFile(pauseMusic);
			audioclip.start();
			for (int i = 0; i < MusicPlayer.SongLength(pauseMusic)
					* 1000000000.0; i += 1000000000.0 / Dirtbox.DEFAULT_FRAME_RATE) {
				if (MainGameState.inGame) {
					audioclip.stop();
					break;
				}
				Thread.sleep((long) (1000.0 / Dirtbox.DEFAULT_FRAME_RATE));
			}
		} catch (InterruptedException | UnsupportedAudioFileException
				| IOException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static boolean ambianceplaying = false;

	@Override
	public void run() {
		while (true) {
			if (!ambianceplaying && MainGameState.inGame) {
				Thread ambianceplayer = new Thread(new AmbiancePlayer());
				ambianceplayer.start();
				ambianceplaying = true;
			}

			if (!MainGameState.inGame) {
				ambianceplaying = false;
				try {
					pauseMusic();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep((long) (1000.0 / Dirtbox.DEFAULT_FRAME_RATE));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
