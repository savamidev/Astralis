package game.panlesBBDD.menuPrincipal.modelsVideo;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.layout.StackPane;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class VideoIntro extends JFXPanel {
    private MediaPlayer mediaPlayer;
    private CardLayout outerLayout;
    private JPanel outerContainer;

    static {
        // Inicializar JavaFX una sola vez
        Platform.startup(() -> {});
    }

    public VideoIntro(CardLayout outerLayout, JPanel outerContainer) {
        this.outerLayout = outerLayout;
        this.outerContainer = outerContainer;
        setPreferredSize(new Dimension(1920, 1080));
    }

    public void play() {
        Platform.runLater(() -> {
            String videoPath = new File("src/resources/video.mp4").toURI().toString();
            Media media = new Media(videoPath);
            mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setFitWidth(1920);
            mediaView.setFitHeight(1080);
            mediaView.setPreserveRatio(true);

            StackPane root = new StackPane();
            root.getChildren().add(mediaView);
            Scene scene = new Scene(root, 1920, 1080);
            setScene(scene);

            // Opcional: al finalizar el video se puede regresar a la intro o detenerse
            mediaPlayer.setOnEndOfMedia(() -> {
                Platform.runLater(() -> {
                    mediaPlayer.stop();
                    // Ejemplo: regresar a la intro
                    outerLayout.show(outerContainer, "IntroMother");
                });
            });

            mediaPlayer.play();
        });
    }
}
