package game.panlesBBDD.modelsIntro.introPack;

import javax.swing.*;
import java.awt.*;

/**
 * Representa el panel principal de la introducción del juego.
 * <p>
 * Este panel gestiona la reproducción del audio de introducción y la transición entre
 * la pantalla de título y el menú principal. Además, carga y muestra una imagen de fondo.
 * </p>
 */
public class IntroMother extends JPanel {
    private Image backgroundImage; // Imagen de fondo para la introducción.
    private IntroAudio introAudio; // Controlador para el audio de introducción.

    // Referencias para la transición a otros paneles.
    private CardLayout outerLayout; // Layout externo para cambiar de paneles.
    private JPanel outerContainer;  // Contenedor que gestiona los paneles de la aplicación.

    /**
     * Crea una instancia de IntroMother.
     *
     * @param outerLayout    CardLayout externo, usado para cambiar de panel en la aplicación.
     * @param outerContainer Contenedor principal de la aplicación.
     */
    public IntroMother(CardLayout outerLayout, JPanel outerContainer) {
        this.outerLayout = outerLayout;
        this.outerContainer = outerContainer;

        setLayout(new BorderLayout());

        // Carga la imagen de fondo desde los recursos.
        backgroundImage = new ImageIcon("src/resources/imagen/fondoInicio.gif").getImage();

        // Inicializa y reproduce el audio de introducción.
        introAudio = new IntroAudio("src/resources/sound/SoundIntro.wav");
        introAudio.start();

        // Configura un CardLayout interno para gestionar la transición entre el título y el menú.
        CardLayout innerLayout = new CardLayout();
        JPanel introContainer = new JPanel(innerLayout);
        introContainer.setOpaque(false);

        // Crea los subpaneles: la pantalla de título y el menú principal.
        Titulo titulo = new Titulo(innerLayout, introContainer);
        MenuPanel menuPanel = new MenuPanel(innerLayout, introContainer, introAudio, outerLayout, outerContainer);

        // Agrega los paneles al contenedor interno.
        introContainer.add(titulo, "Titulo");
        introContainer.add(menuPanel, "MenuPanel");

        // Muestra inicialmente el panel del título.
        innerLayout.show(introContainer, "Titulo");

        // Agrega el contenedor interno al panel principal.
        add(introContainer, BorderLayout.CENTER);
    }

    /**
     * Dibuja la imagen de fondo del panel.
     *
     * @param g Objeto Graphics utilizado para dibujar.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
