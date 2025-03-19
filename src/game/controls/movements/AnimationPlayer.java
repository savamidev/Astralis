package game.controls.movements;

import java.awt.Image;

/**
 * Gestiona la animación de un conjunto de imágenes (frames) para un objeto.
 * Actualiza el frame actual basado en un retraso temporal para reproducir la animación de forma cíclica.
 */
public class AnimationPlayer {
    private Image[] frames;
    private int currentFrame;
    private long lastTime;
    private long delay;

    /**
     * Inicializa el reproductor de animación con los frames y el intervalo de cambio especificados.
     *
     * @param frames Array de imágenes que constituyen la secuencia de la animación.
     * @param delay  Intervalo de tiempo en milisegundos entre cada cambio de frame.
     */
    public AnimationPlayer(Image[] frames, long delay) {
        this.frames = frames;
        this.delay = delay;
        this.currentFrame = 0;
        this.lastTime = System.currentTimeMillis();
    }

    /**
     * Actualiza el frame actual de la animación basándose en el tiempo transcurrido.
     * Si ha pasado el intervalo de tiempo definido, se avanza al siguiente frame de forma cíclica.
     */
    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastTime > delay) {
            currentFrame = (currentFrame + 1) % frames.length;
            lastTime = now;
        }
    }

    /**
     * Obtiene la imagen correspondiente al frame actual de la animación.
     *
     * @return Imagen del frame actual.
     */
    public Image getCurrentFrame() {
        return frames[currentFrame];
    }
}
