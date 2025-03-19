package game.panlesBBDD.stageThree;

import game.controls.movements.GamePanelLevel3;
import javax.swing.*;
import java.awt.*;

/**
 * Panel principal para el Nivel 3.
 * Utiliza un CardLayout para contener el GamePanelLevel3 y gestionar la transición
 * (en este caso, el panel se muestra de forma independiente).
 */
public class PrinciPanelLevel3 extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private GamePanelLevel3 level3Panel;

    /**
     * Constructor que configura el contenedor y añade el GamePanelLevel3.
     */
    public PrinciPanelLevel3() {
        System.out.println("PrinciPanelLevel3: Constructor called.");
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setPreferredSize(new Dimension(1920, 1080));
        mainContainer.setFocusable(true);

        // Crear el GamePanel del nivel 3
        level3Panel = new GamePanelLevel3();
        level3Panel.setBounds(0, 0, 1920, 1080);
        mainContainer.add(level3Panel, "Level3");

        setLayout(new BorderLayout());
        add(mainContainer, BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> {
            mainContainer.requestFocusInWindow();
            level3Panel.requestFocusInWindow();
        });
    }

    /**
     * Método main para pruebas independientes del panel del Nivel 3.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Nivel 3 - PrinciPanel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            PrinciPanelLevel3 panel = new PrinciPanelLevel3();
            frame.setContentPane(panel);
            frame.pack();
            frame.setSize(1920, 1080);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
