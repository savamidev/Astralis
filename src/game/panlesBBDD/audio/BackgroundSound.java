package game.audio;

import javax.sound.sampled.*;
import java.net.URL;

/**
 * Gestiona el sonido de fondo del juego aplicando un efecto fade-in.
 * <p>
 * Esta clase carga un clip de audio desde un recurso y lo reproduce en bucle,
 * incrementando gradualmente el volumen desde un nivel mínimo hasta el máximo.
 * </p>
 */
public class BackgroundSound {
    private Clip clip;
    private FloatControl gainControl;

    /** Volumen máximo en decibeles. */
    private final float MAX_GAIN = 0.0f;
    /** Volumen inicial (mínimo) en decibeles. */
    private final float MIN_GAIN = -40.0f;
    /** Incremento de volumen en cada paso del fade. */
    private final float GAIN_STEP = 1.0f;
    /** Intervalo en milisegundos entre cada incremento del fade. */
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
            // Obtiene el control de ganancia para ajustar el volumen.
            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(MIN_GAIN);
        } catch(Exception e) {
            System.err.println("Error al cargar el sonido de fondo: " + e.getMessage());
        }
    }

    /**
     * Inicia la reproducción en bucle del sonido de fondo con efecto fade-in.
     * <p>
     * El método inicia el loop continuo del clip y en un hilo separado incrementa el volumen
     * gradualmente desde el mínimo hasta alcanzar el máximo.
     * </p>
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
        System.out.println("Reproduciendo sonido de fondo con fade in.");
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
