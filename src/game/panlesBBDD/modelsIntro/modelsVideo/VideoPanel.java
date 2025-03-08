package game.panlesBBDD.modelsIntro.modelsVideo;

import javax.swing.*;
import java.awt.*;

/**
 * Contenedor para la reproducción de video en el juego.
 * <p>
 * Este panel crea una instancia de {@code VideoIntro}, la agrega y la inicia para
 * mostrar el video en pantalla completa.
 * </p>
 */
public class VideoPanel extends JPanel {
    private VideoIntro videoIntro; // Instancia para gestionar la reproducción del video.

    /**
     * Crea una instancia de VideoPanel.
     *
     * @param outerLayout    CardLayout externo para cambiar entre paneles.
     * @param outerContainer Contenedor que gestiona los paneles de la aplicación.
     */
    public VideoPanel(CardLayout outerLayout, JPanel outerContainer) {
        setLayout(new BorderLayout());
        videoIntro = new VideoIntro(outerLayout, outerContainer);
        add(videoIntro, BorderLayout.CENTER);

        // Inicia la reproducción del video al crearse el panel.
        videoIntro.play();
    }

    /**
     * Sobrescribe el método paintComponent para evitar dibujar un fondo adicional.
     *
     * @param g Objeto Graphics utilizado para el dibujo.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // No se dibuja un fondo ya que el video lo cubre.
    }
}
