package game.video;

import javax.swing.*;
import java.awt.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.*;
import javafx.scene.media.*;
import javafx.scene.layout.StackPane;
import javax.swing.SwingUtilities;

/**
 * Panel que muestra un video de transición.
 * <p>
 * Este panel utiliza JavaFX embebido en Swing para reproducir un video de transición. Se asume que el video
 * de transición es diferente al asociado en la clase VideoPanel (por ejemplo, se utiliza la ruta
 * "/resources/videos/newTransition.mp4"). Al finalizar el video se ejecuta un callback para continuar
 * con la transición a la siguiente etapa.
 * </p>
 */
public class TransitionVideoPanel extends JPanel {
    private JFXPanel jfxPanel;
    private Runnable onVideoFinished;

    /**
     * Crea un panel de video de transición.
     *
     * @param onVideoFinished Callback que se ejecuta cuando el video termina.
     */
    public TransitionVideoPanel(Runnable onVideoFinished) {
        this.onVideoFinished = onVideoFinished;
        setLayout(new BorderLayout());
        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);
        initFX();
    }

    /**
     * Inicializa el entorno JavaFX y reproduce el video de transición.
     * <p>
     * Se utiliza la ruta "/resources/videos/newTransition.mp4" para el video de transición.
     * </p>
     */
    private void initFX() {
        Platform.runLater(() -> {
            String videoPath = getClass().getResource("/resources/video.mp4").toExternalForm();
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
