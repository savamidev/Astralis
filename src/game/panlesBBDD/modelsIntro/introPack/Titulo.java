package game.panlesBBDD.modelsIntro.introPack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Representa la pantalla de título con un efecto de fade-in y fade-out.
 * <p>
 * Una vez finalizada la animación, se transita al menú principal.
 * </p>
 */
public class Titulo extends JPanel {
    private final CardLayout innerLayout;   // Layout interno para gestionar la transición al menú.
    private final JPanel introContainer;    // Contenedor principal de la introducción.
    private Image image;                    // Imagen del título.
    private float alpha = 0f;               // Nivel de opacidad para el efecto de fade.

    /**
     * Crea una instancia de Titulo.
     *
     * @param innerLayout    CardLayout interno para la transición.
     * @param introContainer Contenedor que maneja los paneles de la introducción.
     */
    public Titulo(CardLayout innerLayout, JPanel introContainer) {
        this.innerLayout = innerLayout;
        this.introContainer = introContainer;
        setOpaque(false);
        setLayout(new GridBagLayout()); // Centra la imagen en el panel.

        // Carga la imagen del título desde el recurso.
        ImageIcon icon = new ImageIcon("src/resources/imagen/tituloInicial.png");
        image = icon.getImage();

        // Timer para la animación de fade-in, pausa y fade-out.
        Timer fadeInTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.02f; // Incrementa la opacidad gradualmente.
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
                                    alpha -= 0.05f; // Decrementa la opacidad.
                                    if(alpha <= 0f) {
                                        alpha = 0f;
                                        ((Timer)e.getSource()).stop(); // Detiene el fade-out.
                                        // Al finalizar, muestra el menú principal.
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
        fadeInTimer.setInitialDelay(2000); // Retardo inicial antes del fade-in.
        fadeInTimer.start();
    }

    /**
     * Dibuja la imagen del título aplicando el efecto de fade.
     *
     * @param g Objeto Graphics utilizado para el dibujo.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(image != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            // Centra la imagen horizontalmente y la posiciona verticalmente.
            int x = (getWidth() - image.getWidth(this)) / 2;
            int y = (getHeight() - image.getHeight(this)) / 2;
            g2d.drawImage(image, x, 200, this);
            g2d.dispose();
        }
    }
}
