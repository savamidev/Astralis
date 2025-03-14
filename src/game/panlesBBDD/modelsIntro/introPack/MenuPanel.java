package game.panlesBBDD.modelsIntro.introPack;

import game.panlesBBDD.modelsIntro.modelsVideo.VideoPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPanel extends JPanel {
    private IntroAudio introAudio;
    private CardLayout outerLayout;
    private JPanel outerContainer;

    /**
     * Constructor que recibe el audio de introducción, el layout externo y el contenedor externo.
     *
     * @param introAudio     Instancia de IntroAudio para controlar la música.
     * @param outerLayout    CardLayout externo para cambiar de panel.
     * @param outerContainer Contenedor principal de la aplicación.
     */
    public MenuPanel(IntroAudio introAudio, CardLayout outerLayout, JPanel outerContainer) {
        this.introAudio = introAudio;
        this.outerLayout = outerLayout;
        this.outerContainer = outerContainer;
        setLayout(null);
        setOpaque(false);

        // Cargar y redimensionar la imagen del botón START
        ImageIcon startIcon = new ImageIcon("src/resources/imagen/botones/start.png");
        Image startImg = startIcon.getImage().getScaledInstance(850, 700, Image.SCALE_SMOOTH);
        ImageIcon resizedStartIcon = new ImageIcon(startImg);

        // Cargar y redimensionar la imagen del botón EXIT
        ImageIcon exitIcon = new ImageIcon("src/resources/imagen/botones/exit.png");
        Image exitImg = exitIcon.getImage().getScaledInstance(850, 700, Image.SCALE_SMOOTH);
        ImageIcon resizedExitIcon = new ImageIcon(exitImg);

        // Configurar botón START
        JButton startButton = new JButton(resizedStartIcon);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        // Ubicación y dimensiones (ajusta estos valores según tu diseño)
        startButton.setBounds(540, 10, resizedStartIcon.getIconWidth(), resizedStartIcon.getIconHeight());
        add(startButton);

        // Configurar botón EXIT
        JButton exitButton = new JButton(resizedExitIcon);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        exitButton.setBounds(540, 100, resizedExitIcon.getIconWidth(), resizedExitIcon.getIconHeight());
        add(exitButton);

        // Acción del botón EXIT: cierra la aplicación.
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Acción del botón START: detiene el audio y transita al VideoPanel.
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                introAudio.stop();
                if (outerContainer.getComponentCount() < 2) {
                    VideoPanel videoPanel = new VideoPanel(outerLayout, outerContainer);
                    outerContainer.add(videoPanel, "VideoPanel");
                }
                outerLayout.show(outerContainer, "VideoPanel");
            }
        });
    }
}
