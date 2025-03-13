package game.audio;

import javax.sound.sampled.*;
import java.net.URL;

/**
 * Gestiona el sonido de fondo del juego.
 * <p>
 * Esta clase carga un clip de audio desde un recurso y lo reproduce en bucle.
 * Se puede iniciar con un efecto de fade‑in o directamente a volumen completo.
 * </p>
 */
public class BackgroundSound {
    private Clip clip;
    private FloatControl gainControl;

    private final float MAX_GAIN = 0.0f;
    private final float MIN_GAIN = -40.0f;
    private final float GAIN_STEP = 1.0f;
    private final int FADE_INTERVAL = 200;

    /**
     * Crea una instancia de BackgroundSound cargando el clip de audio desde la ruta especificada.
     *
     * @param path Ruta del recurso de audio (por ejemplo, "/resources/sound/background.wav").
     */
    public BackgroundSound(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("No se encontró el archivo de audio de fondo: " + path);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(MIN_GAIN);
        } catch (Exception e) {
            System.err.println("Error al cargar el sonido de fondo: " + e.getMessage());
        }
    }

    /**
     * Inicia la reproducción en bucle del sonido de fondo con efecto fade‑in.
     * Este método aumenta progresivamente el volumen hasta llegar al máximo.
     */
    public void playWithFadeIn() {
        if (clip == null) {
            return;
        }
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        new Thread(() -> {
            float currentGain = MIN_GAIN;
            while (currentGain < MAX_GAIN) {
                try {
                    Thread.sleep(FADE_INTERVAL);
                } catch (InterruptedException e) {
                    System.err.println("Fade in interrumpido: " + e.getMessage());
                }
                currentGain += GAIN_STEP;
                if (currentGain > MAX_GAIN) {
                    currentGain = MAX_GAIN;
                }
                gainControl.setValue(currentGain);
            }
            System.out.println("Fade in completado, volumen máximo alcanzado.");
        }).start();
    }

    /**
     * Reproduce el sonido de fondo a volumen completo sin efecto fade‑in.
     */
    public void play() {
        if (clip == null) {
            return;
        }
        gainControl.setValue(MAX_GAIN);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Detiene la reproducción del sonido de fondo.
     */
    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
}
