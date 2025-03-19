package game.effects;

import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.List;

/**
 * Representa una rama del rayo compuesta por múltiples segmentos.
 * Cada rama se dibuja trazando cada segmento con un grosor variable para simular
 * la apariencia irregular de un rayo.
 */
public class LightningBranch {
    private List<game.effects.LightningSegment> segments;

    /**
     * Crea una instancia de LightningBranch a partir de una lista de segmentos.
     *
     * @param segments Lista de {@link LightningSegment} que componen la rama.
     */
    public LightningBranch(List<game.effects.LightningSegment> segments) {
        this.segments = segments;
    }

    /**
     * Dibuja la rama del rayo sobre el contexto gráfico especificado, utilizando
     * un trazo de grosor variable para cada segmento.
     *
     * @param g Objeto Graphics2D sobre el cual se dibuja la rama.
     */
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE); // Color del rayo
        for (game.effects.LightningSegment segment : segments) {
            g.setStroke(new BasicStroke(segment.thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(segment.p1.x, segment.p1.y, segment.p2.x, segment.p2.y);
        }
    }
}
