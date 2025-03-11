package game.effects;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simula partículas tipo hojas, polvo o viento en el área del mapa comprendida entre y=0 y un valor máximo (por ejemplo, el floor).
 * Se mantiene un número máximo de partículas (maxParticles) ajustable.
 * Las partículas varían en velocidad (algunas rápidas, otras lentas) y en tonalidades otoñales.
 */
public class LeafParticleEffect {
    private List<game.effects.Particle> particles;
    private Random random;
    private int worldWidth;
    private int maxY;  // Altura máxima en la que pueden aparecer las partículas (por ejemplo, floor)
    private int maxParticles;

    public LeafParticleEffect(int worldWidth, int maxY, int maxParticles) {
        this.worldWidth = worldWidth;
        this.maxY = maxY;
        this.maxParticles = maxParticles;
        particles = new ArrayList<>();
        random = new Random();
    }

    public void update() {
        // Actualizar y eliminar partículas muertas
        for (int i = particles.size() - 1; i >= 0; i--) {
            game.effects.Particle p = particles.get(i);
            p.update();
            if (!p.isAlive()) {
                particles.remove(i);
            }
        }
        // Generar nuevas partículas hasta alcanzar el máximo
        while (particles.size() < maxParticles) {
            spawnParticle();
        }
    }

    private void spawnParticle() {
        float size = 3 + random.nextFloat() * 4; // Tamaño entre 3 y 7
        // Aseguramos que la partícula se genere de modo que su parte inferior no sobrepase maxY (floor)
        float y = random.nextFloat() * (maxY - size);
        float x = random.nextFloat() * worldWidth;
        // Velocidad aleatoria: variando entre -1 y 1 para ambos ejes
        float dx = (random.nextFloat() - 0.5f) * 2;
        float dy = (random.nextFloat() - 0.5f) * 2;
        float maxLife = 60 + random.nextInt(60); // Vida entre 60 y 120 frames
        // Tonalidades otoñales (rojos, naranjas, marrones)
        Color[] autumnColors = {
                new Color(205, 92, 92),
                new Color(210, 105, 30),
                new Color(244, 164, 96),
                new Color(139, 69, 19)
        };
        Color chosen = autumnColors[random.nextInt(autumnColors.length)];
        int colorRGB = chosen.getRGB();
        game.effects.Particle particle = new game.effects.Particle(x, y, dx, dy, maxLife, colorRGB, size);
        particles.add(particle);
    }


    public void draw(Graphics2D g2d) {
        for (game.effects.Particle p : particles) {
            Color baseColor = new Color(p.colorRGB);
            Color c = new Color(baseColor.getRed() / 255f, baseColor.getGreen() / 255f, baseColor.getBlue() / 255f, p.getAlpha());
            g2d.setColor(c);
            g2d.fillOval((int) p.x, (int) p.y, (int) p.size, (int) p.size);
        }
    }
}
