package game.panlesBBDD.menuPrincipal.modelsVideo;

import javax.swing.*;
import java.awt.*;

public class VideoPanel extends JPanel {
    private VideoIntro videoIntro;

    public VideoPanel(CardLayout outerLayout, JPanel outerContainer) {
        setLayout(new BorderLayout());
        videoIntro = new VideoIntro(outerLayout, outerContainer);
        add(videoIntro, BorderLayout.CENTER);
        // Inicia el video al crear el panel
        videoIntro.play();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // El video cubre todo el panel; no se requiere fondo adicional.
    }
}
