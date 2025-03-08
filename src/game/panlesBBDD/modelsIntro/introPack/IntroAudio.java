package game.panlesBBDD.modelsIntro.introPack;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Gestiona la reproducción de audio para la introducción del juego.
 * <p>
 * Utiliza la API {@code javax.sound.sampled} para cargar, reproducir y detener
 * el audio de introducción en bucle continuo.
 * </p>
 */
public class IntroAudio {
    private Clip clip; // Objeto Clip que almacena y reproduce el audio.

    /**
     * Crea una instancia de IntroAudio cargando el archivo de audio especificado.
     *
     * @param audioFilePath Ruta del archivo de audio a reproducir.
     */
    public IntroAudio(String audioFilePath) {
        try {
            File audioFile = new File(audioFilePath); // Carga el archivo desde el sistema de archivos.
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Reproducción en bucle infinito.
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Detiene la reproducción del audio, si está en ejecución.
     */
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * Inicia la reproducción del audio, si no está ya en ejecución.
     */
    public void start() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
        }
    }
}
