package game.panlesBBDD.stageTwo;

import game.controls.movements.GamePanelLevel2;
import game.controls.movements.GamePanelLevel3;
import game.listeners.LevelTransitionListener;
import game.video.TransitionVideoPanel;
import javax.swing.*;
import java.awt.*;

/**
 * Panel principal para el nivel 2 que gestiona la transición entre el GamePanelLevel2 y el GamePanelLevel3.
 * <p>
 * Cuando se detecta la colisión con el NPC en el GamePanelLevel2, se muestra un panel de video de transición
 * (utilizando TransitionVideoPanel) y, al finalizar el video, se carga el GamePanelLevel3.
 * </p>
 */
public class PrinciPanelLevel2 extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private GamePanelLevel2 level2Panel;

    /**
     * Crea el panel principal para el nivel 2 y configura la transición de paneles.
     */
    public PrinciPanelLevel2() {
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setPreferredSize(new Dimension(1920, 1080));

        // Crear el GamePanel del nivel 2
        level2Panel = new GamePanelLevel2();
        level2Panel.setBounds(0, 0, 1920, 1080);

        // Asignar el listener de transición para el nivel 2, utilizando TransitionVideoPanel
        level2Panel.setLevelTransitionListener(new LevelTransitionListener() {
            @Override
            public void onLevelTransitionRequested() {
                // Mostrar el TransitionVideoPanel al dispararse la transición
                TransitionVideoPanel videoPanel = new TransitionVideoPanel(() -> {
                    // Al terminar el video, cargar el GamePanelLevel3
                    GamePanelLevel3 level3 = new GamePanelLevel3();
                    mainContainer.add(level3, "Level3");
                    cardLayout.show(mainContainer, "Level3");
                    level3.requestFocusInWindow();
                });
                mainContainer.add(videoPanel, "TransitionVideoPanel");
                cardLayout.show(mainContainer, "TransitionVideoPanel");
                videoPanel.requestFocusInWindow();
            }
        });

        mainContainer.add(level2Panel, "Level2");
        setLayout(new BorderLayout());
        add(mainContainer, BorderLayout.CENTER);
    }

    /**
     * Al añadirse a la ventana, se solicita el foco para que el GamePanelLevel2 capture los eventos de teclado.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> {
            level2Panel.requestFocusInWindow();
        });
    }

    /**
     * Método principal para iniciar la aplicación.
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Nivel 2 - Transición");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            PrinciPanelLevel2 panel = new PrinciPanelLevel2();
            frame.setContentPane(panel);
            frame.pack();
            frame.setSize(1920, 1080);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
