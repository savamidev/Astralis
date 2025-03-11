package game.controls.movements;

import game.audio.BackgroundSound;
import game.objects.Collectible;
import game.objects.Collectible.Type;
import game.objects.PortalNPC;

import game.effects.LeafParticleEffect;
import game.effects.RunGrassEffect;
import game.listeners.LevelTransitionListener;
import game.panlesBBDD.map.map1.CollisionManager;
import game.panlesBBDD.map.map1.DeathStyledDialog;
import game.panlesBBDD.map.map1.TileMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Player player;
    private Camera camera;
    private TileMap tileMap;
    private CollisionManager collisionManager;
    private Image backgroundImage;
    private int worldWidth = 6493;
    private int worldHeight = 1080;
    private final int floorY = 1000;
    private final int initialStartY = 650;
    private final boolean debugMode = false;

    private BackgroundSound backgroundSound;
    private List<Collectible> collectibles;

    // Mensajes de colección
    private String collectibleMessage = null;
    private long collectibleMessageStartTime;
    private final int COLLECTIBLE_MESSAGE_DURATION = 2000;
    private int collectibleMessageX;
    private int collectibleMessageY;

    // Mensaje de instrucciones
    private boolean showInstructions = true;
    private long instructionStartTime;
    private final int INSTRUCTION_DURATION = 10000;
    private final int FADE_IN_DURATION = 2000;
    private final int VISIBLE_DURATION = 6000;
    private final int FADE_OUT_DURATION = 2000;
    private Image instructionsOverlayImg;
    private Image collectibleOverlayImg;
    private int customInstructionsX = 300;
    private int customInstructionsY = 800;

    private PortalNPC portalNpc;
    private long portalMessageStartTime;
    private final long PORTAL_MESSAGE_DURATION = 5000;

    private boolean deathTriggered = false;

    // Efectos
    private LeafParticleEffect leafParticleEffect;
    private RunGrassEffect runGrassEffect;
    private int maxLeafParticles = 150;
    private int maxGrassParticles = 30;

    // Variable para controlar la transición
    private boolean transitionTriggered = false;
    private LevelTransitionListener levelTransitionListener;

    public GamePanel() {
        setPreferredSize(new Dimension(1920, 1080));
        setOpaque(false);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        int tileSize = 40;
        tileMap = new TileMap("/resources/Map01.csv", tileSize);
        collisionManager = new CollisionManager(tileMap);

        player = new Player(150, initialStartY, worldWidth);
        camera = new Camera(player, 3000, 1080, worldWidth, worldHeight);

        URL bgUrl = getClass().getResource("/resources/imagen/fondoS10.png");
        if (bgUrl != null) {
            backgroundImage = new ImageIcon(bgUrl).getImage();
        } else {
            System.err.println("No se encontró el fondo en /resources/imagen/fondoS10.png");
        }

        backgroundSound = new BackgroundSound("/resources/sound/background/background.wav");
        backgroundSound.playWithFadeIn();

        collectibles = new ArrayList<>();
        collectibles.add(new Collectible(Type.SANDIA, 400, 370, 60, 60, "/resources/imagen/collect/sandia.png"));
        collectibles.add(new Collectible(Type.LLAVE, 1300, 550, 60, 60, "/resources/imagen/collect/llave.png"));
        collectibles.add(new Collectible(Type.BOTAS, 1500, 550, 50, 50, "/resources/imagen/collect/botas.png"));

        portalNpc = new PortalNPC(6300, 550, 100, 100, "/resources/imagen/npc/guard.gif");

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

        instructionStartTime = System.currentTimeMillis();
        timer = new Timer(16, this);
        timer.start();

        leafParticleEffect = new LeafParticleEffect(worldWidth, floorY, maxLeafParticles);
        runGrassEffect = new RunGrassEffect(maxGrassParticles);
    }

    public void setLevelTransitionListener(LevelTransitionListener listener) {
        this.levelTransitionListener = listener;
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

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, worldWidth, worldHeight, this);
        }

        if (debugMode) {
            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            drawTileMap(g2d);
            g2d.setComposite(originalComposite);
        }

        for (Collectible col : collectibles) {
            col.draw(g2d);
        }

        Image playerImg = player.getImage();
        if (playerImg != null) {
            g2d.drawImage(playerImg, player.getX(), player.getY(), player.getWidth(), player.getHeight(), this);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }

        if (player.getCollisionRectangle().intersects(portalNpc.getBounds())) {
            portalNpc.triggerMessage();
        }
        portalNpc.draw(g2d, player.getPlayerState().hasLlave());

        leafParticleEffect.draw(g2d);
        runGrassEffect.draw(g2d);

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
                    case 0: g2d.setColor(new Color(200, 200, 200)); break;
                    case 1: g2d.setColor(Color.DARK_GRAY); break;
                    case 2: g2d.setColor(Color.BLUE); break;
                    case 3: g2d.setColor(Color.GREEN); break;
                    case 4: g2d.setColor(Color.MAGENTA); break;
                    default: g2d.setColor(Color.BLACK); break;
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

        for (Collectible col : collectibles) {
            if (!col.isCollected()) {
                col.update();
            }
        }

        leafParticleEffect.update();
        runGrassEffect.update();

        if (player.getDx() != 0 && !player.isJumping()) {
            Rectangle feet = player.getFeetRectangle();
            int originX = feet.x + feet.width / 2;
            int originY = feet.y + feet.height / 2;
            runGrassEffect.spawnParticles(originX, originY);
        }

        Rectangle feetRect = player.getFeetRectangle();
        Rectangle waterRect = new Rectangle(feetRect.x, feetRect.y, feetRect.width, feetRect.height + 10);
        int tileSize = tileMap.getTileSize();
        int startCol = waterRect.x / tileSize;
        int endCol = (waterRect.x + waterRect.width) / tileSize;
        int startRow = waterRect.y / tileSize;
        int endRow = (waterRect.y + waterRect.height) / tileSize;
        boolean waterCollision = false;
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                if (tileMap.getTileAt(col * tileSize, row * tileSize) == 2) {
                    waterCollision = true;
                    break;
                }
            }
            if (waterCollision) break;
        }
        if (!deathTriggered && waterCollision) {
            triggerDeath();
            return;
        }

        if (player.getDy() < 0) {
            Rectangle headRect = player.getHeadRectangle();
            if (collisionManager.isColliding(headRect)) {
                int collisionRow = headRect.y / tileSize;
                int newY = (collisionRow + 1) * tileSize;
                player.setPosition(player.getX(), newY);
                player.resetVerticalMotion();
            }
        }

        Rectangle feetCollisionRect = player.getFeetRectangle();
        if (player.getDy() >= 0 && collisionManager.isColliding(feetCollisionRect)) {
            int collisionRow = (feetCollisionRect.y + feetCollisionRect.height) / tileSize;
            int newY = collisionRow * tileSize - player.getHeight();
            player.setPosition(player.getX(), newY);
            player.resetVerticalMotion();
        }

        Rectangle collisionRect = player.getCollisionRectangle();
        if (collisionManager.isColliding(collisionRect)) {
            player.setPosition(oldX, player.getY());
            player.stop();
        }

        for (Collectible col : collectibles) {
            if (!col.isCollected() && player.getCollisionRectangle().intersects(col.getBounds())) {
                col.setCollected(true);
                Rectangle bounds = col.getBounds();
                collectibleMessageX = bounds.x;
                collectibleMessageY = bounds.y - 50;

                if (col.getType() == Collectible.Type.SANDIA) {
                    player.getPlayerState().setSandia(true);
                    collectibleMessage = "Has recogido la Sandía: ¡Incrementa tu energía y habilita dash!";
                } else if (col.getType() == Collectible.Type.LLAVE) {
                    player.getPlayerState().setLlave(true);
                    collectibleMessage = "Has obtenido la Llave: ¡Ahora puedes abrir la puerta!";
                } else if (col.getType() == Collectible.Type.BOTAS) {
                    player.applyBoots();
                    collectibleMessage = "¡Has recogido las Botas: Aumenta tu velocidad!";
                }
                collectibleMessageStartTime = System.currentTimeMillis();
                playCollectSound();
            }
        }

        // Si el jugador tiene llave y colisiona con el portal, dispara la transición (solo una vez)
        if (!transitionTriggered && player.getCollisionRectangle().intersects(portalNpc.getBounds())
                && player.getPlayerState().hasLlave()) {
            transitionTriggered = true;
            timer.stop();
            if (backgroundSound != null) {
                backgroundSound.stop();
            }
            if (levelTransitionListener != null) {
                levelTransitionListener.onLevelTransitionRequested();
            }
            return;
        }

        Rectangle playerRect = player.getCollisionRectangle();
        startCol = playerRect.x / tileSize;
        int endColRect = (playerRect.x + playerRect.width) / tileSize;
        int startRowRect = playerRect.y / tileSize;
        int endRowRect = (playerRect.y + playerRect.height) / tileSize;
        boolean collisionWithType2 = false;
        for (int row = startRowRect; row <= endRowRect; row++) {
            for (int col = startCol; col <= endColRect; col++) {
                if (tileMap.getTileAt(col * tileSize, row * tileSize) == 2) {
                    collisionWithType2 = true;
                    break;
                }
            }
            if (collisionWithType2) break;
        }
        if (!deathTriggered && collisionWithType2) {
            triggerDeath();
        }

        repaint();
    }

    private void triggerDeath() {
        deathTriggered = true;
        timer.stop();
        if (backgroundSound != null) {
            backgroundSound.stop();
        }
        player.stop();
        player.stopWalkingSound();

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        DeathStyledDialog dsd = new DeathStyledDialog(owner, () -> {
            player.setPosition(150, initialStartY - player.getHeight());
            player.stop();
            player.stopWalkingSound();
            player.getPlayerState().reset();
            for (Collectible col : collectibles) {
                col.setCollected(false);
            }
            backgroundSound.playWithFadeIn();
            timer.start();
            deathTriggered = false;
            SwingUtilities.invokeLater(() -> requestFocusInWindow());
        });
        dsd.showDialog();
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

    private void drawOverlayMessages(Graphics2D g2d) {
        long currentTime = System.currentTimeMillis();
        if (collectibleMessage != null) {
            if (currentTime - collectibleMessageStartTime >= COLLECTIBLE_MESSAGE_DURATION) {
                collectibleMessage = null;
            } else {
                int overlayWidth = 400;
                int overlayHeight = 50;
                if (collectibleOverlayImg != null) {
                    g2d.drawImage(collectibleOverlayImg, collectibleMessageX, collectibleMessageY, overlayWidth, overlayHeight, null);
                } else {
                    g2d.setColor(new Color(0, 0, 0, 150));
                    g2d.fillRoundRect(collectibleMessageX, collectibleMessageY, overlayWidth, overlayHeight, 15, 15);
                }
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(collectibleMessage);
                int textX = collectibleMessageX + (overlayWidth - textWidth) / 2;
                int textY = collectibleMessageY + (overlayHeight + fm.getAscent()) / 2 - 5;
                g2d.drawString(collectibleMessage, textX, textY);
            }
        }
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
                int instructionsOverlayX = customInstructionsX;
                int instructionsOverlayY = customInstructionsY;
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
