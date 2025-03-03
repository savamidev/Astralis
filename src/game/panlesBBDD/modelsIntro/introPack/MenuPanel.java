package game.panlesBBDD.modelsIntro.introPack;

import game.panlesBBDD.modelsIntro.modelsVideo.VideoPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Clase MenuPanel
 *
 * Este panel representa el menú principal del juego, proporcionando botones
 * para iniciar el juego o salir de la aplicación. Los botones se muestran como imágenes
 * y se pueden mover libremente arrastrándolos.
 */
public class MenuPanel extends JPanel {
    private final CardLayout innerLayout;   // Layout interno para manejar la transición entre el título y el menú.
    private final JPanel introContainer;    // Contenedor del panel de la introducción.
    private final IntroAudio introAudio;    // Controlador de audio de la introducción.
    private final CardLayout outerLayout;   // Layout externo para gestionar el cambio de paneles globalmente.
    private final JPanel outerContainer;    // Contenedor principal de la aplicación.

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

        // Usar layout nulo para posicionamiento absoluto
        setLayout(null);
        setOpaque(false); // Fondo transparente

        // Cargar imágenes para los botones
        // Reemplaza "ruta/a/tu/imagen_start.png" y "ruta/a/tu/imagen_exit.png" por las rutas correctas de tus imágenes.
        ImageIcon startIcon = new ImageIcon("src/resources/next01.png");
        ImageIcon exitIcon = new ImageIcon("ruta/a/tu/imagen_exit.png");

        // Crear botón START con imagen
        JButton startButton = new JButton(startIcon);
        // Establecer posición y tamaño basado en la imagen
        startButton.setBounds(900, 500, startIcon.getIconWidth(), startIcon.getIconHeight());
        add(startButton);

        // Crear botón EXIT con imagen
        JButton exitButton = new JButton(exitIcon);
        exitButton.setBounds(100, 200, exitIcon.getIconWidth(), exitIcon.getIconHeight());
        add(exitButton);

        // Hacer que los botones se puedan mover arrastrándolos (opcional)
        makeDraggable(startButton);
        makeDraggable(exitButton);

        // Acción del botón EXIT: cierra la aplicación
        exitButton.addActionListener(e -> System.exit(0));

        // Acción del botón START: cambia al panel de video y detiene la música de la introducción.
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
     * Método auxiliar para hacer un componente draggable (móvil mediante arrastre).
     * Si no se desea esta funcionalidad, se pueden eliminar las llamadas a este método.
     *
     * @param comp El componente que se desea hacer movable.
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
                // Obtener la ubicación actual del componente
                int thisX = comp.getLocation().x;
                int thisY = comp.getLocation().y;

                // Calcular la diferencia entre la posición actual y la inicial
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // Establecer la nueva ubicación
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                comp.setLocation(X, Y);
            }
        };
        comp.addMouseListener(ma);
        comp.addMouseMotionListener(ma);
    }
}
