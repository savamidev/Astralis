package game.controls.movements;

import game.audio.BackgroundSound;
import game.objects.PortalNPC;
import game.panlesBBDD.map.map1.CollisionManager;
import game.panlesBBDD.map.map1.DeathStyledDialog;
import game.panlesBBDD.map.map1.TileMap;
import game.effects.RainParticle;
import game.effects.Particle;
import game.effects.Lightning;
import game.listeners.LevelTransitionListener;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanelLevel2 extends JPanel implements ActionListener, KeyListener {

    // Parámetros del mundo y fase
    private int worldWidth = 5760;
    private int worldHeight = 1080;
    private final int floorY = 1000;
    private final int initialStartY = 670;
    private final boolean debugMode = false;

    // Elementos del juego
    private Player player;
    private Camera camera;
    private TileMap tileMap;
    private CollisionManager collisionManager;
    private Image backgroundImage;

    // Audio y NPC
    private BackgroundSound backgroundSound;
    private PortalNPC portalNpc;

    // Partículas
    private List<RainParticle> rainParticles;
    private final int baseRainDrops = 600;
    private List<Particle> footParticles;

    // Efectos de rayo
    private Lightning currentLightning;
    private long nextLightningTime = System.currentTimeMillis() + 4000;
    private final long lightningDuration = 1500; // ms
    private Clip lightningClip;
    private boolean lightningSoundStarted = false;
    private long lightningStartTime = 0;
    private final long soundFadeDuration = 4000; // ms

    // Capa oscura
    private final int virtualWidth = 3840;
    private final int virtualHeight = 2160;
    private float darknessLevel = 0.1f;
    private final float maxDarkness = 0.6f;
    private final float darknessStep = 0.0008f;

    // Vista del jugador
    private final int screenWidth = 1920;
    private final int screenHeight = 1080;

    private final Random rand = new Random();

    // Zona de advertencia para el rayo
    private boolean warningActive = false;
    private long warningStartTime = 0;
    private final long warningDuration = 1500; // 3 s
    private Rectangle warningZone = null;

    // Listener para transición
    private boolean transitionTriggered = false;
    private LevelTransitionListener levelTransitionListener;

    // Timer
    private Timer timer;

    // Efecto de lluvia
    private long rainStartTime = System.currentTimeMillis();

    public GamePanelLevel2() {
        setPreferredSize(new Dimension(worldWidth, worldHeight));
        setOpaque(false);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        tileMap = new TileMap("/resources/Map02.csv", 40);
        collisionManager = new CollisionManager(tileMap);

        player = new Player(150, initialStartY, worldWidth);
        // En este mapa el jugador NO debe tener sandía (solo 1 salto, sin dash)
        player.getPlayerState().setSandia(false);

        camera = new Camera(player, screenWidth, screenHeight, worldWidth, worldHeight);

        URL bgUrl = getClass().getResource("/resources/imagen/fondoS2.png");
        if (bgUrl != null) {
            backgroundImage = new ImageIcon(bgUrl).getImage();
        } else {
            System.err.println("No se encontró el fondo en /resources/imagen/fondoS2.png");
        }

        backgroundSound = new BackgroundSound("/resources/sound/background/background2.wav");
        backgroundSound.play();

        portalNpc = new PortalNPC(5380, 290, 500, 500, "/resources/imagen/cave.png");

        try {
            URL soundURL = getClass().getResource("/resources/sound/lightning.wav");
            if (soundURL != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
                lightningClip = AudioSystem.getClip();
                lightningClip.open(audioIn);
            } else {
                System.err.println("No se encontró el sonido del rayo en /resources/sound/lightning.wav");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        timer = new Timer(16, this);
        timer.start();

        initRainParticles();
        footParticles = new ArrayList<>();
    }

    private void initRainParticles() {
        rainParticles = new ArrayList<>();
        for (int i = 0; i < baseRainDrops; i++) {
            rainParticles.add(RainParticle.createRandom(worldWidth, worldHeight));
        }
    }

    public void setLevelTransitionListener(LevelTransitionListener listener) {
        this.levelTransitionListener = listener;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
            requestFocus();
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gWorld = (Graphics2D) g.create();
        camera.setScreenSize(getWidth(), getHeight());
        int camOffsetX = camera.getOffsetX();
        int camOffsetY = camera.getOffsetY();
        gWorld.translate(-camOffsetX, -camOffsetY);

        if (backgroundImage != null) {
            gWorld.drawImage(backgroundImage, 0, 0, worldWidth, worldHeight, this);
        }
        if (debugMode) {
            Composite orig = gWorld.getComposite();
            gWorld.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            drawTileMap(gWorld);
            gWorld.setComposite(orig);
        }
        for (Particle p : footParticles) {
            float alpha = p.getAlpha();
            Color footColor = new Color((p.colorRGB >> 16) & 0xFF,
                    (p.colorRGB >> 8) & 0xFF,
                    p.colorRGB & 0xFF,
                    (int)(alpha * 255));
            gWorld.setColor(footColor);
            gWorld.fillOval((int) p.x, (int) p.y, (int) p.size, (int) p.size);
        }
        Image playerImg = player.getImage();
        if (playerImg != null) {
            gWorld.drawImage(playerImg, player.getX(), player.getY(), player.getWidth(), player.getHeight(), this);
        } else {
            gWorld.setColor(Color.RED);
            gWorld.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }
        portalNpc.draw(gWorld);
        if (warningActive && warningZone != null) {
            gWorld.setColor(new Color(255, 0, 0, 30));
            gWorld.fillRect(warningZone.x, warningZone.y, warningZone.width, warningZone.height);
            gWorld.setColor(new Color(255, 0, 0, 40));
            gWorld.drawRect(warningZone.x, warningZone.y, warningZone.width, warningZone.height);
        }
        if (currentLightning != null) {
            currentLightning.draw(gWorld);
        }
        gWorld.dispose();

        Graphics2D gDark = (Graphics2D) g.create();
        gDark.setTransform(new AffineTransform());
        gDark.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, darknessLevel));
        gDark.setColor(Color.BLACK);
        int darkWidth = getWidth() * 2;
        int darkHeight = getHeight() * 2;
        gDark.fillRect(0, 0, darkWidth, darkHeight);
        gDark.dispose();

        Graphics2D gRain = (Graphics2D) g.create();
        gRain.translate(-camOffsetX, -camOffsetY);
        gRain.setStroke(new BasicStroke(2));
        for (RainParticle drop : rainParticles) {
            float alpha = drop.getAlpha();
            int a = (int)(alpha * 255);
            Color startColor = new Color(255, 255, 255, a);
            Color endColor = new Color(0, 0, 139, a);
            int x1 = (int) drop.x;
            int y1 = (int) drop.y;
            int x2 = (int)(drop.x + drop.size * 2);
            int y2 = (int)(drop.y + drop.size * 4);
            GradientPaint gp = new GradientPaint(x1, y1, startColor, x2, y2, endColor);
            gRain.setPaint(gp);
            gRain.drawLine(x1, y1, x2, y2);
        }
        gRain.dispose();

        if (currentLightning != null) {
            long elapsedFlash = System.currentTimeMillis() - lightningStartTime;
            long flashPeriod = lightningDuration / 3;
            long mod = elapsedFlash % flashPeriod;
            if (mod < flashPeriod * 0.2) {
                float flashAlpha = 0.2f * (1 - (float)mod / (flashPeriod * 0.2f));
                Graphics2D gFlash = (Graphics2D) g.create();
                gFlash.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, flashAlpha));
                gFlash.setColor(Color.WHITE);
                gFlash.fillRect(0, 0, getWidth(), getHeight());
                gFlash.dispose();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long currentTime = System.currentTimeMillis();

        player.update();
        for (int i = 0; i < footParticles.size(); i++) {
            Particle p = footParticles.get(i);
            p.update();
            if (!p.isAlive()) {
                footParticles.remove(i);
                i--;
            }
        }
        if (player.getDx() != 0 && !player.isJumping()) {
            spawnFootParticles(player.getFeetRectangle());
        }
        for (int i = 0; i < rainParticles.size(); i++) {
            RainParticle drop = rainParticles.get(i);
            drop.update();
            if (drop.x > worldWidth || drop.y > worldHeight) {
                rainParticles.set(i, RainParticle.createRandom(worldWidth, worldHeight));
            }
        }

        long elapsedRain = currentTime - rainStartTime;
        float rainMultiplier = 0.5f;
        if (elapsedRain < 20000) {
            rainMultiplier = 1.0f + 0.8f * (elapsedRain / 8000f);
        } else {
            rainMultiplier = 1.8f;
        }
        RainParticle.speedMultiplier = rainMultiplier;
        int desiredDrops = (int)(baseRainDrops * rainMultiplier);
        while (rainParticles.size() < desiredDrops) {
            rainParticles.add(RainParticle.createRandom(worldWidth, worldHeight));
        }

        if (!warningActive && currentTime >= nextLightningTime) {
            warningActive = true;
            warningStartTime = currentTime;
            int camOffsetX = camera.getOffsetX();
            int camOffsetY = camera.getOffsetY();
            int zoneWidth = 200;
            int zoneHeight = screenHeight;
            int warnX = camOffsetX + rand.nextInt(screenWidth - zoneWidth + 1);
            int warnY = camOffsetY;
            warningZone = new Rectangle(warnX, warnY, zoneWidth, zoneHeight);
            System.out.println("Warning zone activated: " + warningZone);
        }
        if (warningActive && currentTime >= warningStartTime + warningDuration) {
            currentLightning = new Lightning(lightningDuration, warningZone.x, warningZone.y, warningZone.width, warningZone.height);
            if (lightningClip != null) {
                lightningClip.stop();
                lightningClip.setFramePosition(0);
                lightningClip.start();
                lightningSoundStarted = true;
                lightningStartTime = currentTime;
            }
            if (player.getCollisionRectangle().intersects(warningZone)) {
                System.out.println("Player in warning zone. Executing lightning death.");
                lightningDeath();
            } else {
                System.out.println("Player avoided warning zone. Lightning appears.");
            }
            warningActive = false;
            nextLightningTime = currentTime + 4000;
        }
        if (lightningSoundStarted && lightningClip != null && lightningClip.isRunning()) {
            long fadeStart = lightningStartTime + lightningDuration;
            if (currentTime >= fadeStart) {
                float fadeProgress = (currentTime - fadeStart) / (float) soundFadeDuration;
                if (fadeProgress >= 1.0f) {
                    lightningClip.stop();
                    lightningSoundStarted = false;
                } else {
                    try {
                        FloatControl gainControl = (FloatControl) lightningClip.getControl(FloatControl.Type.MASTER_GAIN);
                        float minGain = gainControl.getMinimum();
                        float newGain = 0f + (minGain - 0f) * fadeProgress;
                        gainControl.setValue(newGain);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        if (!warningActive && currentLightning != null && !currentLightning.isActive()) {
            currentLightning = null;
        }

        if (!transitionTriggered && player.getCollisionRectangle().intersects(portalNpc.getBounds())) {
            System.out.println("NPC collision detected in GamePanelLevel2");
            transitionTriggered = true;
            player.stopWalkingSound();
            player.stopAllSounds();
            if (backgroundSound != null) {
                backgroundSound.stop();
            }
            if (levelTransitionListener != null) {
                System.out.println("Calling levelTransitionListener.onLevelTransitionRequested()");
                levelTransitionListener.onLevelTransitionRequested();
            } else {
                System.err.println("LevelTransitionListener is null in GamePanelLevel2");
            }
            return;
        }

        if (darknessLevel < maxDarkness) {
            darknessLevel += darknessStep;
            if (darknessLevel > maxDarkness) {
                darknessLevel = maxDarkness;
            }
        }

        repaint();
    }

    private void spawnFootParticles(Rectangle feet) {
        int numParticles = 2;
        for (int i = 0; i < numParticles; i++) {
            float x = feet.x + (float)(Math.random() * feet.width);
            float y = feet.y + feet.height;
            float dx = (float)(Math.random() - 0.5);
            float dy = -(float)(Math.random() * 1 + 0.5f);
            float maxLife = 20;
            int colorRGB = 0x777777;
            float size = 5 + (float)(Math.random() * 3);
            footParticles.add(new Particle(x, y, dx, dy, maxLife, colorRGB, size));
        }
    }

    private void lightningDeath() {
        System.out.println("Executing lightning death animation...");
        timer.stop();
        // Marcar al jugador como no vivo para evitar nuevos sonidos
        player.setAlive(false);
        player.stop();
        player.stopWalkingSound();
        player.stopAllSounds();
        if (backgroundSound != null) {
            backgroundSound.stop();
        }
        if (lightningClip != null && lightningClip.isRunning()) {
            lightningClip.stop();
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        DeathStyledDialog dsd = new DeathStyledDialog(owner, () -> {
            player.setPosition(150, initialStartY - player.getHeight());
            player.getPlayerState().reset();
            player.setAlive(true);
            backgroundSound.play();
            timer.start();
            System.out.println("Player repositioned after lightning death.");
        });
        dsd.showDialog();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!player.isAlive()) return;
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> player.moveLeft();
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> player.moveRight();
            case KeyEvent.VK_W, KeyEvent.VK_UP -> player.jump();
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> player.moveDown();
            case KeyEvent.VK_SHIFT -> player.dash();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!player.isAlive()) return;
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_A, KeyEvent.VK_LEFT, KeyEvent.VK_D, KeyEvent.VK_RIGHT -> player.stop();
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> player.stopDown();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    private void drawTileMap(Graphics2D g2d) {
        int tileSize = tileMap.getTileSize();
        int[][] tiles = tileMap.getTiles();
        for (int row = 0; row < tiles.length; row++) {
            int cols = tiles[row].length;
            for (int col = 0; col < cols; col++) {
                int tileType = tiles[row][col];
                switch (tileType) {
                    case 0 -> g2d.setColor(new Color(200, 200, 200));
                    case 1 -> g2d.setColor(Color.DARK_GRAY);
                    case 2 -> g2d.setColor(Color.BLUE);
                    case 3 -> g2d.setColor(Color.GREEN);
                    case 4 -> g2d.setColor(Color.MAGENTA);
                    default -> g2d.setColor(Color.BLACK);
                }
                g2d.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(col * tileSize, row * tileSize, tileSize, tileSize);
            }
        }
    }
}
