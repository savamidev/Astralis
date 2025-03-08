package game.controls.movements;

import java.awt.Image;

/**
 * Gestiona la animación de un conjunto de imágenes (frames) para un objeto.
 * <p>
 * Esta clase actualiza el frame actual en función de un retraso temporal,
 * permitiendo reproducir animaciones de manera cíclica.
 * </p>
 */
public class AnimationPlayer {
    private Image[] frames;
    private int currentFrame;
    private long lastTime;
    private long delay;

    /**
     * Crea una instancia de AnimationPlayer con los frames y el retraso especificado.
     *
     * @param frames Array de imágenes que representan los diferentes frames de la animación.
     * @param delay  Tiempo en milisegundos entre cada cambio de frame.
     */
    public AnimationPlayer(Image[] frames, long delay) {
        this.frames = frames;
        this.delay = delay;
        this.currentFrame = 0;
        this.lastTime = System.currentTimeMillis();
    }

    /**
     * Actualiza el frame actual de la animación basándose en el tiempo transcurrido.
     * Si ha pasado el tiempo definido en {@code delay}, se avanza al siguiente frame.
     */
    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastTime > delay) {
            currentFrame = (currentFrame + 1) % frames.length;
            lastTime = now;
        }
    }

    /**
     * Obtiene la imagen del frame actual de la animación.
     *
     * @return La imagen actual.
     */
    public Image getCurrentFrame() {
        return frames[currentFrame];
    }
}
