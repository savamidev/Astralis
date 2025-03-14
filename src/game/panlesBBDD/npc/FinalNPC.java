package game.objects;

import java.awt.*;
import javax.swing.ImageIcon;
import java.net.URL;

/**
 * FinalNPC representa al NPC final que, al interactuar (o colisionar) con el jugador,
 * dispara la transición final (por ejemplo, reproduciendo el video final del juego).
 */
public class FinalNPC {
    private int x, y;
    private int width, height;
    private Image image;

    /**
     * Crea una instancia de FinalNPC.
     * @param x La posición X.
     * @param y La posición Y.
     * @param width Ancho del NPC.
     * @param height Alto del NPC.
     * @param imagePath Ruta del recurso de imagen.
     */
    public FinalNPC(int x, int y, int width, int height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        try {
            URL url = getClass().getResource(imagePath);
            if (url != null) {
                image = new ImageIcon(url).getImage();
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen del NPC final: " + e.getMessage());
            image = null;
        }
    }

    /**
     * Dibuja al NPC.
     * @param g Objeto Graphics para el dibujo.
     */
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, width, height);
        }
    }

    /**
     * Retorna el área de colisión del NPC.
     * @return Un Rectangle representando su hitbox.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
