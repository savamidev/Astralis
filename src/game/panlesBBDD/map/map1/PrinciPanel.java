package game.panlesBBDD.stageOne;

import game.controls.movements.GamePanel;
import game.panlesBBDD.stageTwo.PrinciPanelLevel2;
import game.listeners.LevelTransitionListener;
import game.video.VideoPanel;
import javax.swing.*;
import java.awt.*;

public class PrinciPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainContainer;

    public PrinciPanel() {
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setPreferredSize(new Dimension(1920, 1080));

        // Crear el GamePanel del nivel 1
        GamePanel gamePanel = new GamePanel();
        gamePanel.setBounds(0, 0, 1920, 1080);
        // Asignar el listener de transición para pasar al nivel 2
        gamePanel.setLevelTransitionListener(new LevelTransitionListener() {
            @Override
            public void onLevelTransitionRequested() {
                // Mostrar el VideoPanel de transición
                VideoPanel videoPanel = new VideoPanel(() -> {
                    // Al terminar el video, cargar el contenedor de nivel 2 (PrinciPanelLevel2)
                    PrinciPanelLevel2 panelLevel2 = new PrinciPanelLevel2();
                    mainContainer.add(panelLevel2, "Level2");
                    cardLayout.show(mainContainer, "Level2");
                    panelLevel2.requestFocusInWindow();
                });
                mainContainer.add(videoPanel, "VideoPanel");
                cardLayout.show(mainContainer, "VideoPanel");
                videoPanel.requestFocusInWindow();
            }
        });

        mainContainer.add(gamePanel, "Level1");
        setLayout(new BorderLayout());
        add(mainContainer, BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> {
            // Solicitar el foco si es necesario.
        });
    }

    public void repaintAll() {
        for (Component component : getComponents()) {
            component.repaint();
        }
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Nivel 1");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            PrinciPanel panel = new PrinciPanel();
            frame.setContentPane(panel);
            frame.pack();
            frame.setSize(1920, 1080);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
