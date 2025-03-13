package game.effects;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gestiona la generación y dibujo del rayo completo, incluyendo:
 * - Una rama principal de gran tamaño con múltiples pasos y ramificaciones.
 * - Un efecto de flash creativo (3 destellos) sobre la pantalla.
 */
public class Lightning {
    private List<game.effects.LightningBranch> branches;
    private long startTime;
    private long duration;
    private int virtualWidth;
    private int virtualHeight;
    private int flashRepetitions = 3; // 3 destellos
    private Random rand = new Random();

    public Lightning(long duration, int virtualWidth, int virtualHeight) {
        this.duration = duration;
        this.virtualWidth = virtualWidth;
        this.virtualHeight = virtualHeight;
        this.startTime = System.currentTimeMillis();
        this.branches = generateBranches();
    }

    /**
     * Genera la rama principal y algunas ramificaciones con segmentos de grosor variable.
     * Se incrementa el número de pasos y se amplían los rangos de desplazamiento para que el rayo sea más grande y realista.
     */
    private List<game.effects.LightningBranch> generateBranches() {
        List<game.effects.LightningBranch> branchList = new ArrayList<>();
        // --- Rama principal ---
        List<game.effects.LightningSegment> mainSegments = new ArrayList<>();
        int startX = rand.nextInt(virtualWidth);
        int y = 0;
        Point prevPoint = new Point(startX, y);
        float initialThickness = 15.0f;  // mayor grosor inicial
        float finalThickness = 5.0f;     // grosor final
        int steps = 25;                // más pasos para un rayo más largo y realista
        for (int i = 1; i <= steps; i++) {
            int offsetX = rand.nextInt(81) - 40;  // rango -40 a 40
            int offsetY = 40 + rand.nextInt(41);    // rango 40 a 80
            int newX = Math.max(0, Math.min(virtualWidth, prevPoint.x + offsetX));
            int newY = Math.min(virtualHeight, prevPoint.y + offsetY);
            Point newPoint = new Point(newX, newY);
            // Interpolación lineal para variar el grosor a lo largo de la rama
            float thickness = initialThickness + (finalThickness - initialThickness) * ((float) i / steps);
            mainSegments.add(new game.effects.LightningSegment(prevPoint, newPoint, thickness));
            prevPoint = newPoint;
        }
        branchList.add(new game.effects.LightningBranch(mainSegments));

        // --- Ramificaciones ---
        int numBranches = 4 + rand.nextInt(3); // entre 4 y 6 ramificaciones
        for (int b = 0; b < numBranches; b++) {
            List<game.effects.LightningSegment> branchSegments = new ArrayList<>();
            int branchStartIndex = 2 + rand.nextInt(steps - 4); // evitar el inicio y el final
            Point branchStartPoint = mainSegments.get(branchStartIndex).p1;
            int branchLength = 5 + rand.nextInt(4); // ramificaciones de 5 a 8 segmentos
            float branchInitialThickness = mainSegments.get(branchStartIndex).thickness * 0.9f;
            float branchFinalThickness = branchInitialThickness * 0.5f;
            Point currentPoint = branchStartPoint;
            for (int i = 1; i <= branchLength; i++) {
                int offsetX = rand.nextInt(81) - 40;
                int offsetY = 40 + rand.nextInt(41);
                int newX = Math.max(0, Math.min(virtualWidth, currentPoint.x + offsetX));
                int newY = Math.min(virtualHeight, currentPoint.y + offsetY);
                Point newPoint = new Point(newX, newY);
                float thickness = branchInitialThickness + (branchFinalThickness - branchInitialThickness) * ((float) i / branchLength);
                branchSegments.add(new game.effects.LightningSegment(currentPoint, newPoint, thickness));
                currentPoint = newPoint;
            }
            branchList.add(new game.effects.LightningBranch(branchSegments));
        }
        return branchList;
    }

    /**
     * Calcula el nivel de opacidad para el flash creativo, generando 3 destellos durante la duración.
     */
    private float getFlashAlpha() {
        long elapsed = System.currentTimeMillis() - startTime;
        long period = duration / flashRepetitions;
        long mod = elapsed % period;
        if (mod < period / 2) {
            float progress = (float) mod / (period / 2);
            return 0.5f * (1 - progress);
        }
        return 0f;
    }

    /**
     * Dibuja las ramas del rayo y, encima, el efecto de flash (destellos).
     */
    public void draw(Graphics2D g) {
        for (game.effects.LightningBranch branch : branches) {
            branch.draw(g);
        }
        float alpha = getFlashAlpha();
        if (alpha > 0) {
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g.setComposite(ac);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, virtualWidth, virtualHeight);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    /**
     * Indica si el rayo sigue activo según la duración definida.
     */
    public boolean isActive() {
        return System.currentTimeMillis() - startTime < duration;
    }
}
