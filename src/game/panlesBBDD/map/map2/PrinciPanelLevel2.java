package game.panlesBBDD.stageTwo;

import game.controls.movements.GamePanelLevel2;
import javax.swing.*;
import java.awt.*;

public class PrinciPanelLevel2 extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainContainer;

    public PrinciPanelLevel2() {
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setPreferredSize(new Dimension(1920, 1080));

        // Crear el GamePanel del nivel 2
        GamePanelLevel2 level2Panel = new GamePanelLevel2();
        level2Panel.setBounds(0, 0, 1920, 1080);
        mainContainer.add(level2Panel, "Level2");

        setLayout(new BorderLayout());
        add(mainContainer, BorderLayout.CENTER);

        // Mostrar directamente el panel del nivel 2
        cardLayout.show(mainContainer, "Level2");
        level2Panel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Nivel 2 - Panel de Prueba");
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
