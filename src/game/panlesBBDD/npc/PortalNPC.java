package game.objects;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
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
     * Activa la visualización del mensaje.
     */
    public void triggerMessage() {
        showMessage = true;
        messageStartTime = System.currentTimeMillis();
    }

    /**
     * Devuelve si el mensaje aún está activo.
     */
    public boolean isMessageActive() {
        if (showMessage) {
            long elapsed = System.currentTimeMillis() - messageStartTime;
            if (elapsed < MESSAGE_DURATION) {
                return true;
            } else {
                showMessage = false;
            }
        }
        return false;
    }

    /**
     * Dibuja el NPC y, si el mensaje está activo, dibuja un recuadro con el mensaje.
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

        // Dibujar el mensaje si está activo
        if (isMessageActive()) {
            String message;
            if (playerHasKey) {
                message = "¡Ya puedes pasar a la siguiente misión!";
            } else {
                message = "No puedo dejarte pasar, aun no has encontrado la llave, "
                        + "recuerda buscarla y entregármela";
            }
            // Definir un ancho máximo para el recuadro
            int maxWidth = 280;
            Font font = g.getFont();
            // Si no se quiere usar la fuente actual, se puede definir una específica:
            // font = new Font("Arial", Font.BOLD, 14);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            // Dividir el mensaje en líneas usando el método wrapText
            String[] lines = wrapText(message, fm, maxWidth);
            // Calcular el ancho máximo real y el alto total del recuadro
            int boxWidth = 0;
            for (String line : lines) {
                int lineWidth = fm.stringWidth(line);
                if (lineWidth > boxWidth) {
                    boxWidth = lineWidth;
                }
            }
            boxWidth += 20; // padding horizontal
            int lineHeight = fm.getHeight();
            int boxHeight = lines.length * lineHeight + 10; // padding vertical

            // Posicionar el recuadro centrado horizontalmente sobre el NPC, por encima de él
            int boxX = x + (width - boxWidth) / 2;
            int boxY = y - boxHeight - 10;

            // Dibujar fondo del recuadro (con opacidad)
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);

            // Dibujar el texto centrado en cada línea
            g.setColor(Color.WHITE);
            int textY = boxY + fm.getAscent() + 5;
            for (String line : lines) {
                int textWidth = fm.stringWidth(line);
                int textX = boxX + (boxWidth - textWidth) / 2;
                g.drawString(line, textX, textY);
                textY += lineHeight;
            }
        }
    }

    /**
     * Método auxiliar para hacer word-wrap del texto.
     * Divide el texto en líneas de modo que cada línea no supere maxWidth píxeles.
     *
     * @param text     El texto a envolver.
     * @param fm       FontMetrics para medir el texto.
     * @param maxWidth El ancho máximo permitido en píxeles.
     * @return Un arreglo de Strings, cada uno representando una línea.
     */
    private String[] wrapText(String text, FontMetrics fm, int maxWidth) {
        String[] words = text.split(" ");
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            if (currentLine.length() == 0) {
                currentLine.append(word);
            } else {
                String testLine = currentLine.toString() + " " + word;
                if (fm.stringWidth(testLine) <= maxWidth) {
                    currentLine.append(" ").append(word);
                } else {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                }
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines.toArray(new String[0]);
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
