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

    // Conversación: Iris tiene 2 mensajes y Viajero 2 mensajes.
    private final String[] conversation = {
            "Iris: ¡Hola, viajero! Mi hermana se ha perdido, ¿has visto a una chica parecida a mí?",
            "Viajero: ¡Vaya, lo siento mucho! Escuché en la ciudad que la vieron yendo hacia la Cueva del Susurro.",
            "Iris: De acuerdo, ¿sabrías guiarme?",
            "Viajero: Sí, te dejo mi mapa y podrás guiarte hasta la cueva."
    };
    // Cada mensaje se muestra durante 3000 ms (3 segundos)
    private final long MESSAGE_PER_MESSAGE = 5000;

    // Control del mensaje del NPC
    private boolean showMessage = false;
    private long messageStartTime = 0;
    // La duración total de la conversación se calcula a partir del número de mensajes
    private final long TOTAL_MESSAGE_DURATION = conversation.length * MESSAGE_PER_MESSAGE;

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
     * Activa la visualización del mensaje (solo si aún no se ha activado).
     */
    public void triggerMessage() {
        if (!showMessage) {
            showMessage = true;
            messageStartTime = System.currentTimeMillis();
        }
    }

    /**
     * Indica si la conversación aún está activa.
     */
    public boolean isMessageActive() {
        if (showMessage) {
            long elapsed = System.currentTimeMillis() - messageStartTime;
            if (elapsed < TOTAL_MESSAGE_DURATION) {
                return true;
            } else {
                showMessage = false;
            }
        }
        return false;
    }

    /**
     * Dibuja el NPC y, si la conversación está activa, muestra el mensaje actual.
     *
     * @param g Objeto Graphics para el dibujo.
     */
    public void draw(Graphics g) {
        // Dibujar la imagen del NPC
        if (gifImage != null) {
            g.drawImage(gifImage, x, y, width, height, null);
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(x, y, width, height);
        }

        // Dibujar el mensaje de conversación si está activo
        if (isMessageActive()) {
            long elapsed = System.currentTimeMillis() - messageStartTime;
            // Calcular qué mensaje se debe mostrar
            int index = (int)(elapsed / MESSAGE_PER_MESSAGE);
            if(index >= conversation.length) {
                index = conversation.length - 1;
            }
            String message = conversation[index];

            int maxWidth = 280;
            Font font = g.getFont();
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            String[] lines = wrapText(message, fm, maxWidth);
            int boxWidth = 0;
            for (String line : lines) {
                int lineWidth = fm.stringWidth(line);
                if (lineWidth > boxWidth) {
                    boxWidth = lineWidth;
                }
            }
            boxWidth += 30; // padding horizontal
            int lineHeight = fm.getHeight();
            int boxHeight = lines.length * lineHeight + 20; // padding vertical

            // Posicionar el recuadro centrado sobre el NPC (por encima de él)
            int boxX = x + (width - boxWidth) / 2;
            int boxY = y - boxHeight - 10;

            // Dibujar fondo del recuadro
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);

            // Dibujar el texto centrado
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
