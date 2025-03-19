package game.effects;

/**
 * Representa una partícula individual con posición, velocidad, vida y apariencia.
 * La opacidad de la partícula se calcula en función de la vida restante.
 */
public class Particle {
    public float x, y;
    public float dx, dy;
    public float life, maxLife;
    public float size;
    public int colorRGB; // Color (sin alpha); la alpha se calculará en función de la vida

    /**
     * Crea una nueva partícula con las propiedades especificadas.
     *
     * @param x       Posición X inicial.
     * @param y       Posición Y inicial.
     * @param dx      Velocidad en el eje X.
     * @param dy      Velocidad en el eje Y.
     * @param maxLife Vida máxima (y también inicial) de la partícula.
     * @param colorRGB Color de la partícula (sin componente alfa).
     * @param size    Tamaño de la partícula.
     */
    public Particle(float x, float y, float dx, float dy, float maxLife, int colorRGB, float size) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.life = maxLife;
        this.maxLife = maxLife;
        this.colorRGB = colorRGB;
        this.size = size;
    }

    /**
     * Actualiza la posición de la partícula y decrementa su vida.
     */
    public void update() {
        x += dx;
        y += dy;
        life -= 1;
    }

    /**
     * Indica si la partícula sigue "viva" (es decir, si su vida es mayor que cero).
     *
     * @return {@code true} si la partícula aún tiene vida; {@code false} en caso contrario.
     */
    public boolean isAlive() {
        return life > 0;
    }

    /**
     * Retorna la opacidad actual (valor entre 0 y 1) de la partícula en función de la vida restante.
     *
     * @return Un valor flotante representando la opacidad actual.
     */
    public float getAlpha() {
        return Math.max(0, life / maxLife);
    }
}
