package game.panlesBBDD.stageOne;

import game.controls.movements.GamePanel;
import game.controls.movements.PlayerState;
import javax.swing.*;
import java.awt.*;

public class PrinciPanel extends JPanel {
    private GamePanel gamePanel;

    public PrinciPanel() {
        setLayout(new BorderLayout());
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1920, 1080));
        gamePanel = new GamePanel();
        gamePanel.setBounds(0, 0, 1920, 1080);
        layeredPane.add(gamePanel, Integer.valueOf(0));
        add(layeredPane, BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }

    public void repaintAll() {
        for (Component component : getComponents()) {
            component.repaint();
        }
        repaint();
    }

    public void loadNextLevel(PlayerState state) {
        System.out.println("Cargando el siguiente nivel con: Sandía = " + state.hasSandia() + ", Llave = " + state.hasLlave());
        removeAll();
        gamePanel = new GamePanel();
        gamePanel.setBounds(0, 0, 1920, 1080);
        add(gamePanel);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Nivel 1");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            PrinciPanel panel = new PrinciPanel();
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setSize(1920, 1080);
            frame.setVisible(true);
        });
    }
}
