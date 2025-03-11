package game.effects;

/**
 * Representa una partícula individual con posición, velocidad, vida y apariencia.
 */
public class Particle {
    public float x, y;
    public float dx, dy;
    public float life, maxLife;
    public float size;
    public int colorRGB; // Color (sin alpha); la alpha se calculará en función de la vida

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

    public void update() {
        x += dx;
        y += dy;
        life -= 1;
    }

    public boolean isAlive() {
        return life > 0;
    }

    /**
     * Retorna la opacidad actual (0 a 1) según la vida restante.
     */
    public float getAlpha() {
        return Math.max(0, life / maxLife);
    }
}
