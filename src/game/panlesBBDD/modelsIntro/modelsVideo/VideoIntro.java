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
 * Clase VideoIntro
 *
 * Esta clase utiliza JavaFX para reproducir un video dentro de una aplicación Swing.
 * Se usa JFXPanel para integrar JavaFX en Swing y mostrar un video en pantalla completa.
 */
public class VideoIntro extends JFXPanel {
    private MediaPlayer mediaPlayer;  // Objeto para controlar la reproducción del video.
    private CardLayout outerLayout;   // Layout externo para cambiar entre pantallas.
    private JPanel outerContainer;    // Contenedor externo donde se encuentran los paneles.

    static {
        // Inicializa JavaFX una única vez en toda la aplicación.
        Platform.startup(() -> {});
    }

    /**
     * Constructor de VideoIntro.
     *
     * @param outerLayout    CardLayout externo para la gestión de pantallas.
     * @param outerContainer Contenedor donde se agregarán los paneles de la aplicación.
     */
    public VideoIntro(CardLayout outerLayout, JPanel outerContainer) {
        this.outerLayout = outerLayout;
        this.outerContainer = outerContainer;
        setPreferredSize(new Dimension(1920, 1080)); // Define el tamaño del panel de video.
    }

    /**
     * Método para reproducir el video.
     */
    public void play() {
        Platform.runLater(() -> { // Ejecuta en el hilo de JavaFX.
            // Carga el archivo de video desde la carpeta de recursos.
            String videoPath = new File("src/resources/video.mp4").toURI().toString();
            Media media = new Media(videoPath);
            mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);

            // Ajusta el tamaño del video para ocupar toda la pantalla.
            mediaView.setFitWidth(1920);
            mediaView.setFitHeight(1080);
            mediaView.setPreserveRatio(true); // Mantiene la relación de aspecto.

            // Crea un contenedor StackPane y agrega el video.
            StackPane root = new StackPane();
            root.getChildren().add(mediaView);
            Scene scene = new Scene(root, 1920, 1080);
            setScene(scene); // Establece la escena en el JFXPanel.

            // Cuando el video termina, regresa al panel de la introducción.
            mediaPlayer.setOnEndOfMedia(() -> {
                Platform.runLater(() -> {
                    mediaPlayer.stop(); // Detiene la reproducción.
                    outerLayout.show(outerContainer, "IntroMother"); // Cambia a la intro.
                });
            });

            mediaPlayer.play(); // Inicia la reproducción del video.
        });
    }
}
