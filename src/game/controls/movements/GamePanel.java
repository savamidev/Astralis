package game.controls.movements;

import game.map.TileMap;
import game.panlesBBDD.stageOne.PrinciPanel;
import game.collision.CollisionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Player player;
    private Camera camera;
    private TileMap tileMap;
    private CollisionManager collisionManager;
    private Image backgroundImage;
    // Se fija el mundo a 1920x1080
    private int worldWidth = 3000;
    private int worldHeight = 1080;

    // Modo debug para dibujar el TileMap con 50% de transparencia
    private final boolean debugMode = false;

    public GamePanel() {
        setPreferredSize(new Dimension(1920, 1080));
        setOpaque(false);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        int tileSize = 40;
        // Cargar el TileMap desde el archivo CSV
        tileMap = new TileMap("/resources/Map01.csv", tileSize);
        // Forzamos el mundo a 1920x1080, ignorando la altura que pueda indicar el CSV
        worldWidth = 3000;
        worldHeight = 1080;

        // Inicializar el CollisionManager
        collisionManager = new CollisionManager(tileMap);

        // Usar un suelo fijo de 650 (como se define en el constructor del jugador)
        int floorY = 750;

        // Inicializar al jugador usando floorY y worldWidth para límites horizontales.
        player = new Player(200, floorY, worldWidth);

        // Inicializar la cámara
        camera = new Camera(player, 3000, 1080, worldWidth, worldHeight);

        // Cargar la imagen de fondo
        URL bgUrl = getClass().getResource("/resources/fondoS4.png");
        if (bgUrl != null) {
            backgroundImage = new ImageIcon(bgUrl).getImage();
        } else {
            System.err.println("No se encontró la imagen de fondo en /resources/fondoS4.png");
        }

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
        camera.setScreenSize(getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D) g.create();

        int offsetX = camera.getOffsetX();
        int offsetY = camera.getOffsetY();

        // Traslada el origen según la cámara
        g2d.translate(-offsetX, -offsetY);

        // Dibuja la imagen de fondo ajustada al mundo
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, worldWidth, worldHeight, this);
        }

        // Dibuja el TileMap si está activo el modo debug
        if (debugMode) {
            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            drawTileMap(g2d);
            g2d.setComposite(originalComposite);
        }

        // Dibuja al jugador sin volver a restar el offset, ya que la transformación global ya se aplicó
        Image playerImg = player.getImage();
        if (playerImg != null) {
            g2d.drawImage(playerImg, player.getX(), player.getY(), player.getWidth(), player.getHeight(), this);
        } else {
            // Dibuja un rectángulo si la imagen del jugador es nula (debug visual)
            g2d.setColor(Color.RED);
            g2d.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }

        g2d.dispose();
    }

    private void drawTileMap(Graphics2D g2d) {
        int tileSize = tileMap.getTileSize();
        int[][] tiles = tileMap.getTiles();
        for (int row = 0; row < tiles.length; row++) {
            int cols = tiles[row].length;
            for (int col = 0; col < cols; col++) {
                int tileType = tiles[row][col];
                switch (tileType) {
                    case 0:
                        g2d.setColor(new Color(200, 200, 200));
                        break;
                    case 1:
                        g2d.setColor(Color.DARK_GRAY);
                        break;
                    case 2:
                        g2d.setColor(Color.BLUE);
                        break;
                    case 3:
                        g2d.setColor(Color.GREEN);
                        break;
                    case 4:
                        g2d.setColor(Color.MAGENTA);
                        break;
                    default:
                        g2d.setColor(Color.BLACK);
                        break;
                }
                g2d.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(col * tileSize, row * tileSize, tileSize, tileSize);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int oldX = player.getX();
        int oldY = player.getY();

        player.update(); // El jugador actualiza su propia posición (x,y).

        // Comprobar colisión en la parte superior (cabeza) cuando el jugador sube (dy negativo)
        if (player.getDy() < 0) {
            Rectangle headRect = player.getHeadRectangle();
            if (collisionManager.isColliding(headRect)) {
                int tileSize = tileMap.getTileSize();
                int collisionRow = headRect.y / tileSize;
                int newY = (collisionRow + 1) * tileSize;
                player.setPosition(player.getX(), newY);
                player.resetVerticalMotion();
            }
        }

        // Comprobar colisión vertical (pies del jugador)
        Rectangle feetRect = player.getFeetRectangle();
        if (player.getDy() >= 0 && collisionManager.isColliding(feetRect)) {
            int tileSize = tileMap.getTileSize();
            int collisionRow = (feetRect.y + feetRect.height) / tileSize;
            int newY = collisionRow * tileSize - player.getHeight();
            player.setPosition(player.getX(), newY);
            player.resetVerticalMotion();
        }

        // Comprobar colisión lateral
        Rectangle collisionRect = player.getCollisionRectangle();
        if (collisionManager.isColliding(collisionRect)) {
            player.setPosition(oldX, player.getY());
            player.stop();
        }

        repaint();

        Object ancestor = SwingUtilities.getAncestorOfClass(PrinciPanel.class, this);
        if (ancestor instanceof PrinciPanel) {
            ((PrinciPanel) ancestor).repaintAll();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
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
