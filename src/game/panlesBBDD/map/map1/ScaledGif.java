package game.panlesBBDD.map.map1;

import javax.swing.*;
import java.awt.*;

public class ScaledGif extends JPanel {
    private ImageIcon gifIcon;

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
