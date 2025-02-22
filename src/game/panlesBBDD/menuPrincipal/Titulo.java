package game.panlesBBDD.menuPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Titulo extends JPanel {
    private final CardLayout innerLayout;
    private final JPanel introContainer;
    private Image image;
    private float alpha = 0f;

    public Titulo(CardLayout innerLayout, JPanel introContainer) {
        this.innerLayout = innerLayout;
        this.introContainer = introContainer;
        setOpaque(false);
        setLayout(new GridBagLayout());

        ImageIcon icon = new ImageIcon("src/resources/titulo.png");
        image = icon.getImage();

        // Timer para animación: fade-in, pausa y fade-out
        Timer fadeInTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.02f;
                if(alpha >= 1f) {
                    alpha = 1f;
                    ((Timer)e.getSource()).stop();
                    // Esperar 3500 ms antes de iniciar el fade-out
                    new Timer(3500, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Timer fadeOutTimer = new Timer(50, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    alpha -= 0.05f;
                                    if(alpha <= 0f) {
                                        alpha = 0f;
                                        ((Timer)e.getSource()).stop();
                                        // Al terminar, se muestra el menú de botones
                                        innerLayout.show(introContainer, "MenuPanel");
                                    }
                                    repaint();
                                }
                            });
                            fadeOutTimer.start();
                        }
                    }).start();
                }
                repaint();
            }
        });
        fadeInTimer.setInitialDelay(2000);
        fadeInTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(image != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            int x = (getWidth() - image.getWidth(this)) / 2;
            int y = (getHeight() - image.getHeight(this)) / 2;
            g2d.drawImage(image, x, y, this);
            g2d.dispose();
        }
    }
}
