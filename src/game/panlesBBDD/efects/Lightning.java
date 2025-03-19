package game.effects;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Genera y gestiona el efecto visual de un rayo, compuesto por una rama principal y ramas laterales.
 * El efecto se limita a una zona virtual definida y tiene una duración especificada.
 */
public class Lightning {
    private List<game.effects.LightningBranch> branches;
    private long startTime;
    private long duration;
    private int virtualWidth;  // Ancho de la zona de efecto
    private int virtualHeight; // Alto de la zona de efecto
    private Random rand = new Random();
    private int offsetX; // Posición X en el mundo para el efecto (tras transformación)
    private int offsetY; // Posición Y en el mundo para el efecto

    /**
     * Construye un nuevo efecto de rayo dentro de una zona específica.
     *
     * @param duration   Duración del efecto en milisegundos.
     * @param offsetX    Coordenada X de la zona en el mundo.
     * @param offsetY    Coordenada Y de la zona en el mundo.
     * @param zoneWidth  Ancho de la zona en la que se genera el efecto.
     * @param zoneHeight Alto de la zona en la que se genera el efecto.
     */
    public Lightning(long duration, int offsetX, int offsetY, int zoneWidth, int zoneHeight) {
        this.duration = duration;
        this.virtualWidth = zoneWidth;
        this.virtualHeight = zoneHeight;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.startTime = System.currentTimeMillis();
        this.branches = generateBranches();
    }

    /**
     * Genera la rama principal y las ramas laterales del rayo.
     * La rama principal se compone de segmentos lineales con leves perturbaciones,
     * y las ramas laterales se generan aleatoriamente a partir de puntos intermedios.
     *
     * @return Lista de ramas que conforman el rayo.
     */
    private List<game.effects.LightningBranch> generateBranches() {
        List<game.effects.LightningBranch> branchList = new ArrayList<>();
        List<game.effects.LightningSegment> mainSegments = new ArrayList<>();
        int steps = 20;  // Número de pasos para la rama principal
        // Punto de inicio: posición aleatoria en X dentro de la zona, y Y = 0 (parte superior)
        int startX = rand.nextInt(virtualWidth);
        Point prevPoint = new Point(startX, 0);
        for (int i = 1; i <= steps; i++) {
            float t = (float) i / steps;
            // Punto ideal en línea recta de arriba a abajo
            int idealY = (int) (t * virtualHeight);
            // Perturbar la posición ideal con variación aleatoria
            int perturbX = prevPoint.x + rand.nextInt(21) - 10;
            int perturbY = idealY + rand.nextInt(21) - 10;
            // Asegurar que el punto esté dentro de la zona
            perturbX = Math.max(0, Math.min(virtualWidth, perturbX));
            perturbY = Math.max(0, Math.min(virtualHeight, perturbY));
            Point currentPoint = new Point(perturbX, perturbY);
            // Grosor que disminuye linealmente: de 10 a 2 píxeles
            float thickness = 10 * (1 - t) + 2;
            mainSegments.add(new game.effects.LightningSegment(prevPoint, currentPoint, thickness));
            prevPoint = currentPoint;
        }
        branchList.add(new game.effects.LightningBranch(mainSegments));

        // Generar ramas laterales con mayor probabilidad para aumentar el detalle
        for (int i = 3; i < steps - 3; i++) {
            if (rand.nextFloat() < 0.65f) { // Aumentado de 0.35f a 0.65f
                List<game.effects.LightningSegment> branchSegments = new ArrayList<>();
                Point branchStart = mainSegments.get(i).p1;
                int branchSteps = 5; // Número de pasos para la rama lateral
                Point branchPrev = branchStart;
                for (int j = 1; j <= branchSteps; j++) {
                    float t = (float) j / branchSteps;
                    int deltaX = rand.nextInt(31) - 15; // Variación ±15 píxeles en X
                    int deltaY = rand.nextInt(21) + 10;   // Entre 10 y 30 píxeles en Y
                    int newX = branchPrev.x + deltaX;
                    int newY = branchPrev.y + deltaY;
                    newX = Math.max(0, Math.min(virtualWidth, newX));
                    newY = Math.max(0, Math.min(virtualHeight, newY));
                    Point branchPoint = new Point(newX, newY);
                    // Grosor que disminuye de 6 a 1 en la rama lateral
                    float branchThickness = 6 * (1 - t) + 1;
                    branchSegments.add(new game.effects.LightningSegment(branchPrev, branchPoint, branchThickness));
                    branchPrev = branchPoint;
                }
                branchList.add(new game.effects.LightningBranch(branchSegments));
            }
        }
        return branchList;
    }

    /**
     * Dibuja el efecto de rayo trasladando el contexto gráfico a la posición definida
     * y renderizando todas las ramas del rayo.
     *
     * @param g Objeto Graphics2D utilizado para el renderizado.
     */
    public void draw(Graphics2D g) {
        AffineTransform old = g.getTransform();
        g.translate(offsetX, offsetY);
        for (game.effects.LightningBranch branch : branches) {
            branch.draw(g);
        }
        g.setTransform(old);
    }

    /**
     * Indica si el efecto de rayo aún se encuentra activo en función de su duración.
     *
     * @return {@code true} si el efecto sigue activo; {@code false} si ha finalizado.
     */
    public boolean isActive() {
        return System.currentTimeMillis() - startTime < duration;
    }
}
