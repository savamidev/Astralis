package game.panlesBBDD.modelsIntro.introPack;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Clase IntroAudio
 *
 * Esta clase gestiona la reproducción de un archivo de audio en bucle continuo.
 * Utiliza la API javax.sound.sampled para cargar, reproducir y detener un audio.
 */
public class IntroAudio {
    private Clip clip; // Objeto Clip que almacena y reproduce el audio.

    /**
     * Constructor de IntroAudio.
     *
     * @param audioFilePath Ruta del archivo de audio a reproducir.
     */
    public IntroAudio(String audioFilePath) {
        try {
            File audioFile = new File(audioFilePath); // Carga el archivo de audio desde el sistema de archivos.
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile); // Convierte el archivo en un flujo de audio.

            clip = AudioSystem.getClip(); // Obtiene un objeto Clip para reproducir el audio.
            clip.open(audioStream); // Carga el flujo de audio en el Clip.
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Configura el Clip para reproducirse en bucle infinito.
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace(); // Maneja posibles excepciones (archivo no compatible, error de I/O o línea de audio no disponible).
        }
    }

    /**
     * Método para detener la reproducción del audio.
     * Verifica si el Clip no es nulo y está en ejecución antes de detenerlo.
     */
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * Método para iniciar la reproducción del audio.
     * Si el Clip no es nulo y no está en ejecución, lo inicia.
     */
    public void start() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
        }
    }
}
