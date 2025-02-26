package game.panlesBBDD.modelsIntro.introPack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase Titulo
 *
 * Esta clase representa la pantalla de título con un efecto de fade-in y fade-out.
 * Una vez finalizada la animación, se muestra el menú principal.
 */
public class Titulo extends JPanel {
    private final CardLayout innerLayout;   // Layout interno para gestionar la transición al menú.
    private final JPanel introContainer;    // Contenedor principal de la introducción.
    private Image image;                     // Imagen del título.
    private float alpha = 0f;                // Nivel de opacidad para el efecto de fade.

    /**
     * Constructor de Titulo.
     *
     * @param innerLayout    CardLayout interno para la transición entre el título y el menú.
     * @param introContainer Contenedor que maneja los paneles de la introducción.
     */
    public Titulo(CardLayout innerLayout, JPanel introContainer) {
        this.innerLayout = innerLayout;
        this.introContainer = introContainer;
        setOpaque(false); // Hace que el fondo sea transparente.
        setLayout(new GridBagLayout()); // Se usa para centrar la imagen.

        // Carga la imagen del título.
        ImageIcon icon = new ImageIcon("src/resources/titulo.png");
        image = icon.getImage();

        // Timer para animación: fade-in, pausa y fade-out
        Timer fadeInTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.02f; // Aumenta la opacidad gradualmente.
                if(alpha >= 1f) {
                    alpha = 1f;
                    ((Timer)e.getSource()).stop(); // Detiene el fade-in.

                    // Espera 3500 ms antes de iniciar el fade-out.
                    new Timer(3500, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Timer para el efecto de fade-out.
                            Timer fadeOutTimer = new Timer(50, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    alpha -= 0.05f; // Reduce la opacidad gradualmente.
                                    if(alpha <= 0f) {
                                        alpha = 0f;
                                        ((Timer)e.getSource()).stop(); // Detiene el fade-out.
                                        // Al finalizar la animación, se muestra el menú.
                                        innerLayout.show(introContainer, "MenuPanel");
                                    }
                                    repaint(); // Vuelve a dibujar el panel con la nueva opacidad.
                                }
                            });
                            fadeOutTimer.start();
                        }
                    }).start();
                }
                repaint();
            }
        });
        fadeInTimer.setInitialDelay(2000); // Espera inicial antes de iniciar el fade-in.
        fadeInTimer.start();
    }

    /**
     * Método para dibujar la imagen con el efecto de fade-in y fade-out.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(image != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)); // Aplica la opacidad.
            int x = (getWidth() - image.getWidth(this)) / 2;
            int y = (getHeight() - image.getHeight(this)) / 2;
            g2d.drawImage(image, x, y, this); // Dibuja la imagen centrada.
            g2d.dispose();
        }
    }
}
