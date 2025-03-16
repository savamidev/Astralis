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
    private final long PORTAL_MESSAGE_DURATION = 18000; // 10 segundos
    private boolean portalMessageTriggered = false;

    private boolean deathTriggered = false;

    // Efectos
    private LeafParticleEffect leafParticleEffect;
    private RunGrassEffect runGrassEffect;
    private int maxLeafParticles = 150;
    private int maxGrassParticles = 30;

    // Variable para controlar la transición
    private boolean transitionTriggered = false;
    private LevelTransitionListener levelTransitionListener;

    // Nueva variable para la casa
    private Image houseImage;
    private int houseX;
    private int houseY;

    public GamePanel() {
        setPreferredSize(new Dimension(1920, 1080));
        setOpaque(false);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        int tileSize = 40;
        tileMap = new TileMap("/resources/Map01.csv", tileSize);
        collisionManager = new CollisionManager(tileMap);

        player = new Player(10, initialStartY, worldWidth);
        camera = new Camera(player, 3000, 1080, worldWidth, worldHeight);

        URL bgUrl = getClass().getResource("/resources/imagen/fondoS10.png");
        if (bgUrl != null) {
            backgroundImage = new ImageIcon(bgUrl).getImage();
        } else {
            System.err.println("No se encontró el fondo en /resources/imagen/fondoS10.png");
        }

        backgroundSound = new BackgroundSound("/resources/sound/background/background.wav");
        // Reemplaza playWithFadeIn() por play() para iniciar el audio a volumen completo
        backgroundSound.play();

        collectibles = new ArrayList<>();
        collectibles.add(new Collectible(Type.SANDIA, 400, 370, 60, 60, "/resources/imagen/collect/sandia.png"));
        collectibles.add(new Collectible(Type.BOTAS, 1500, 550, 50, 50, "/resources/imagen/collect/botas.png"));

        portalNpc = new PortalNPC(6200, 440, 200, 200, "/resources/imagen/npc/guard.gif");

        // Cargar imagen de la casa y definir sus coordenadas
        URL houseUrl = getClass().getResource("/resources/imagen/casa.png");
        if (houseUrl != null) {
            houseImage = new ImageIcon(houseUrl).getImage();
            // Modifica estos valores para colocar la casa donde desees
            houseX = 6100;
            houseY = 45;
        } else {
            System.err.println("No se encontró la imagen de la casa en /resources/imagen/house.png");
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

        // Dibujar la casa (modifica houseX y houseY para ajustar la posición)
        if (houseImage != null) {
            g2d.drawImage(houseImage, houseX, houseY, houseImage.getWidth(this), houseImage.getHeight(this), this);
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

        // Se dibuja el NPC, el mensaje se activa internamente una única vez.
        portalNpc.draw(g2d);

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
        Rectangle waterRect = new Rectangle(feetRect.x, feetRect.y, feetRect.width, feetRect.height + 30);
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
                } else if (col.getType() == Collectible.Type.BOTAS) {
                    player.applyBoots();
                    collectibleMessage = "¡Has recogido las Botas: Aumenta tu velocidad!";
                }
                collectibleMessageStartTime = System.currentTimeMillis();
                playCollectSound();
            }
        }

        // Manejo del NPC Portal y transición:
        // Al colisionar, se activa el mensaje una única vez; pasados 10 segundos se realiza la transición.
        if (!transitionTriggered && player.getCollisionRectangle().intersects(portalNpc.getBounds())) {
            // Desactivar la entrada para que no se puedan procesar nuevos movimientos
            removeKeyListener(this);
            // Detener el movimiento del jugador
            player.stop();

            if (!portalMessageTriggered) {
                portalNpc.triggerMessage();
                portalMessageStartTime = System.currentTimeMillis();
                portalMessageTriggered = true;
            } else if (System.currentTimeMillis() - portalMessageStartTime >= PORTAL_MESSAGE_DURATION) {
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
            player.setPosition(130, initialStartY - player.getHeight());
            player.stop();
            player.stopWalkingSound();
            player.getPlayerState().reset();
            for (Collectible col : collectibles) {
                col.setCollected(false);
            }
            // Se reemplaza playWithFadeIn() por play() para iniciar a volumen completo
            backgroundSound.play();
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

                int overlayWidth = 700;
                int overlayHeight = 120;
                int instructionsOverlayX = 100;
                int instructionsOverlayY = 170;

                if (instructionsOverlayImg != null) {
                    g2d.drawImage(instructionsOverlayImg, instructionsOverlayX, instructionsOverlayY, overlayWidth, overlayHeight, null);
                } else {
                    g2d.setColor(new Color(0, 0, 0, 200));
                    g2d.fillRoundRect(instructionsOverlayX, instructionsOverlayY, overlayWidth, overlayHeight, 20, 20);
                }

                g2d.setColor(Color.WHITE);
                String instructionText = "Controles: W o ↑ para saltar, A o ← para izquierda, D o → para derecha, doble salto w o ↑ y shift para dash";

                Font originalFont = g2d.getFont();
                FontMetrics fm = g2d.getFontMetrics(originalFont);
                int textWidth = fm.stringWidth(instructionText);
                int maxTextWidth = overlayWidth - 10;

                if (textWidth > maxTextWidth) {
                    float scalingFactor = (float) maxTextWidth / textWidth;
                    float newFontSize = Math.max(10, originalFont.getSize2D() * scalingFactor);
                    Font newFont = originalFont.deriveFont(newFontSize);
                    g2d.setFont(newFont);
                    fm = g2d.getFontMetrics(newFont);
                    textWidth = fm.stringWidth(instructionText);
                }

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
