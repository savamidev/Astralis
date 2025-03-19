package game.effects;

import game.controls.movements.Player;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Sistema de partículas que simula el efecto de niebla en el juego.
 * Genera y actualiza partículas dentro de un área definida, aplicando interacciones
 * de repulsión cuando el jugador se acerca.
 */
public class FogParticleSystem {
    private List<game.effects.Particle> particles;
    private int spawnTop;    // Parte superior del área de niebla = floorY - offsetAbove
    private int spawnBottom; // Parte inferior del área de niebla = 1080
    private int spawnWidth;  // Ancho en el que se generan las partículas
    private int spawnCount;  // Número de partículas que se generan en cada actualización

    /**
     * Construye un sistema de partículas de niebla.
     *
     * @param floorY     Posición Y del suelo.
     * @param offsetAbove Desplazamiento vertical hacia arriba desde el suelo para iniciar la niebla.
     * @param spawnBottom Posición Y inferior donde pueden aparecer las partículas.
     */
    public FogParticleSystem(int floorY, int offsetAbove, int spawnBottom) {
        // El área de niebla se extiende desde floorY - offsetAbove hasta spawnBottom.
        this.spawnTop = floorY - offsetAbove;
        this.spawnBottom = spawnBottom;
        particles = new ArrayList<>();
        spawnWidth = 1920; // Valor por defecto
        spawnCount = 10;   // Genera 10 partículas por frame para mayor densidad
    }

    /**
     * Establece el ancho de la zona de generación de partículas.
     *
     * @param width Ancho en píxeles.
     */
    public void setSpawnWidth(int width) {
        this.spawnWidth = width;
    }

    /**
     * Establece la cantidad de partículas a generar en cada actualización.
     *
     * @param count Número de partículas.
     */
    public void setSpawnCount(int count) {
        this.spawnCount = count;
    }

    /**
     * Actualiza las partículas existentes y genera nuevas partículas.
     * Aplica un efecto de repulsión si el jugador se encuentra cerca de una partícula.
     *
     * @param player Referencia al jugador para calcular interacciones.
     */
    public void update(Player player) {
        int playerCenterX = player.getX() + player.getWidth() / 2;
        int playerCenterY = player.getY() + player.getHeight() / 2;
        // Actualizar cada partícula y aplicar efecto de repulsión si el jugador está cerca
        for (int i = 0; i < particles.size(); i++) {
            game.effects.Particle p = particles.get(i);
            float dx = p.x - playerCenterX;
            float dy = p.y - playerCenterY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance < 50 && distance != 0) {
                float repulsionStrength = (50 - distance) / 50 * 0.5f;
                p.dx += (dx / distance) * repulsionStrength;
                p.dy += (dy / distance) * repulsionStrength;
            }
            p.update();
            if (!p.isAlive()) {
                particles.remove(i);
                i--;
            }
        }
        spawnParticles();
    }

    /**
     * Genera partículas de niebla de forma aleatoria dentro del área definida.
     */
    private void spawnParticles() {
        for (int i = 0; i < spawnCount; i++) {
            int x = (int)(Math.random() * spawnWidth);
            // y se genera entre spawnTop y spawnBottom
            int y = spawnTop + (int)(Math.random() * (spawnBottom - spawnTop));
            // Movimiento muy suave y aleatorio
            float dx = (float)((Math.random() - 0.5) * 0.2);
            float dy = (float)(Math.random() * 0.2 - 0.1);
            float life = 200 + (float)(Math.random() * 50);
            // Tamaño entre 1 y 3 píxeles para partículas más pequeñas
            float size = 1 + (float)(Math.random() * 2);
            int color = 0xBBBBBB; // Gris claro
            particles.add(new game.effects.Particle(x, y, dx, dy, life, color, size));
        }
    }

    /**
     * Dibuja las partículas de niebla en el contexto gráfico proporcionado.
     *
     * @param g2 Objeto Graphics2D utilizado para el dibujo.
     */
    public void draw(Graphics2D g2) {
        for (game.effects.Particle p : particles) {
            float alpha = p.getAlpha();
            Color fogColor = new Color((p.colorRGB >> 16) & 0xFF,
                    (p.colorRGB >> 8) & 0xFF,
                    p.colorRGB & 0xFF,
                    (int)(alpha * 255));
            g2.setColor(fogColor);
            g2.fillOval((int)p.x, (int)p.y, (int)p.size, (int)p.size);
        }
    }
}
