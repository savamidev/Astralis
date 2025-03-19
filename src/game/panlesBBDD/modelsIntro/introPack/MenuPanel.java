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
        setLayout(null); // Usamos layout nulo para posicionar explícitamente
        setOpaque(false);

        // Cargar y redimensionar la imagen del botón START (por ejemplo, 600x500)
        ImageIcon startIcon = new ImageIcon("src/resources/imagen/botones/start.png");
        Image startImg = startIcon.getImage().getScaledInstance(600, 500, Image.SCALE_SMOOTH);
        ImageIcon resizedStartIcon = new ImageIcon(startImg);

        // Cargar y redimensionar la imagen del botón EXIT (por ejemplo, 600x500)
        ImageIcon exitIcon = new ImageIcon("src/resources/imagen/botones/exit.png");
        Image exitImg = exitIcon.getImage().getScaledInstance(600, 500, Image.SCALE_SMOOTH);
        ImageIcon resizedExitIcon = new ImageIcon(exitImg);

        // Crear botón START y establecer sus coordenadas explícitas
        JButton startButton = new JButton(resizedStartIcon);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        // Por ejemplo, posicionarlo en x=100, y=100
        startButton.setBounds(670, 30, resizedStartIcon.getIconWidth(), resizedStartIcon.getIconHeight());
        add(startButton);

        // Crear botón EXIT y establecer sus coordenadas explícitas
        JButton exitButton = new JButton(resizedExitIcon);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        // Para evitar superposición, colocar EXIT debajo de START.
        // Si START está en y=100 y tiene altura 500, con un margen de 20 píxeles:
        exitButton.setBounds(670, 300 , resizedExitIcon.getIconWidth(), resizedExitIcon.getIconHeight());
        add(exitButton);

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

        // Acción del botón EXIT: muestra confirmación y cierra la aplicación.
        // Acción del botón EXIT: muestra confirmación con apariencia uniforme y cierra la aplicación.
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Color uniforme para toda la ventana de diálogo
                Color unifiedColor = new Color(45, 45, 45);

                // Configurar propiedades de UIManager para que el fondo y el panel tengan el mismo color
                UIManager.put("OptionPane.background", unifiedColor);
                UIManager.put("Panel.background", unifiedColor);
                UIManager.put("OptionPane.messageForeground", Color.WHITE);
                UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 14));

                // Opciones personalizadas
                Object[] options = {"Salir", "Cancelar"};

                int result = JOptionPane.showOptionDialog(MenuPanel.this,
                        "¿Estás seguro que deseas salir?",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[1]);

                if (result == JOptionPane.YES_OPTION) {
                    if (introAudio != null) {
                        introAudio.stop();
                    }
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(MenuPanel.this);
                    if (frame != null) {
                        frame.dispose();
                    }
                    System.exit(0);
                }
            }
        });

    }
}
