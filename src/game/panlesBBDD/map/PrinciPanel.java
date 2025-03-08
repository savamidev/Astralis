package game.panlesBBDD.stageOne;

import game.controls.movements.GamePanel;
import game.controls.movements.PlayerState;
import javax.swing.*;
import java.awt.*;

/**
 * Representa el panel principal de la primera fase del juego.
 * <p>
 * Este panel contiene un GamePanel que gestiona el juego en sí. Además, proporciona
 * métodos para refrescar la interfaz y cargar el siguiente nivel basándose en el estado del jugador.
 * </p>
 */
public class PrinciPanel extends JPanel {
    private GamePanel gamePanel;

    /**
     * Crea una instancia de PrinciPanel y configura su diseño.
     * <p>
     * Se utiliza un JLayeredPane para organizar el GamePanel, permitiendo un manejo flexible
     * de los componentes superpuestos.
     * </p>
     */
    public PrinciPanel() {
        setLayout(new BorderLayout());
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1920, 1080));
        gamePanel = new GamePanel();
        gamePanel.setBounds(0, 0, 1920, 1080);
        layeredPane.add(gamePanel, Integer.valueOf(0));
        add(layeredPane, BorderLayout.CENTER);
    }

    /**
     * Se invoca cuando el panel se añade al contenedor. Solicita el foco para que el GamePanel
     * pueda recibir eventos de teclado.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }

    /**
     * Refresca la interfaz de todos los componentes contenidos en el panel.
     */
    public void repaintAll() {
        for (Component component : getComponents()) {
            component.repaint();
        }
        repaint();
    }

    /**
     * Carga el siguiente nivel del juego basado en el estado actual del jugador.
     *
     * @param state Estado del jugador, que incluye información sobre objetos recogidos.
     */
    public void loadNextLevel(PlayerState state) {
        System.out.println("Cargando el siguiente nivel con: Sandía = " + state.hasSandia() + ", Llave = " + state.hasLlave());
        removeAll();
        gamePanel = new GamePanel();
        gamePanel.setBounds(0, 0, 1920, 1080);
        add(gamePanel);
        revalidate();
        repaint();
    }

    /**
     * Método principal para ejecutar este panel de forma independiente (útil para pruebas).
     *
     * @param args Argumentos de la línea de comandos.
     */
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
