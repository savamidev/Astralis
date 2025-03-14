package game.effects;

import java.awt.Color;

public class RainParticle extends game.effects.Particle {
    public int initialAlpha; // Valor alfa inicial para el efecto de transparencia
    private float baseDx, baseDy; // Velocidades base para la gota

    // Variable estática para incrementar la velocidad (1.0 = velocidad base)
    public static float speedMultiplier = 1.0f;

    public RainParticle(float x, float y, float dx, float dy, float maxLife, int colorRGB, float size, int initialAlpha) {
        super(x, y, dx, dy, maxLife, colorRGB, size);
        this.initialAlpha = initialAlpha;
        this.baseDx = dx;
        this.baseDy = dy;
    }

    // Método fábrica para crear una partícula de lluvia aleatoria
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

    @Override
    public void update() {
        // Actualizar posición usando las velocidades base multiplicadas por speedMultiplier
        x += baseDx * speedMultiplier;
        y += baseDy * speedMultiplier;
        life -= 1;
    }

    @Override
    public float getAlpha() {
        float lifeFraction = Math.max(0, life / maxLife);
        return (initialAlpha / 255f) * lifeFraction;
    }
}
