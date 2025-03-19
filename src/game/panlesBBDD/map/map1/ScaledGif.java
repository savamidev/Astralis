package game.panlesBBDD.map.map1;

import javax.swing.*;
import java.awt.*;

/**
 * Panel especializado para mostrar un GIF escalado al tamaño del panel.
 * Carga el recurso gráfico especificado y lo dibuja ajustándolo a las dimensiones actuales.
 */
public class ScaledGif extends JPanel {
    private ImageIcon gifIcon;

    /**
     * Crea un panel que muestra el GIF ubicado en la ruta especificada.
     *
     * @param resourcePath Ruta del recurso GIF.
     */
    public ScaledGif(String resourcePath) {
        // Carga el gif desde la ruta especificada
        gifIcon = new ImageIcon(getClass().getResource(resourcePath));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Dibuja la imagen escalada al tamaño actual del panel
        g.drawImage(gifIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}
