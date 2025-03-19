package game.panlesBBDD.stageTwo;

import game.controls.movements.GamePanelLevel2;
import game.listeners.LevelTransitionListener;
import game.panlesBBDD.stageThree.PrinciPanelLevel3;
import game.video.TransitionVideoPanel;
import javax.swing.*;
import java.awt.*;

/**
 * Panel principal para el Nivel 2.
 * Utiliza un CardLayout para gestionar la transición entre el GamePanelLevel2 y la fase de transición mediante video,
 * pasando luego al Nivel 3.
 */
public class PrinciPanelLevel2 extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private GamePanelLevel2 level2Panel;

    /**
     * Constructor que configura el contenedor y añade el panel del Nivel 2.
     * También asigna el listener para la transición al siguiente nivel.
     */
    public PrinciPanelLevel2() {
        System.out.println("PrinciPanelLevel2: Constructor called.");
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setPreferredSize(new Dimension(1920, 1080));
        mainContainer.setFocusable(true);

        // Crear el GamePanel del nivel 2 y asignarle el listener de transición
        level2Panel = new GamePanelLevel2();
        level2Panel.setBounds(0, 0, 1920, 1080);

        level2Panel.setLevelTransitionListener(new LevelTransitionListener() {
            @Override
            public void onLevelTransitionRequested() {
                System.out.println("PrinciPanelLevel2: onLevelTransitionRequested called.");
                TransitionVideoPanel videoPanel = new TransitionVideoPanel(() -> {
                    System.out.println("PrinciPanelLevel2: Callback from TransitionVideoPanel. Creating PrinciPanelLevel3.");
                    PrinciPanelLevel3 panelLevel3 = new PrinciPanelLevel3();
                    mainContainer.add(panelLevel3, "PrinciPanelLevel3");
                    cardLayout.show(mainContainer, "PrinciPanelLevel3");
                    panelLevel3.requestFocusInWindow();
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

    @Override
    public void addNotify() {
        super.addNotify();
        // Forzamos que el contenedor y el GamePanelLevel2 tengan el foco
        SwingUtilities.invokeLater(() -> {
            mainContainer.requestFocusInWindow();
            level2Panel.requestFocusInWindow();
        });
    }

    /**
     * Fuerza la actualización de la interfaz de todos los componentes contenidos.
     */
    public void repaintAll() {
        for (Component component : getComponents()) {
            component.repaint();
        }
        repaint();
    }

    /**
     * Método main para ejecutar el panel de Nivel 2 de forma independiente.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
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
