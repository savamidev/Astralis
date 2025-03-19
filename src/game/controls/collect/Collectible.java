package game.objects;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

/**
 * Representa un objeto coleccionable en el juego que el jugador puede recolectar,
 * como una sandía, una llave o unas botas. Su posición vertical oscila para proporcionar
 * un efecto visual dinámico y atractivo.
 *
 * @author
 */
public class Collectible {

    /**
     * Define los tipos disponibles para los objetos coleccionables.
     */
    public enum Type {
        SANDIA,
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
     * Construye un nuevo objeto coleccionable con las características y recurso de imagen especificados.
     *
     * @param type      Tipo del coleccionable (ej. SANDIA, BOTAS).
     * @param x         Coordenada X donde se posicionará el objeto.
     * @param y         Coordenada base en Y para la posición del objeto.
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
     * Actualiza el estado del objeto aplicando un efecto de oscilación vertical.
     * El método modifica el offset vertical para simular un movimiento de vaivén.
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
     * Dibuja el objeto coleccionable en el contexto gráfico especificado.
     * Solo se dibuja si el objeto no ha sido recolectado y la imagen se cargó correctamente.
     *
     * @param g Objeto Graphics utilizado para el renderizado.
     */
    public void draw(Graphics g) {
        if (!collected && image != null) {
            int yPos = baseY + Math.round(currentOffset);
            g.drawImage(image, x, yPos, width, height, null);
        }
    }

    /**
     * Retorna el área de colisión del objeto, considerando el efecto de oscilación.
     *
     * @return Un objeto Rectangle que define los límites de colisión.
     */
    public Rectangle getBounds() {
        int yPos = baseY + Math.round(currentOffset);
        return new Rectangle(x, yPos, width, height);
    }

    /**
     * Define el estado de recogida del objeto.
     *
     * @param collected {@code true} si el objeto ha sido recolectado, {@code false} en caso contrario.
     */
    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    /**
     * Indica si el objeto ya ha sido recolectado.
     *
     * @return {@code true} si el objeto fue recolectado, {@code false} de lo contrario.
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Obtiene el tipo del objeto coleccionable.
     *
     * @return Valor de {@link Type} que representa el tipo del objeto.
     */
    public Type getType() {
        return type;
    }
}
