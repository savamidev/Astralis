package game.objects;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

/**
 * Representa un objeto coleccionable en el juego, como una sandía o una llave.
 * <p>
 * Cada objeto coleccionable posee una posición, dimensiones, un tipo y un efecto de
 * oscilación vertical para dar una animación atractiva. Además, puede ser recogido
 * por el jugador, cambiando su estado.
 * </p>
 *
 * @author
 */
public class Collectible {

    /**
     * Enumeración que define los tipos de coleccionable.
     */
    public enum Type {
        /** Representa una sandía. */
        SANDIA,
        /** Representa una llave. */
        LLAVE
    }

    // Posición en X del objeto.
    private int x;
    // Posición base en Y del objeto para el efecto de oscilación.
    private int baseY;
    // Dimensiones del objeto.
    private int width, height;
    // Tipo de coleccionable.
    private Type type;
    // Indica si el objeto ha sido recogido.
    private boolean collected;
    // Imagen que representa el objeto.
    private Image image;

    // Variables para el efecto de oscilación:

    /** Amplitud máxima de oscilación en píxeles. */
    private int oscillationRange = 10;
    /** Velocidad de oscilación en píxeles por actualización. */
    private float oscillationSpeed = 0.5f;
    /** Desplazamiento actual acumulado para la oscilación (se redondea para calcular la posición final). */
    private float currentOffset = 0f;
    /** Dirección del movimiento vertical: 1 para hacia abajo, -1 para hacia arriba. */
    private int direction = 1;

    /**
     * Crea un nuevo objeto coleccionable con las características especificadas.
     *
     * @param type      Tipo del objeto (puede ser {@link Collectible.Type#SANDIA} o {@link Collectible.Type#LLAVE}).
     * @param x         Coordenada X en la que se posicionará el objeto.
     * @param y         Coordenada base en Y en la que se posicionará el objeto.
     * @param width     Ancho del objeto.
     * @param height    Alto del objeto.
     * @param imagePath Ruta del recurso de imagen que representa el objeto
     *                  (por ejemplo, "/resources/imagen/sandia.png").
     */
    public Collectible(Type type, int x, int y, int width, int height, String imagePath) {
        this.type = type;
        this.x = x;
        this.baseY = y;
        this.width = width;
        this.height = height;
        this.collected = false;
        try {
            // Intenta cargar la imagen del recurso especificado.
            image = new ImageIcon(getClass().getResource(imagePath)).getImage();
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen de " + type + ": " + e.getMessage());
            image = null;
        }
    }

    /**
     * Actualiza la posición vertical del objeto para generar un efecto de oscilación.
     * <p>
     * Este método incrementa el desplazamiento vertical acumulado según la velocidad
     * y la dirección. Cuando se alcanza el rango máximo definido, se invierte la dirección
     * para simular un movimiento oscilatorio.
     * </p>
     */
    public void update() {
        if (!collected) {
            currentOffset += oscillationSpeed * direction;
            if (currentOffset > oscillationRange || currentOffset < -oscillationRange) {
                direction *= -1;
            }
            // Se redondea el offset para mantener la coherencia con coordenadas enteras.
        }
    }

    /**
     * Dibuja el objeto coleccionable en el componente gráfico especificado.
     *
     * @param g Objeto {@link Graphics} en el cual se dibuja el objeto.
     */
    public void draw(Graphics g) {
        if (!collected && image != null) {
            int yPos = baseY + Math.round(currentOffset);
            g.drawImage(image, x, yPos, width, height, null);
        }
    }

    /**
     * Obtiene el área rectangular que delimita el objeto coleccionable.
     * <p>
     * Este rectángulo se utiliza para la detección de colisiones.
     * </p>
     *
     * @return Un objeto {@link Rectangle} que representa la posición y dimensiones del objeto.
     */
    public Rectangle getBounds() {
        int yPos = baseY + Math.round(currentOffset);
        return new Rectangle(x, yPos, width, height);
    }

    /**
     * Marca el objeto como recogido o lo restaura a su estado inicial.
     *
     * @param collected {@code true} si se ha recogido el objeto; {@code false} en caso contrario.
     */
    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    /**
     * Indica si el objeto ya ha sido recogido.
     *
     * @return {@code true} si el objeto ya fue recogido; de lo contrario, {@code false}.
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Devuelve el tipo de objeto coleccionable.
     *
     * @return El tipo del objeto, ya sea {@link Collectible.Type#SANDIA} o {@link Collectible.Type#LLAVE}.
     */
    public Type getType() {
        return type;
    }
}
