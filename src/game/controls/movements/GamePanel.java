package game.controls.movements;

import game.panlesBBDD.stageOne.PrinciPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Player player;
    private Camera camera;

    public GamePanel(game.map.SOMap mapa) {
        setPreferredSize(new Dimension(1920, 1080));
        setOpaque(false);  // Para que se vea el fondo
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        // Inicializa el jugador
        player = new Player(100, 100);

        // Inicializa la cámara usando el tamaño de pantalla y las dimensiones del mapa
        camera = new Camera(player, 1920, 1080, mapa.getWidth(), mapa.getHeight());

        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Ahora aplicamos la misma transformación para dibujar al jugador
        camera.setScreenSize(getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D) g.create();
        int offsetX = camera.getOffsetX();
        int offsetY = camera.getOffsetY();
        g2d.translate(-offsetX, -offsetY);

        // Dibuja al jugador en sus coordenadas en el mundo
        Image img = player.getImage();
        if (img != null) {
            g2d.drawImage(img, player.getX(), player.getY(), this);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect(player.getX(), player.getY(), 50, 50);
        }
        g2d.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.update();
        repaint(); // Repinta GamePanel
        // Repinta el JLayeredPane completo
        Object ancestor = SwingUtilities.getAncestorOfClass(PrinciPanel.class, this);
        if (ancestor instanceof PrinciPanel) {
            ((PrinciPanel) ancestor).repaintAll();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch(key) {
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
        switch(key) {
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

    public Player getPlayer() {
        return player;
    }

    public Camera getCamera() {
        return camera;
    }
}
