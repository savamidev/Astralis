package game.effects;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simula un efecto de partículas que representan hojas o polvo, agregando
 * dinamismo visual en áreas específicas del mapa.
 * Genera partículas con propiedades aleatorias para lograr un efecto natural.
 */
public class LeafParticleEffect {
    private List<game.effects.Particle> particles;
    private Random random;
    private int worldWidth;
    private int maxY;  // Altura máxima en la que pueden aparecer las partículas (por ejemplo, el floor)
    private int maxParticles;

    /**
     * Crea un nuevo efecto de partículas para hojas.
     *
     * @param worldWidth   Ancho del mundo, usado para posicionar las partículas horizontalmente.
     * @param maxY         Altura máxima en la que pueden generarse las partículas.
     * @param maxParticles Número máximo de partículas simultáneas.
     */
    public LeafParticleEffect(int worldWidth, int maxY, int maxParticles) {
        this.worldWidth = worldWidth;
        this.maxY = maxY;
        this.maxParticles = maxParticles;
        particles = new ArrayList<>();
        random = new Random();
    }

    /**
     * Actualiza el estado de las partículas:
     * <ul>
     *     <li>Actualiza la posición de cada partícula.</li>
     *     <li>Elimina las partículas que han expirado.</li>
     *     <li>Genera nuevas partículas hasta alcanzar el máximo configurado.</li>
     * </ul>
     */
    public void update() {
        for (int i = particles.size() - 1; i >= 0; i--) {
            game.effects.Particle p = particles.get(i);
            p.update();
            if (!p.isAlive()) {
                particles.remove(i);
            }
        }
        while (particles.size() < maxParticles) {
            spawnParticle();
        }
    }

    /**
     * Genera una nueva partícula con propiedades aleatorias:
     * <ul>
     *     <li>Tamaño entre 3 y 7 píxeles.</li>
     *     <li>Posición aleatoria dentro del ancho del mundo y hasta {@code maxY}.</li>
     *     <li>Velocidad aleatoria en ambos ejes.</li>
     *     <li>Vida entre 60 y 120 frames.</li>
     *     <li>Color basado en tonalidades otoñales.</li>
     * </ul>
     */
    private void spawnParticle() {
        float size = 3 + random.nextFloat() * 4; // Tamaño entre 3 y 7
        float y = random.nextFloat() * (maxY - size);
        float x = random.nextFloat() * worldWidth;
        float dx = (random.nextFloat() - 0.5f) * 2;
        float dy = (random.nextFloat() - 0.5f) * 2;
        float maxLife = 60 + random.nextInt(60); // Vida entre 60 y 120 frames
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

    /**
     * Dibuja todas las partículas en el contexto gráfico proporcionado.
     *
     * @param g2d Objeto Graphics2D utilizado para renderizar las partículas.
     */
    public void draw(Graphics2D g2d) {
        for (game.effects.Particle p : particles) {
            Color baseColor = new Color(p.colorRGB);
            Color c = new Color(baseColor.getRed() / 255f, baseColor.getGreen() / 255f, baseColor.getBlue() / 255f, p.getAlpha());
            g2d.setColor(c);
            g2d.fillOval((int) p.x, (int) p.y, (int) p.size, (int) p.size);
        }
    }
}
