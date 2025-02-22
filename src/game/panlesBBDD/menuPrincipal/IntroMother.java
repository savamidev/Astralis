package game.panlesBBDD.menuPrincipal;

import javax.swing.*;
import java.awt.*;

public class IntroMother extends JPanel {
    private Image backgroundImage;
    private IntroAudio introAudio;

    // Referencias externas para cambiar al panel de video
    private CardLayout outerLayout;
    private JPanel outerContainer;

    public IntroMother(CardLayout outerLayout, JPanel outerContainer) {
        this.outerLayout = outerLayout;
        this.outerContainer = outerContainer;

        setLayout(new BorderLayout());
        backgroundImage = new ImageIcon("src/resources/background GIF.gif").getImage();
        introAudio = new IntroAudio("src/resources/SoundIntro.wav");
        introAudio.start();

        // Nivel interno: CardLayout para mostrar Titulo y luego MenuPanel
        CardLayout innerLayout = new CardLayout();
        JPanel introContainer = new JPanel(innerLayout);
        introContainer.setOpaque(false);

        // El panel del título (con animación de fade in/out)
        Titulo titulo = new Titulo(innerLayout, introContainer);
        // El menú con los botones (se pasa además outerLayout y outerContainer para poder cambiar a VideoPanel)
        MenuPanel menuPanel = new MenuPanel(innerLayout, introContainer, introAudio, outerLayout, outerContainer);

        introContainer.add(titulo, "Titulo");
        introContainer.add(menuPanel, "MenuPanel");
        innerLayout.show(introContainer, "Titulo");

        add(introContainer, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
