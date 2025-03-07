package game.objects;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

/**
 * Representa un objeto coleccionable en el juego, como una sandía o una llave.
 */
public class Collectible {
    /**
     * Tipos de coleccionable.
     */
    public enum Type {
        SANDIA, LLAVE
    }

    private int x;
    private int baseY; // posición inicial en Y para el efecto de oscilación
    private int width, height;
    private Type type;
    private boolean collected;
    private Image image;

    // Variables para el efecto de oscilación (ahora usando float para movimientos más precisos)
    private int oscillationRange = 10; // Amplitud de oscilación en píxeles
    private float oscillationSpeed = 0.5f;  // Velocidad de oscilación en píxeles por actualización (más lenta)
    private float currentOffset = 0f;
    private int direction = 1; // 1 = hacia abajo, -1 = hacia arriba

    /**
     * Constructor para un objeto coleccionable.
     * @param type Tipo del objeto (SANDIA o LLAVE).
     * @param x Coordenada X.
     * @param y Coordenada Y.
     * @param width Ancho del objeto.
     * @param height Alto del objeto.
     * @param imagePath Ruta del recurso de imagen (por ejemplo, "/resources/imagen/sandia.png").
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
     * Actualiza la posición vertical para generar un efecto de oscilación.
     */
    public void update() {
        if (!collected) {
            currentOffset += oscillationSpeed * direction;
            if (currentOffset > oscillationRange || currentOffset < -oscillationRange) {
                direction *= -1;
            }
            // Se asigna la posición actualizada redondeando el offset
            // para mantener la propiedad de entero en la posición y
            // (puedes adaptar según sea necesario)
        }
    }

    /**
     * Dibuja el objeto coleccionable en el gráfico.
     * @param g Objeto Graphics donde se dibuja.
     */
    public void draw(Graphics g) {
        if (!collected && image != null) {
            int yPos = baseY + Math.round(currentOffset);
            g.drawImage(image, x, yPos, width, height, null);
        }
    }

    /**
     * Devuelve el rectángulo que representa el área de colisión del objeto.
     * @return Rectángulo con posición y tamaño.
     */
    public Rectangle getBounds() {
        int yPos = baseY + Math.round(currentOffset);
        return new Rectangle(x, yPos, width, height);
    }

    /**
     * Marca el objeto como recogido.
     * @param collected true si se ha recogido, false en caso contrario.
     */
    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    /**
     * Indica si el objeto ya ha sido recogido.
     * @return true si ya fue recogido.
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Devuelve el tipo del objeto.
     * @return Tipo del objeto.
     */
    public Type getType() {
        return type;
    }
}
