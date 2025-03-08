package game.objects;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import java.awt.Image;

public class PortalNPC {
    private int x, y;
    private int width, height;
    private Image gifImage;

    // Control del mensaje del NPC
    private boolean showMessage = false;
    private long messageStartTime = 0;
    private final long MESSAGE_DURATION = 5000; // 5 segundos

    public PortalNPC(int x, int y, int width, int height, String gifPath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        try {
            // Cargar el gif; ImageIcon gestiona la animación
            gifImage = new ImageIcon(getClass().getResource(gifPath)).getImage();
        } catch (Exception e) {
            System.err.println("Error al cargar el gif del NPC: " + e.getMessage());
            gifImage = null;
        }
    }

    /**
     * Llama a este método para activar la visualización del mensaje.
     * El mensaje se mostrará durante MESSAGE_DURATION (5s).
     */
    public void triggerMessage() {
        showMessage = true;
        messageStartTime = System.currentTimeMillis();
    }

    /**
     * Permite saber si el mensaje aún está activo.
     */
    public boolean isMessageActive() {
        if (showMessage) {
            long elapsed = System.currentTimeMillis() - messageStartTime;
            if (elapsed < MESSAGE_DURATION) {
                return true;
            } else {
                // Expiró el tiempo, se desactiva
                showMessage = false;
            }
        }
        return false;
    }

    /**
     * Retorna el tiempo transcurrido desde que se activó el mensaje.
     */
    public long getMessageElapsedTime() {
        return System.currentTimeMillis() - messageStartTime;
    }

    /**
     * Dibuja el NPC y, si showMessage es true, dibuja un recuadro con el mensaje.
     * El mensaje varía según si el jugador posee la llave.
     *
     * @param g            Objeto Graphics para el dibujo.
     * @param playerHasKey true si el jugador tiene la llave.
     */
    public void draw(Graphics g, boolean playerHasKey) {
        // Dibujar la imagen del NPC
        if (gifImage != null) {
            g.drawImage(gifImage, x, y, width, height, null);
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(x, y, width, height);
        }

        // Solo se dibuja el mensaje si está activado y no ha expirado
        if (isMessageActive()) {
            String message;
            if (playerHasKey) {
                message = "¡Ya puedes pasar a la siguiente mision!";
            } else {
                message = "No puedo dejarte pasar, aun no has encontrado la llave, recorda buscarla y entregarmela";
            }
            // Definir dimensiones y posición del recuadro (por encima del NPC)
            int boxWidth = 300;
            int boxHeight = 40;
            int boxX = x + (width - boxWidth) / 2;
            int boxY = y - boxHeight - 10;

            // Fondo semitransparente
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);

            // Dibujar el texto centrado en el recuadro
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(message);
            int textX = boxX + (boxWidth - textWidth) / 2;
            int textY = boxY + ((boxHeight - fm.getHeight()) / 2) + fm.getAscent();
            g.drawString(message, textX, textY);
        }
    }

    /**
     * Retorna el área de colisión del NPC.
     *
     * @return Un objeto Rectangle que representa su hitbox.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
