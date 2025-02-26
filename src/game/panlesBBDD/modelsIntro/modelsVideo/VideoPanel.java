package game.panlesBBDD.modelsIntro.modelsVideo;

import javax.swing.*;
import java.awt.*;

/**
 * Clase VideoPanel
 *
 * Este panel sirve como contenedor para el componente de reproducción de video.
 * Se encarga de inicializar y mostrar el video en la aplicación.
 */
public class VideoPanel extends JPanel {
    private VideoIntro videoIntro; // Instancia de VideoIntro para manejar la reproducción del video.

    /**
     * Constructor de VideoPanel.
     *
     * @param outerLayout    CardLayout externo para cambiar entre pantallas.
     * @param outerContainer Contenedor donde se gestionan los distintos paneles.
     */
    public VideoPanel(CardLayout outerLayout, JPanel outerContainer) {
        setLayout(new BorderLayout()); // Usa BorderLayout para centrar el contenido.

        // Crea la instancia de VideoIntro y la añade al panel.
        videoIntro = new VideoIntro(outerLayout, outerContainer);
        add(videoIntro, BorderLayout.CENTER);

        // Inicia la reproducción del video al crear el panel.
        videoIntro.play();
    }

    /**
     * Sobrescribe el método paintComponent para evitar pintar un fondo adicional,
     * ya que el video cubre todo el panel.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // No es necesario dibujar un fondo, ya que el video se encarga de ello.
    }
}
