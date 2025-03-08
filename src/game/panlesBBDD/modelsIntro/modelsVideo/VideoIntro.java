package game.panlesBBDD.modelsIntro.modelsVideo;

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

/**
 * Reproduce un video dentro de una aplicación Swing utilizando JavaFX.
 * <p>
 * La clase utiliza un {@code JFXPanel} para integrar JavaFX en Swing, creando una escena
 * que muestra el video. Al finalizar la reproducción, se transita al panel del juego.
 * </p>
 */
public class VideoIntro extends JFXPanel {
    private MediaPlayer mediaPlayer;  // Controla la reproducción del video.
    private CardLayout outerLayout;   // Layout externo para cambiar entre paneles.
    private JPanel outerContainer;    // Contenedor que gestiona los paneles de la aplicación.

    static {
        // Inicializa JavaFX una única vez en toda la aplicación.
        Platform.startup(() -> {});
    }

    /**
     * Crea una instancia de VideoIntro.
     *
     * @param outerLayout    CardLayout externo para la gestión de paneles.
     * @param outerContainer Contenedor donde se agregarán los paneles.
     */
    public VideoIntro(CardLayout outerLayout, JPanel outerContainer) {
        this.outerLayout = outerLayout;
        this.outerContainer = outerContainer;
        setPreferredSize(new Dimension(1920, 1080));
    }

    /**
     * Inicia la reproducción del video.
     * <p>
     * El método carga el video desde un recurso, configura la escena de JavaFX y
     * reproduce el video. Cuando finaliza, se detiene la reproducción y se transita
     * al panel del juego (PrinciPanel) de forma segura en el hilo Swing.
     * </p>
     */
    public void play() {
        Platform.runLater(() -> {
            String videoPath = new File("src/resources/videos/videoHistoria.mp4").toURI().toString();
            Media media = new Media(videoPath);
            mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);

            // Ajusta el video para ocupar toda la pantalla.
            mediaView.setFitWidth(1920);
            mediaView.setFitHeight(1080);
            mediaView.setPreserveRatio(true);

            StackPane root = new StackPane();
            root.getChildren().add(mediaView);
            Scene scene = new Scene(root, 1920, 1080);
            setScene(scene);

            // Al finalizar el video, detiene la reproducción y transita al panel del juego.
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                SwingUtilities.invokeLater(() -> {
                    boolean existePrinciPanel = false;
                    for (Component comp : outerContainer.getComponents()) {
                        if (comp instanceof game.panlesBBDD.stageOne.PrinciPanel) {
                            existePrinciPanel = true;
                            break;
                        }
                    }
                    if (!existePrinciPanel) {
                        game.panlesBBDD.stageOne.PrinciPanel princiPanel = new game.panlesBBDD.stageOne.PrinciPanel();
                        outerContainer.add(princiPanel, "PrinciPanel");
                    }
                    outerLayout.show(outerContainer, "PrinciPanel");
                });
            });

            mediaPlayer.play();
        });
    }
}
