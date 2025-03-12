package game.effects;

import java.awt.Color;

public class RainParticle extends game.effects.Particle {
    public int initialAlpha; // Valor alfa inicial para el efecto de transparencia

    public RainParticle(float x, float y, float dx, float dy, float maxLife, int colorRGB, float size, int initialAlpha) {
        super(x, y, dx, dy, maxLife, colorRGB, size);
        this.initialAlpha = initialAlpha;
    }

    // Método fábrica para crear una partícula de lluvia aleatoria
    public static RainParticle createRandom(int worldWidth, int worldHeight) {
        float x = (float) (Math.random() * worldWidth);
        float y = (float) (Math.random() * worldHeight);
        // Velocidades aumentadas para efecto "más rápido" y trayectoria diagonal
        float dx = 3 + (float) (Math.random() * 3); // entre 3 y 6
        float dy = 10 + (float) (Math.random() * 5); // entre 10 y 15
        float maxLife = worldHeight / dy;
        // Tamaño aleatorio entre 1 y 3 (más pequeño)
        float size = 1 + (float) (Math.random() * 2);
        // Se genera un tono de azul: azul entre 200 y 255, y alfa entre 30 y 150 para efectos semitransparentes
        int blue = 200 + (int)(Math.random() * 56);
        int alpha = 30 + (int)(Math.random() * 121);
        // En este caso, asumimos que los componentes rojo y verde son 0
        int colorRGB = blue; // Se almacena el valor azul (los otros dos son 0)
        return new RainParticle(x, y, dx, dy, maxLife, colorRGB, size, alpha);
    }

    @Override
    public float getAlpha() {
        // Calcula la opacidad actual basándose en la vida restante, multiplicada por el alfa inicial
        float lifeFraction = Math.max(0, life / maxLife);
        return (initialAlpha / 255f) * lifeFraction;
    }
}
