package game.panlesBBDD.modelsIntro.movements;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer; // Timer para actualizar el juego (~60 FPS)
    private Player player; // Instancia del jugador

    public GamePanel() {
        setPreferredSize(new Dimension(1920, 1080)); // Tamaño del panel
        setFocusable(true); // Permite que el panel reciba eventos de teclado
        addKeyListener(this); // Agrega el KeyListener para capturar teclas

        // Inicializa el jugador en una posición específica (ejemplo: 100, 100)
        player = new Player(100, 100);

        // Timer para llamar a actionPerformed cada 16 ms (60 FPS)
        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Dibuja el fondo en blanco
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 1920, 1080);

        // Dibuja al jugador con la animación actual
        Image img = player.getImage();
        if (img != null) {
            g.drawImage(img, player.getX(), player.getY(), this);
        } else {
            // Si la imagen no está disponible, se dibuja un rectángulo rojo
            g.setColor(Color.RED);
            g.fillRect(player.getX(), player.getY(), 50, 50);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Actualiza el estado del jugador y repinta el panel
        player.update();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Movimiento del jugador según la tecla presionada
        switch (key) {
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                player.moveLeft();
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                player.moveRight();
                break;
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                player.jump();
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                player.moveDown();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        // Detiene el movimiento cuando se suelta la tecla
        switch (key) {
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                player.stop();
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                player.stopDown();
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No se usa, pero debe estar implementado por la interfaz KeyListener
    }

    public static void main(String[] args) {
        // Creación de la ventana del juego
        JFrame frame = new JFrame("Mi Juego 2D");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new GamePanel());
        frame.pack(); // Ajusta el tamaño según el panel
        frame.setLocationRelativeTo(null); // Centra la ventana en la pantalla
        frame.setVisible(true); // Muestra la ventana
    }
}

