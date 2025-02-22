package game.panlesBBDD.menuPrincipal;

import game.panlesBBDD.menuPrincipal.modelsVideo.VideoPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel {
    private final CardLayout innerLayout;
    private final JPanel introContainer;
    private final IntroAudio introAudio;
    private final CardLayout outerLayout;
    private final JPanel outerContainer;

    public MenuPanel(CardLayout innerLayout, JPanel introContainer, IntroAudio introAudio,
                     CardLayout outerLayout, JPanel outerContainer) {
        this.innerLayout = innerLayout;
        this.introContainer = introContainer;
        this.introAudio = introAudio;
        this.outerLayout = outerLayout;
        this.outerContainer = outerContainer;

        setOpaque(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        JButton startButton = new JButton("START");
        startButton.setFont(new Font("Pixel Art", Font.PLAIN, 24));
        add(startButton, gbc);

        gbc.gridy++;
        JButton exitButton = new JButton("EXIT");
        exitButton.setFont(new Font("Pixel Art", Font.PLAIN, 24));
        add(exitButton, gbc);

        exitButton.addActionListener(e -> System.exit(0));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Detener el audio de la intro
                introAudio.stop();
                // Agregar VideoPanel al contenedor externo (si no se agregó aún)
                if(outerContainer.getComponentCount() < 2) {
                    VideoPanel videoPanel = new VideoPanel(outerLayout, outerContainer);
                    outerContainer.add(videoPanel, "VideoPanel");
                }
                // Cambiar al panel de video (nivel externo)
                outerLayout.show(outerContainer, "VideoPanel");
            }
        });
    }
}
