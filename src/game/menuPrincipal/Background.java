package game.menuPrincipal;

import javax.swing.*;
import java.awt.*;

public class Background extends JPanel {
    private ImageIcon imagen;

    public Background() {
        cargarImagen("src/resources/background GIF.gif");
    }

    private void cargarImagen(String rootImage) {
        imagen = new ImageIcon(rootImage);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagen != null) {
            g.drawImage(imagen.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
