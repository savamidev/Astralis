package game.panlesBBDD.menuPrincipal;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class IntroAudio {
    private Clip clip;

    public IntroAudio(String audioFilePath) {
        try {
            File audioFile = new File(audioFilePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void start() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
        }
    }
}
