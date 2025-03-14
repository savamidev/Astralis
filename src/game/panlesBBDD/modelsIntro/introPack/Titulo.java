package game.panlesBBDD.modelsIntro.introPack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Titulo extends JPanel {
    private Image image;
    private float alpha = 0f;

    public Titulo() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        // Especificamos un tamaño preferido para que se muestre correctamente
        setPreferredSize(new Dimension(800, 200));

        // Cargar la imagen del título.
        ImageIcon icon = new ImageIcon("src/resources/imagen/tituloInicial.png");
        image = icon.getImage();

        // Timer para el efecto de fade‑in.
        Timer fadeInTimer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                alpha += 0.02f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    ((Timer)e.getSource()).stop();
                }
                repaint();
            }
        });
        fadeInTimer.setInitialDelay(3600); // Espera 2 segundos antes de iniciar el fade‑in.
        fadeInTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            int x = (getWidth() - image.getWidth(this)) / 2;
            // Se agrega un offset de 20 píxeles para bajar el título.
            int y = (getHeight() - image.getHeight(this)) / 2 + 20;
            g2d.drawImage(image, x, y, this);
            g2d.dispose();
        }
    }
}
