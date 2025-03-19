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
 * Panel que reproduce el video final del juego utilizando JavaFX embebido en Swing.
 * Al finalizar el video, se ejecuta el callback proporcionado para finalizar el juego.
 */
public class VideoFinal extends JPanel {
    private JFXPanel jfxPanel;
    private Runnable onVideoFinished;

    /**
     * Crea el panel para el video final y establece el callback que se ejecutará al finalizar.
     *
     * @param onVideoFinished Acción a ejecutar al terminar el video final.
     */
    public VideoFinal(Runnable onVideoFinished) {
        System.out.println("VideoFinal: Constructor llamado.");
        this.onVideoFinished = onVideoFinished;
        setLayout(new BorderLayout());
        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);
        initFX();
    }

    /**
     * Inicializa el entorno JavaFX, carga y reproduce el video final.
     */
    private void initFX() {
        Platform.runLater(() -> {
            try {
                // Se asume que el video final se encuentra en "resources/videos/videoFinal.mp4"
                URL url = VideoFinal.class.getClassLoader().getResource("resources/videos/videoFinal.mp4");
                System.out.println("VideoFinal: URL del video: " + url);
                if (url == null) {
                    System.err.println("VideoFinal: No se encontró el recurso de video en resources/videos/videoFinal.mp4");
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
                    System.out.println("VideoFinal: Video finalizado, llamando al callback.");
                    if (onVideoFinished != null) {
                        SwingUtilities.invokeLater(onVideoFinished);
                    }
                });

                mediaPlayer.play();
                System.out.println("VideoFinal: Reproduciendo video final.");
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(onVideoFinished);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // No es necesario pintar fondo adicional ya que el video lo cubre
    }
}
