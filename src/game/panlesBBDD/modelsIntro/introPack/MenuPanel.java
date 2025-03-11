package game.panlesBBDD.modelsIntro.introPack;

import game.panlesBBDD.modelsIntro.modelsVideo.VideoPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Representa el menú principal del juego.
 * <p>
 * Este panel muestra botones (como START y EXIT) mediante imágenes y permite moverlos
 * mediante arrastre. Además, se encarga de la transición al panel de video al iniciar el juego.
 * </p>
 */
public class MenuPanel extends JPanel {
    private final CardLayout innerLayout;   // Layout interno para la transición entre título y menú.
    private final JPanel introContainer;      // Contenedor de los paneles de introducción.
    private final IntroAudio introAudio;      // Controlador de audio de introducción.
    private final CardLayout outerLayout;     // Layout externo para gestionar el cambio de paneles.
    private final JPanel outerContainer;      // Contenedor principal de la aplicación.

    /**
     * Crea una instancia de MenuPanel con las dependencias necesarias.
     *
     * @param innerLayout    CardLayout interno para la transición.
     * @param introContainer Contenedor de los paneles de introducción.
     * @param introAudio     Instancia de IntroAudio para controlar la música.
     * @param outerLayout    CardLayout externo para el cambio global de paneles.
     * @param outerContainer Contenedor principal de la aplicación.
     */
    public MenuPanel(CardLayout innerLayout, JPanel introContainer, IntroAudio introAudio,
                     CardLayout outerLayout, JPanel outerContainer) {
        this.innerLayout = innerLayout;
        this.introContainer = introContainer;
        this.introAudio = introAudio;
        this.outerLayout = outerLayout;
        this.outerContainer = outerContainer;

        // Se utiliza un layout nulo para posicionamiento absoluto.
        setLayout(null);
        setOpaque(false);

        // Carga la imagen original del botón START.
        ImageIcon startIcon = new ImageIcon("src/resources/imagen/botones/start.png");
        // Redimensiona la imagen a las dimensiones deseadas (ejemplo: 200x100 píxeles).
        Image startImg = startIcon.getImage().getScaledInstance(650, 500, Image.SCALE_SMOOTH);
        // Crea un nuevo ImageIcon con la imagen redimensionada.
        ImageIcon resizedStartIcon = new ImageIcon(startImg);

        // Carga la imagen original del botón EXIT.
        ImageIcon exitIcon = new ImageIcon("src/resources/imagen/botones/exit.png");
        // Redimensiona la imagen a las dimensiones deseadas (ejemplo: 200x100 píxeles).
        Image exitImg = exitIcon.getImage().getScaledInstance(650, 500, Image.SCALE_SMOOTH);
        // Crea un nuevo ImageIcon con la imagen redimensionada.
        ImageIcon resizedExitIcon = new ImageIcon(exitImg);

        // Crea y configura el botón START usando el icono redimensionado.
        JButton startButton = new JButton(resizedStartIcon);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setBounds(600, 300, resizedStartIcon.getIconWidth(), resizedStartIcon.getIconHeight());
        add(startButton);

        // Crea y configura el botón EXIT usando el icono redimensionado.
        JButton exitButton = new JButton(resizedExitIcon);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        exitButton.setBounds(600, 370, resizedExitIcon.getIconWidth(), resizedExitIcon.getIconHeight());
        add(exitButton);

        // Acción del botón EXIT: cierra la aplicación.
        exitButton.addActionListener(e -> System.exit(0));

        // Acción del botón START: detiene el audio de introducción y transita al panel de video.
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                introAudio.stop();
                if (outerContainer.getComponentCount() < 2) {
                    VideoPanel videoPanel = new VideoPanel(outerLayout, outerContainer);
                    outerContainer.add(videoPanel, "VideoPanel");
                }
                outerLayout.show(outerContainer, "VideoPanel");
            }
        });
    }

    /**
     * Hace que un componente sea draggable (móvil mediante arrastre).
     * <p>
     * Si no se desea esta funcionalidad, se pueden eliminar las llamadas a este método.
     * </p>
     *
     * @param comp El componente que se desea hacer movible.
     */
    private void makeDraggable(JComponent comp) {
        MouseAdapter ma = new MouseAdapter() {
            Point initialClick;
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                // Obtiene la ubicación actual del componente.
                int thisX = comp.getLocation().x;
                int thisY = comp.getLocation().y;

                // Calcula la diferencia entre la posición actual y la inicial.
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // Establece la nueva ubicación.
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                comp.setLocation(X, Y);
            }
        };
        comp.addMouseListener(ma);
        comp.addMouseMotionListener(ma);
    }
}
