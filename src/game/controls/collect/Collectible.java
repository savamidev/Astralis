package game.objects;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

/**
 * Representa un objeto coleccionable en el juego, como una sandía, una llave o unas botas.
 */
public class Collectible {

    /**
     * Enumeración que define los tipos de coleccionable.
     */
    public enum Type {
        SANDIA,
        LLAVE,
        BOTAS  // Nuevo tipo de coleccionable
    }

    private int x;
    private int baseY;
    private int width, height;
    private Type type;
    private boolean collected;
    private Image image;

    // Variables para el efecto de oscilación:
    private int oscillationRange = 10;
    private float oscillationSpeed = 0.5f;
    private float currentOffset = 0f;
    private int direction = 1;

    /**
     * Crea un nuevo objeto coleccionable con las características especificadas.
     *
     * @param type      Tipo del objeto (SANDIA, LLAVE o BOTAS).
     * @param x         Coordenada X en la que se posicionará el objeto.
     * @param y         Coordenada base en Y en la que se posicionará el objeto.
     * @param width     Ancho del objeto.
     * @param height    Alto del objeto.
     * @param imagePath Ruta del recurso de imagen.
     */
    public Collectible(Type type, int x, int y, int width, int height, String imagePath) {
        this.type = type;
        this.x = x;
        this.baseY = y;
        this.width = width;
        this.height = height;
        this.collected = false;
        try {
            image = new ImageIcon(getClass().getResource(imagePath)).getImage();
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen de " + type + ": " + e.getMessage());
            image = null;
        }
    }

    /**
     * Actualiza la posición vertical para crear un efecto de oscilación.
     */
    public void update() {
        if (!collected) {
            currentOffset += oscillationSpeed * direction;
            if (currentOffset > oscillationRange || currentOffset < -oscillationRange) {
                direction *= -1;
            }
        }
    }

    /**
     * Dibuja el coleccionable en el componente gráfico.
     */
    public void draw(Graphics g) {
        if (!collected && image != null) {
            int yPos = baseY + Math.round(currentOffset);
            g.drawImage(image, x, yPos, width, height, null);
        }
    }

    /**
     * Retorna el área de colisión del objeto.
     */
    public Rectangle getBounds() {
        int yPos = baseY + Math.round(currentOffset);
        return new Rectangle(x, yPos, width, height);
    }

    /**
     * Establece el estado recogido del objeto.
     */
    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    /**
     * Indica si el objeto ya fue recogido.
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Retorna el tipo de coleccionable.
     */
    public Type getType() {
        return type;
    }
}
