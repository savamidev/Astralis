package game.effects;

import java.awt.Color;

/**
 * Representa una partícula de lluvia que extiende la funcionalidad de {@link Particle}.
 * Incluye características específicas como velocidades base y un valor alfa inicial para efectos de transparencia.
 */
public class RainParticle extends game.effects.Particle {
    public int initialAlpha; // Valor alfa inicial para el efecto de transparencia
    private float baseDx, baseDy; // Velocidades base para la gota

    // Variable estática para incrementar la velocidad (1.0 = velocidad base)
    public static float speedMultiplier = 1.0f;

    /**
     * Crea una partícula de lluvia con las propiedades especificadas.
     *
     * @param x            Posición X inicial.
     * @param y            Posición Y inicial.
     * @param dx           Velocidad en el eje X.
     * @param dy           Velocidad en el eje Y.
     * @param maxLife      Vida máxima de la partícula.
     * @param colorRGB     Color de la partícula.
     * @param size         Tamaño de la partícula.
     * @param initialAlpha Valor alfa inicial para la transparencia.
     */
    public RainParticle(float x, float y, float dx, float dy, float maxLife, int colorRGB, float size, int initialAlpha) {
        super(x, y, dx, dy, maxLife, colorRGB, size);
        this.initialAlpha = initialAlpha;
        this.baseDx = dx;
        this.baseDy = dy;
    }

    /**
     * Método fábrica para crear una partícula de lluvia aleatoria dentro de los límites del mundo.
     *
     * @param worldWidth  Ancho del mundo.
     * @param worldHeight Altura del mundo.
     * @return Una instancia de {@link RainParticle} con propiedades generadas aleatoriamente.
     */
    public static RainParticle createRandom(int worldWidth, int worldHeight) {
        float x = (float) (Math.random() * worldWidth);
        float y = (float) (Math.random() * worldHeight);
        // Velocidades base: entre 3 y 6 para dx y entre 10 y 15 para dy
        float dx = 3 + (float) (Math.random() * 3); // entre 3 y 6
        float dy = 10 + (float) (Math.random() * 5); // entre 10 y 15
        float maxLife = worldHeight / dy;
        // Tamaño aleatorio entre 1 y 3 (más pequeño)
        float size = 1 + (float) (Math.random() * 2);
        // Genera un tono de azul: el componente azul entre 200 y 255; alfa entre 30 y 150
        int blue = 200 + (int)(Math.random() * 56);
        int alpha = 30 + (int)(Math.random() * 121);
        // Guardamos el valor azul en colorRGB (asumiendo que rojo y verde son 0)
        int colorRGB = blue;
        return new RainParticle(x, y, dx, dy, maxLife, colorRGB, size, alpha);
    }

    /**
     * Actualiza la posición de la partícula de lluvia utilizando las velocidades base
     * multiplicadas por el speedMultiplier y decrementa su vida.
     */
    @Override
    public void update() {
        x += baseDx * speedMultiplier;
        y += baseDy * speedMultiplier;
        life -= 1;
    }

    /**
     * Retorna la opacidad actual de la partícula de lluvia, escalada por el valor alfa inicial.
     *
     * @return Un valor flotante representando la opacidad actual.
     */
    @Override
    public float getAlpha() {
        float lifeFraction = Math.max(0, life / maxLife);
        return (initialAlpha / 255f) * lifeFraction;
    }
}
