package game.effects;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simula el efecto visual de hierba en movimiento, generando partículas que representan
 * hojas o pasto que emergen del suelo (normalmente en los pies del jugador).
 */
public class RunGrassEffect {
    private List<game.effects.Particle> particles;
    private Random random;
    private int maxParticles;

    /**
     * Crea un efecto de hierba con un número máximo de partículas.
     *
     * @param maxParticles Número máximo de partículas simultáneas.
     */
    public RunGrassEffect(int maxParticles) {
        this.maxParticles = maxParticles;
        particles = new ArrayList<>();
        random = new Random();
    }

    /**
     * Genera partículas desde el origen dado (normalmente los pies del jugador).
     * Se generan 3 partículas por llamada.
     * Los tonos de verde varían entre un verde oscuro y un verde medio.
     *
     * @param originX Coordenada X de origen.
     * @param originY Coordenada Y de origen.
     */
    public void spawnParticles(int originX, int originY) {
        int count = 3;
        for (int i = 0; i < count; i++) {
            float x = originX + random.nextInt(11) - 5;
            float y = originY + random.nextInt(5);
            // Velocidad reducida para que las partículas se mantengan más tiempo
            float dx = (random.nextFloat() - 0.5f) * 0.3f;
            float dy = -random.nextFloat() * 0.5f;
            // Vida entre 30 y 45 frames para que duren un poco más
            float life = 30 + random.nextInt(16);
            // Tono de verde: el componente verde varía entre 100 y 200
            int green = 100 + random.nextInt(101);
            int colorRGB = new Color(0, green, 0).getRGB();
            // Tamaño entre 2 y 4 píxeles
            float size = 2 + random.nextFloat() * 2;
            particles.add(new game.effects.Particle(x, y, dx, dy, life, colorRGB, size));
        }
    }

    /**
     * Actualiza el estado de todas las partículas y elimina aquellas que han expirado.
     */
    public void update() {
        for (int i = particles.size() - 1; i >= 0; i--) {
            game.effects.Particle p = particles.get(i);
            p.update();
            if (!p.isAlive()) {
                particles.remove(i);
            }
        }
    }

    /**
     * Dibuja todas las partículas de hierba en el contexto gráfico proporcionado.
     *
     * @param g2d Objeto Graphics2D utilizado para renderizar las partículas.
     */
    public void draw(Graphics2D g2d) {
        for (game.effects.Particle p : particles) {
            Color baseColor = new Color(p.colorRGB);
            Color c = new Color(
                    baseColor.getRed() / 255f,
                    baseColor.getGreen() / 255f,
                    baseColor.getBlue() / 255f,
                    p.getAlpha());
            g2d.setColor(c);
            g2d.fillOval((int)p.x, (int)p.y, (int)p.size, (int)p.size);
        }
    }
}
