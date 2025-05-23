package game.video;

import javax.swing.*;
import java.awt.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.*;
import javafx.scene.media.*;
import javafx.scene.layout.StackPane;
import javax.swing.SwingUtilities;
import java.net.URL;

/**
 * Panel que muestra un video de transición utilizando JavaFX embebido en Swing.
 * Al finalizar el video, se ejecuta el callback indicado.
 */
public class TransitionVideoPanel extends JPanel {
    private JFXPanel jfxPanel;
    private Runnable onVideoFinished;

    /**
     * Crea el panel de video de transición y establece el callback a ejecutar al finalizar.
     *
     * @param onVideoFinished Acción a ejecutar al finalizar el video.
     */
    public TransitionVideoPanel(Runnable onVideoFinished) {
        System.out.println("TransitionVideoPanel: Constructor called.");
        this.onVideoFinished = onVideoFinished;
        setLayout(new BorderLayout());
        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);
        initFX();
    }

    /**
     * Inicializa el entorno JavaFX, carga y reproduce el video de transición.
     */
    private void initFX() {
        Platform.runLater(() -> {
            try {
                // Usamos el ClassLoader para mayor consistencia
                URL url = TransitionVideoPanel.class.getClassLoader().getResource("resources/videos/videoFase23.mp4");
                System.out.println("TransitionVideoPanel: URL del video: " + url);
                if(url == null) {
                    System.err.println("TransitionVideoPanel: No se encontró el recurso de video en resources/video.mp4");
                    SwingUtilities.invokeLater(onVideoFinished);
                    return;
                }
                String videoPath = url.toExternalForm();
                Media media = new Media(videoPath);
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                MediaView mediaView = new MediaView(mediaPlayer);
                mediaView.setPreserveRatio(true);
                mediaView.setFitWidth(1920);
                mediaView.setFitHeight(1080);

                StackPane root = new StackPane();
                Scene scene = new Scene(root, getWidth(), getHeight());
                root.getChildren().add(mediaView);

                jfxPanel.setScene(scene);

                mediaPlayer.setOnEndOfMedia(() -> {
                    System.out.println("TransitionVideoPanel: Video finalizado, llamando onVideoFinished.");
                    if (onVideoFinished != null) {
                        SwingUtilities.invokeLater(onVideoFinished);
                    }
                });

                mediaPlayer.play();
                System.out.println("TransitionVideoPanel: Reproduciendo video.");
            } catch(Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(onVideoFinished);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // No se pinta fondo adicional ya que el video lo cubre.
    }
}
