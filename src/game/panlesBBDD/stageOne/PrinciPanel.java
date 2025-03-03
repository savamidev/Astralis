package game.panlesBBDD.stageOne;

import game.controls.movements.GamePanel;
import game.controls.movements.Camera;

import javax.swing.*;
import java.awt.*;

public class PrinciPanel extends JPanel {
    private GamePanel gamePanel;

    public PrinciPanel() {
        setLayout(new BorderLayout());
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1920, 1080));

        // Crea la instancia del mapa (SOMap)
        game.map.SOMap mapa = new game.map.SOMap();

        // Crea el GamePanel, pasándole el mapa para inicializar la cámara correctamente
        gamePanel = new GamePanel(mapa);
        gamePanel.setBounds(0, 0, 1920, 1080);

        // Obtiene la cámara del GamePanel para usarla en las capas de fondo
        Camera camera = gamePanel.getCamera();

        // Capa 0: Fondo visual
        game.map.MapBackgroundPanel backgroundPanel = new game.map.MapBackgroundPanel(mapa, camera);
        backgroundPanel.setBounds(0, 0, 1920, 1080);

        // Capa 1: Fondo de colisiones (opcional, para depuración)
        game.map.CollisionBackgroundPanel collisionPanel = new game.map.CollisionBackgroundPanel(mapa, camera);
        collisionPanel.setBounds(0, 0, 1920, 1080);

        layeredPane.add(backgroundPanel, Integer.valueOf(0));
        layeredPane.add(collisionPanel, Integer.valueOf(1));
        layeredPane.add(gamePanel, Integer.valueOf(2));

        add(layeredPane, BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }

    public void repaintAll() {
        // Redibuja todos los componentes hijos
        for (Component component : getComponents()) {
            component.repaint();
        }
        // Redibuja el propio PrinciPanel
        repaint();
    }

    public static void main(String[] args) {
        // Creación de la ventana del juego
        JFrame frame = new JFrame("Mi Juego 2D");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new PrinciPanel());
        frame.pack(); // Ajusta el tamaño según el panel
        frame.setLocationRelativeTo(null); // Centra la ventana en la pantalla
        frame.setVisible(true); // Muestra la ventana
        frame.setResizable(false);
        frame.setSize(1920, 1080);
    }
}