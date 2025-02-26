package game.panlesBBDD.modelsIntro.introPack;

import game.panlesBBDD.modelsIntro.modelsVideo.VideoPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase MenuPanel
 *
 * Este panel representa el menú principal del juego, proporcionando botones
 * para iniciar el juego o salir de la aplicación.
 */
public class MenuPanel extends JPanel {
    private final CardLayout innerLayout;   // Layout interno para manejar la transición entre el título y el menú.
    private final JPanel introContainer;    // Contenedor del panel de introducción.
    private final IntroAudio introAudio;    // Controlador de audio de la introducción.
    private final CardLayout outerLayout;   // Layout externo para gestionar el cambio de paneles globalmente.
    private final JPanel outerContainer;    // Contenedor principal que gestiona los diferentes paneles de la aplicación.

    /**
     * Constructor de MenuPanel.
     *
     * @param innerLayout   CardLayout interno para la transición entre los paneles de la introducción.
     * @param introContainer Contenedor que maneja los paneles de la introducción.
     * @param introAudio    Instancia de IntroAudio para controlar la música de fondo.
     * @param outerLayout   CardLayout externo para la gestión global de paneles.
     * @param outerContainer Contenedor principal de la aplicación.
     */
    public MenuPanel(CardLayout innerLayout, JPanel introContainer, IntroAudio introAudio,
                     CardLayout outerLayout, JPanel outerContainer) {
        this.innerLayout = innerLayout;
        this.introContainer = introContainer;
        this.introAudio = introAudio;
        this.outerLayout = outerLayout;
        this.outerContainer = outerContainer;

        setOpaque(false); // Hace que el fondo del panel sea transparente.
        setLayout(new GridBagLayout()); // Se utiliza GridBagLayout para centrar los botones.

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0); // Espaciado entre botones.

        // Botón para iniciar el juego.
        JButton startButton = new JButton("START");
        startButton.setFont(new Font("Pixel Art", Font.PLAIN, 24)); // Fuente de estilo pixel art.
        add(startButton, gbc);

        // Botón para salir de la aplicación.
        gbc.gridy++;
        JButton exitButton = new JButton("EXIT");
        exitButton.setFont(new Font("Pixel Art", Font.PLAIN, 24));
        add(exitButton, gbc);

        // Acción del botón EXIT: cierra la aplicación al ser presionado.
        exitButton.addActionListener(e -> System.exit(0));

        // Acción del botón START: cambia al panel de video.
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Detiene la música de la introducción.
                introAudio.stop();

                // Verifica si el VideoPanel ya está agregado al contenedor externo.
                if(outerContainer.getComponentCount() < 2) {
                    // Si no está agregado, se crea y se añade.
                    VideoPanel videoPanel = new VideoPanel(outerLayout, outerContainer);
                    outerContainer.add(videoPanel, "VideoPanel");
                }

                // Cambia al panel de video en el layout externo.
                outerLayout.show(outerContainer, "VideoPanel");
            }
        });
    }
}
