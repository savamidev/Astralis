package game.panlesBBDD.modelsIntro.introPack;

import javax.swing.*;
import java.awt.*;

/**
 * Clase IntroMother
 *
 * Esta clase representa el panel principal de la introducción del juego.
 * Maneja la reproducción de audio y la transición entre el título y el menú.
 */
public class IntroMother extends JPanel {
    private Image backgroundImage; // Imagen de fondo para la introducción.
    private IntroAudio introAudio; // Objeto para manejar la reproducción del audio de introducción.

    // Referencias externas para cambiar al panel de video
    private CardLayout outerLayout; // Layout externo para gestionar el cambio de paneles.
    private JPanel outerContainer;  // Contenedor externo que maneja los diferentes paneles de la aplicación.

    /**
     * Constructor de IntroMother.
     *
     * @param outerLayout   CardLayout externo, utilizado para cambiar de panel en la aplicación principal.
     * @param outerContainer Contenedor principal que gestiona los diferentes paneles del juego.
     */
    public IntroMother(CardLayout outerLayout, JPanel outerContainer) {
        this.outerLayout = outerLayout;
        this.outerContainer = outerContainer;

        setLayout(new BorderLayout()); // Define el diseño del panel como BorderLayout.

        // Carga la imagen de fondo desde los recursos.
        backgroundImage = new ImageIcon("src/resources/background GIF.gif").getImage();

        // Inicializa y reproduce el audio de introducción en bucle.
        introAudio = new IntroAudio("src/resources/SoundIntro.wav");
        introAudio.start();

        // CardLayout interno para manejar la transición entre el título y el menú.
        CardLayout innerLayout = new CardLayout();
        JPanel introContainer = new JPanel(innerLayout);
        introContainer.setOpaque(false); // Hace que el fondo del panel sea transparente.

        // Se crean los subpaneles:
        // 1. El panel del título con animaciones de fade in/out.
        Titulo titulo = new Titulo(innerLayout, introContainer);

        // 2. El menú principal con botones, que además recibe referencias externas
        // para permitir el cambio al panel de video.
        MenuPanel menuPanel = new MenuPanel(innerLayout, introContainer, introAudio, outerLayout, outerContainer);

        // Se añaden los paneles al contenedor interno.
        introContainer.add(titulo, "Titulo");
        introContainer.add(menuPanel, "MenuPanel");

        // Se muestra el panel del título primero.
        innerLayout.show(introContainer, "Titulo");

        // Se añade el contenedor de introducción al panel principal.
        add(introContainer, BorderLayout.CENTER);
    }

    /**
     * Sobrescribe el método paintComponent para dibujar la imagen de fondo.
     *
     * @param g Objeto Graphics utilizado para dibujar en el panel.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
