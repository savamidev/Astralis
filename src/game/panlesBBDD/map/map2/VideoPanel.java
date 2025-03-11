package game.video;

import javax.swing.*;
import java.awt.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.*;
import javafx.scene.media.*;

public class VideoPanel extends JPanel {
    private JFXPanel jfxPanel;
    private Runnable onVideoFinished;

    public VideoPanel(Runnable onVideoFinished) {
        this.onVideoFinished = onVideoFinished;
        setLayout(new BorderLayout());
        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);
        initFX();
    }

    private void initFX() {
        Platform.runLater(() -> {
            String videoPath = getClass().getResource("/resources/videos/video1.mp4").toExternalForm();
            Media media = new Media(videoPath);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setPreserveRatio(true);
            mediaView.setFitWidth(getWidth());
            mediaView.setFitHeight(getHeight());

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
