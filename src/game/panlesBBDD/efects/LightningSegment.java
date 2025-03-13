package game.effects;

import java.awt.Point;

/**
 * Representa un segmento del rayo, definido por dos puntos y un grosor variable.
 * Cada segmento es la unidad básica que, al combinarse, forma una rama completa del rayo.
 */
public class LightningSegment {
    public Point p1;
    public Point p2;
    public float thickness;

    /**
     * Crea un nuevo segmento de rayo.
     *
     * @param p1        Punto inicial del segmento.
     * @param p2        Punto final del segmento.
     * @param thickness Grosor del segmento.
     */
    public LightningSegment(Point p1, Point p2, float thickness) {
        this.p1 = p1;
        this.p2 = p2;
        this.thickness = thickness;
    }
}
