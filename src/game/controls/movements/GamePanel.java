package game.controls.movements;

import game.map.TileMap;
import game.panlesBBDD.stageOne.PrinciPanel;
import game.collision.CollisionManager;
import game.audio.BackgroundSound;
import game.objects.Collectible;
import game.objects.Collectible.Type;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;

/**
 * Panel principal del juego que gestiona el renderizado, colisiones, audio y overlays.
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Player player;
    private Camera camera;
    private TileMap tileMap;
    private CollisionManager collisionManager;
    private Image backgroundImage;
    // Dimensiones del mundo
    private int worldWidth = 3000;
    private int worldHeight = 1080;
    private final boolean debugMode = false;

    // Fondo musical
    private BackgroundSound backgroundSound;

    // Coleccionables en el juego
    private List<Collectible> collectibles;

    // Variables para el mensaje al recoger un objeto
    private String collectibleMessage = null;
    private long collectibleMessageStartTime;
    private final int COLLECTIBLE_MESSAGE_DURATION = 2000; // 2 segundos

    // Variables para el overlay de instrucciones
    private boolean showInstructions = true;
    private long instructionStartTime;
    private final int INSTRUCTION_DURATION = 10000; // 10 segundos
    private final int FADE_IN_DURATION = 2000;  // 2 segundos
    private final int VISIBLE_DURATION = 6000;  // 6 segundos visibles
    private final int FADE_OUT_DURATION = 2000; // 2 segundos fade out

    // Overlay images y sus posiciones
    private Image instructionsOverlayImg;
    private Image collectibleOverlayImg;
    private int instructionsOverlayX, instructionsOverlayY;
    private int collectibleOverlayX, collectibleOverlayY;

    /**
     * Constructor que inicializa el panel de juego, el fondo, el jugador, la cámara, los coleccionables y el overlay de instrucciones.
     */
    public GamePanel() {
        setPreferredSize(new Dimension(1920, 1080));
        setOpaque(false);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        int tileSize = 40;
        tileMap = new TileMap("/resources/Map01.csv", tileSize);
        worldWidth = 3000;
        worldHeight = 1080;
        collisionManager = new CollisionManager(tileMap);

        int floorY = 750;
        player = new Player(200, floorY, worldWidth);
        camera = new Camera(player, 3000, 1080, worldWidth, worldHeight);

        URL bgUrl = getClass().getResource("/resources/fondoS4.png");
        if (bgUrl != null) {
            backgroundImage = new ImageIcon(bgUrl).getImage();
        } else {
            System.err.println("No se encontró el fondo en /resources/fondoS4.png");
        }

        // Iniciar fondo musical con fade-in
        backgroundSound = new BackgroundSound("/resources/sound/background/background.wav");
        backgroundSound.playWithFadeIn();

        // Inicializar coleccionables con coordenadas por defecto
        collectibles = new ArrayList<>();
        collectibles.add(new Collectible(Type.SANDIA, 500, 600, 70, 70, "/resources/imagen/collect/sandia.png"));
        collectibles.add(new Collectible(Type.LLAVE, 1300, 500, 70, 70, "/resources/imagen/collect/llave.png"));

        // Cargar imágenes de overlay para instrucciones y mensaje de colección
        URL instructionsUrl = getClass().getResource("/resources/imagen/overlays/instructions.png");
        if (instructionsUrl != null) {
            instructionsOverlayImg = new ImageIcon(instructionsUrl).getImage();
        } else {
            System.err.println("No se encontró la imagen de instrucciones en /resources/imagen/overlays/instructions.png");
        }
        URL collectibleUrl = getClass().getResource("/resources/imagen/overlays/collectible.png");
        if (collectibleUrl != null) {
            collectibleOverlayImg = new ImageIcon(collectibleUrl).getImage();
        } else {
            System.err.println("No se encontró la imagen de mensaje de colección en /resources/imagen/overlays/collectible.png");
        }

        // Iniciar el overlay de instrucciones
        instructionStartTime = System.currentTimeMillis();

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
        g2d.translate(-offsetX, -offsetY);

        // Dibuja el fondo
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, worldWidth, worldHeight, this);
        }

        // Si está en modo debug, dibuja el TileMap
        if (debugMode) {
            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            drawTileMap(g2d);
            g2d.setComposite(originalComposite);
        }

        // Dibuja los coleccionables
        for (Collectible col : collectibles) {
            col.draw(g2d);
        }

        // Dibuja al jugador
        Image playerImg = player.getImage();
        if (playerImg != null) {
            g2d.drawImage(playerImg, player.getX(), player.getY(), player.getWidth(), player.getHeight(), this);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }

        // Dibuja los overlays (mensaje de colección e instrucciones)
        drawOverlayMessages(g2d);

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

        player.update();

        // Actualizar la posición de cada coleccionable para el efecto de oscilación
        for (Collectible col : collectibles) {
            if (!col.isCollected()) {
                col.update();
            }
        }

        // Colisión con techo (cabeza)
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

        // Colisión con pies
        Rectangle feetRect = player.getFeetRectangle();
        if (player.getDy() >= 0 && collisionManager.isColliding(feetRect)) {
            int tileSize = tileMap.getTileSize();
            int collisionRow = (feetRect.y + feetRect.height) / tileSize;
            int newY = collisionRow * tileSize - player.getHeight();
            player.setPosition(player.getX(), newY);
            player.resetVerticalMotion();
        }

        // Colisión lateral
        Rectangle collisionRect = player.getCollisionRectangle();
        if (collisionManager.isColliding(collisionRect)) {
            player.setPosition(oldX, player.getY());
            player.stop();
        }

        // Verificar colisión con coleccionables
        for (Collectible col : collectibles) {
            if (!col.isCollected() && player.getCollisionRectangle().intersects(col.getBounds())) {
                col.setCollected(true);
                if (col.getType() == Collectible.Type.SANDIA) {
                    player.getPlayerState().setSandia(true);
                    collectibleMessage = "Has recogido la Sandía: ¡Incrementa tu energía y habilita dash!";
                } else if (col.getType() == Collectible.Type.LLAVE) {
                    player.getPlayerState().setLlave(true);
                    collectibleMessage = "Has obtenido la Llave: ¡Ahora puedes abrir la puerta!";
                }
                collectibleMessageStartTime = System.currentTimeMillis();
                playCollectSound();
            }
        }

        repaint();
        Object ancestor = SwingUtilities.getAncestorOfClass(PrinciPanel.class, this);
        if (ancestor instanceof PrinciPanel) {
            ((PrinciPanel) ancestor).repaintAll();
        }
    }

    private void playCollectSound() {
        try {
            URL url = getClass().getResource("/resources/sound/collect/collect.wav");
            if (url == null) {
                System.err.println("No se encontró el archivo collect.wav");
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            Clip collectClip = AudioSystem.getClip();
            collectClip.open(audioStream);
            collectClip.start();
        } catch (Exception e) {
            System.err.println("Error al reproducir sonido de recogida: " + e.getMessage());
        }
    }

    /**
     * Dibuja los overlays: mensaje de colección e instrucciones.
     * @param g2d Objeto Graphics2D.
     */
    private void drawOverlayMessages(Graphics2D g2d) {
        long currentTime = System.currentTimeMillis();

        // Overlay de mensaje de colección
        if (collectibleMessage != null) {
            if (currentTime - collectibleMessageStartTime >= COLLECTIBLE_MESSAGE_DURATION) {
                collectibleMessage = null;
            } else {
                int overlayWidth = 400;
                int overlayHeight = 50;
                collectibleOverlayX = (getWidth() - overlayWidth) / 2;
                collectibleOverlayY = 50;
                if (collectibleOverlayImg != null) {
                    g2d.drawImage(collectibleOverlayImg, collectibleOverlayX, collectibleOverlayY, overlayWidth, overlayHeight, null);
                } else {
                    g2d.setColor(new Color(0, 0, 0, 150));
                    g2d.fillRoundRect(collectibleOverlayX, collectibleOverlayY, overlayWidth, overlayHeight, 15, 15);
                }
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(collectibleMessage);
                int textX = collectibleOverlayX + (overlayWidth - textWidth) / 2;
                int textY = collectibleOverlayY + (overlayHeight + fm.getAscent()) / 2 - 5;
                g2d.drawString(collectibleMessage, textX, textY);
            }
        }

        // Overlay de instrucciones
        if (showInstructions) {
            long elapsed = currentTime - instructionStartTime;
            if (elapsed >= INSTRUCTION_DURATION) {
                showInstructions = false;
            } else {
                float alpha = 1.0f;
                if (elapsed < FADE_IN_DURATION) {
                    alpha = (float) elapsed / FADE_IN_DURATION;
                } else if (elapsed > FADE_IN_DURATION + VISIBLE_DURATION) {
                    long fadeOutElapsed = elapsed - (FADE_IN_DURATION + VISIBLE_DURATION);
                    alpha = 1.0f - (float) fadeOutElapsed / FADE_OUT_DURATION;
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                int overlayWidth = 500;
                int overlayHeight = 80;
                instructionsOverlayX = (getWidth() - overlayWidth) / 2;
                instructionsOverlayY = getHeight() - overlayHeight - 50;
                if (instructionsOverlayImg != null) {
                    g2d.drawImage(instructionsOverlayImg, instructionsOverlayX, instructionsOverlayY, overlayWidth, overlayHeight, null);
                } else {
                    g2d.setColor(new Color(0, 0, 0, 200));
                    g2d.fillRoundRect(instructionsOverlayX, instructionsOverlayY, overlayWidth, overlayHeight, 20, 20);
                }
                g2d.setColor(Color.WHITE);
                String instructionText = "Controles: W para saltar, A para izquierda, D para derecha, SHIFT para dash";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(instructionText);
                int textX = instructionsOverlayX + (overlayWidth - textWidth) / 2;
                int textY = instructionsOverlayY + (overlayHeight + fm.getAscent()) / 2 - 5;
                g2d.drawString(instructionText, textX, textY);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
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
