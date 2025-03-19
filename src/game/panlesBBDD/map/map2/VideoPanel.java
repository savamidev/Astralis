package game.video;

import javax.swing.*;
import java.awt.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.*;
import javafx.scene.media.*;

/**
 * Panel que integra un reproductor de video utilizando JavaFX embebido en Swing.
 * Reproduce un video y ejecuta una acción al finalizar la reproducción.
 */
public class VideoPanel extends JPanel {
    private JFXPanel jfxPanel;
    private Runnable onVideoFinished;

    /**
     * Crea el VideoPanel y establece el callback que se ejecutará al terminar el video.
     *
     * @param onVideoFinished Acción a ejecutar al finalizar el video.
     */
    public VideoPanel(Runnable onVideoFinished) {
        this.onVideoFinished = onVideoFinished;
        setLayout(new BorderLayout());
        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);
        initFX();
    }

    /**
     * Inicializa el entorno JavaFX, carga y reproduce el video.
     */
    private void initFX() {
        Platform.runLater(() -> {
            String videoPath = getClass().getResource("/resources/videos/video1.mp4").toExternalForm();
            Media media = new Media(videoPath);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setPreserveRatio(true);
            mediaView.setFitWidth(1920);
            mediaView.setFitHeight(1080);

            Group root = new Group();
            Scene scene = new Scene(root, getWidth(), getHeight());
            root.getChildren().add(mediaView);

            jfxPanel.setScene(scene);

            mediaPlayer.setOnEndOfMedia(() -> {
                if (onVideoFinished != null) {
                    SwingUtilities.invokeLater(onVideoFinished);
                }
            });

            mediaPlayer.play();
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
