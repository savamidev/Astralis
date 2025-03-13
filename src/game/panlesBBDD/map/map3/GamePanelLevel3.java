package game.controls.movements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class GamePanelLevel3 extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Player player;
    private Image backgroundImage;
    // Dimensiones del panel
    private int worldWidth = 1920;
    private int worldHeight = 1080;

    public GamePanelLevel3() {
        setPreferredSize(new Dimension(worldWidth, worldHeight));
        setOpaque(false);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        // Inicializar el jugador, fondo o cualquier otro recurso
        player = new Player(100, 700, worldWidth);

        // Cargar un fondo de ejemplo
        URL bgUrl = getClass().getResource("/resources/imagen/fondoS3.png");
        if (bgUrl != null) {
            backgroundImage = new ImageIcon(bgUrl).getImage();
        } else {
            // Si no se encuentra la imagen, se pinta un color de fondo
            backgroundImage = null;
        }

        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.update();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, worldWidth, worldHeight, this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, worldWidth, worldHeight);
        }
        // Dibujar al jugador (o cualquier otro elemento)
        Image playerImg = player.getImage();
        if (playerImg != null) {
            g.drawImage(playerImg, player.getX(), player.getY(), player.getWidth(), player.getHeight(), this);
        } else {
            g.setColor(Color.RED);
            g.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Implementa el control del jugador según necesites
        int key = e.getKeyCode();
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
            case KeyEvent.VK_SHIFT:
                player.dash();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
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
    public void keyTyped(KeyEvent e) { }
}
