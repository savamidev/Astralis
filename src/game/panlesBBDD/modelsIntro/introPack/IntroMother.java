package game.panlesBBDD.modelsIntro.introPack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class IntroMother extends JPanel {
    private Image backgroundImage;
    private IntroAudio introAudio;

    /**
     * Constructor que recibe el CardLayout y el contenedor externo para gestionar la transición al VideoPanel.
     *
     * @param outerLayout    CardLayout externo para cambiar de panel.
     * @param outerContainer Contenedor principal de la aplicación.
     */
    public IntroMother(CardLayout outerLayout, JPanel outerContainer) {
        setLayout(new BorderLayout());

        // Cargar la imagen de fondo.
        backgroundImage = new ImageIcon("src/resources/imagen/fondoInicio.gif").getImage();

        // Iniciar el audio de introducción.
        introAudio = new IntroAudio("src/resources/sound/SoundIntro.wav");
        introAudio.start();

        // Crear el panel del título.
        Titulo titulo = new Titulo();

        // Crear el panel del menú y ocultarlo inicialmente.
        MenuPanel menuPanel = new MenuPanel(introAudio, outerLayout, outerContainer);
        menuPanel.setVisible(false);

        // Agregar los paneles: título en la parte superior y menú en el centro.
        add(titulo, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);

        // Timer para mostrar el menú después de 10 segundos.
        Timer showMenuTimer = new Timer(5000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuPanel.setVisible(true);
                revalidate();
                repaint();
                ((Timer)e.getSource()).stop();
            }
        });
        showMenuTimer.setInitialDelay(9000);
        showMenuTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
