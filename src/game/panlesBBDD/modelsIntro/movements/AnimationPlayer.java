package game.panlesBBDD.modelsIntro.movements;

import java.awt.*;

/**
 * Clase AnimationPlayer
 *
 * Esta clase gestiona la animación de una secuencia de imágenes (frames).
 * Controla la transición entre frames en función de un retardo (delay).
 */
public class AnimationPlayer {
    private Image[] frames;      // Array de imágenes que conforman la animación.
    private int currentFrame;    // Índice del frame actual.
    private long lastTime;       // Última vez que se actualizó el frame.
    private long delay;          // Tiempo de espera entre frames en milisegundos.

    /**
     * Constructor de AnimationPlayer.
     *
     * @param frames Array de imágenes que forman la animación.
     * @param delay  Tiempo en milisegundos entre cada frame.
     */
    public AnimationPlayer(Image[] frames, long delay) {
        this.frames = frames;
        this.delay = delay;
        this.currentFrame = 0;
        this.lastTime = System.currentTimeMillis(); // Registra el tiempo inicial.
    }

    /**
     * Actualiza la animación cambiando de frame si ha pasado el tiempo definido en delay.
     */
    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastTime > delay) { // Verifica si ha pasado el tiempo necesario.
            currentFrame = (currentFrame + 1) % frames.length; // Pasa al siguiente frame en bucle.
            lastTime = now; // Actualiza el tiempo de la última actualización.
        }
    }

    /**
     * Devuelve la imagen del frame actual.
     *
     * @return Imagen correspondiente al frame actual.
     */
    public Image getCurrentFrame() {
        return frames[currentFrame];
    }
}
