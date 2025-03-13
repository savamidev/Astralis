package game.controls.movements;

import game.audio.BackgroundSound;
import game.objects.PortalNPC;
import game.panlesBBDD.map.map1.CollisionManager;
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

/**
 * Clase principal del panel de juego (nivel 2) que integra el jugador, partículas, sonido,
 * rayos con flash y demás efectos.
 */
public class GamePanelLevel2 extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Player player;
    private Camera camera;
    private TileMap tileMap;
    private CollisionManager collisionManager;
    private Image backgroundImage;
    // Dimensiones del área de juego
    private int worldWidth = 5760;
    private int worldHeight = 1080;
    private final int floorY = 1000;
    private final int initialStartY = 670;
    private final boolean debugMode = false;

    private BackgroundSound backgroundSound;
    private PortalNPC portalNpc;
    private boolean deathTriggered = false;

    // Partículas de lluvia
    private List<RainParticle> rainParticles;
    private final int numRaindrops = 600;
    // Partículas "foot dust"
    private List<Particle> footParticles;

    // Instrucciones
    private boolean showInstructions = true;
    private long instructionStartTime;
    private final int INSTRUCTION_DURATION = 10000;
    private final int FADE_IN_DURATION = 2000;
    private final int VISIBLE_DURATION = 6000;
    private final int FADE_OUT_DURATION = 2000;
    private Image instructionsOverlayImg;
    private int customInstructionsX = 300;
    private int customInstructionsY = 800;

    // Sistema de rayos: uso de la nueva clase Lightning
    private Lightning currentLightning;
    // Nuevo intervalo aleatorio de 5 a 10 segundos para el rayo
    private long nextLightningTime = System.currentTimeMillis() + (5000 + (long) (Math.random() * 5000));
    private final long lightningDuration = 1500; // 1.5 segundos

    // Sonido del rayo
    private Clip lightningClip;
    private boolean lightningSoundStarted = false;
    private long lightningStartTime = 0; // Tiempo en que se inició el sonido
    private final long soundFadeDuration = 4000; // Duración del fade-out

    // Filtro de oscurecimiento gradual
    private float darknessLevel = 0.1f;
    private final float maxDarkness = 0.7f;
    private final float darknessStep = 0.0007f;

    // Simulación de pantalla virtual
    private final int virtualWidth = 3840;
    private final int virtualHeight = 2160;
    private Random rand = new Random();

    // Variable para controlar la transición inmediata con el NPC
    private boolean transitionTriggered = false;

    private LevelTransitionListener levelTransitionListener;

    public void setLevelTransitionListener(LevelTransitionListener listener) {
        this.levelTransitionListener = listener;
    }

    public GamePanelLevel2() {
        setPreferredSize(new Dimension(worldWidth, worldHeight));
        setOpaque(false);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        int tileSize = 40;
        tileMap = new TileMap("/resources/Map02.csv", tileSize);
        collisionManager = new CollisionManager(tileMap);

        player = new Player(150, initialStartY, worldWidth);
        camera = new Camera(player, 1920, 1080, worldWidth, worldHeight);

        URL bgUrl = getClass().getResource("/resources/imagen/fondoS2.png");
        if (bgUrl != null) {
            backgroundImage = new ImageIcon(bgUrl).getImage();
        } else {
            System.err.println("No se encontró el fondo en /resources/imagen/fondoS2.png");
        }

        backgroundSound = new BackgroundSound("/resources/sound/background/background2.wav");
        backgroundSound.play();

        portalNpc = new PortalNPC(5380, 290, 500, 500, "/resources/imagen/cave.png");

        URL instructionsUrl = getClass().getResource("/resources/imagen/overlays/instructions.png");
        if (instructionsUrl != null) {
            instructionsOverlayImg = new ImageIcon(instructionsUrl).getImage();
        }

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

        instructionStartTime = System.currentTimeMillis();
        timer = new Timer(16, this);
        timer.start();

        initRainParticles();
        footParticles = new ArrayList<>();
    }

    private void initRainParticles() {
        rainParticles = new ArrayList<>();
        for (int i = 0; i < numRaindrops; i++) {
            rainParticles.add(RainParticle.createRandom(worldWidth, worldHeight));
        }
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
        for (Particle p : footParticles) {
            float alpha = p.getAlpha();
            Color footColor = new Color((p.colorRGB >> 16) & 0xFF,
                    (p.colorRGB >> 8) & 0xFF,
                    p.colorRGB & 0xFF,
                    (int) (alpha * 255));
            g2d.setColor(footColor);
            g2d.fillOval((int) p.x, (int) p.y, (int) p.size, (int) p.size);
        }
        Image playerImg = player.getImage();
        if (playerImg != null) {
            g2d.drawImage(playerImg, player.getX(), player.getY(), player.getWidth(), player.getHeight(), this);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }
        portalNpc.draw(g2d);
        g2d.dispose();

        Graphics2D gLightning = (Graphics2D) g.create();
        gLightning.setTransform(new AffineTransform());
        if (currentLightning != null) {
            currentLightning.draw(gLightning);
        }
        gLightning.dispose();

        if (darknessLevel < maxDarkness) {
            darknessLevel += darknessStep;
        }
        Graphics2D gDark = (Graphics2D) g.create();
        gDark.setTransform(new AffineTransform());
        gDark.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, darknessLevel));
        gDark.setColor(Color.BLACK);
        gDark.fillRect(0, 0, virtualWidth, virtualHeight);
        gDark.dispose();

        Graphics2D gRain = (Graphics2D) g.create();
        gRain.translate(-camera.getOffsetX(), -camera.getOffsetY());
        gRain.setStroke(new BasicStroke(2));
        for (RainParticle drop : rainParticles) {
            float alpha = drop.getAlpha();
            int a = (int) (alpha * 255);
            Color startColor = new Color(255, 255, 255, a);
            Color endColor = new Color(0, 0, 139, a);
            int x1 = (int) drop.x;
            int y1 = (int) drop.y;
            int x2 = (int) (drop.x + drop.size * 2);
            int y2 = (int) (drop.y + drop.size * 4);
            GradientPaint gp = new GradientPaint(x1, y1, startColor, x2, y2, endColor);
            gRain.setPaint(gp);
            gRain.drawLine(x1, y1, x2, y2);
        }
        gRain.dispose();
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
        long currentTime = System.currentTimeMillis();

        // --- Gestión del sonido del rayo con fade-out progresivo ---
        if (!lightningSoundStarted && currentTime >= nextLightningTime) {
            if (lightningClip != null && lightningClip.isRunning()) {
                lightningClip.stop();
                try {
                    FloatControl gainControl = (FloatControl) lightningClip.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(0f);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            lightningSoundStarted = true;
            lightningClip.setFramePosition(0);
            try {
                FloatControl gainControl = (FloatControl) lightningClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(0f);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            lightningClip.start();
            lightningStartTime = currentTime;
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
                        float maxGain = 0f;
                        float newGain = maxGain + (minGain - maxGain) * fadeProgress;
                        gainControl.setValue(newGain);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        // --- Gestión del sistema de rayos ---
        if (currentLightning == null && currentTime >= nextLightningTime) {
            currentLightning = new Lightning(lightningDuration, virtualWidth, virtualHeight);
            nextLightningTime = currentTime + (5000 + (long) (Math.random() * 5000)); // intervalo aleatorio de 5 a 10 segundos
        }
        if (currentLightning != null && !currentLightning.isActive()) {
            currentLightning = null;
        }

        // --- Actualización del jugador y partículas ---
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
            Rectangle feet = player.getFeetRectangle();
            spawnFootParticles(feet);
        }
        for (int i = 0; i < rainParticles.size(); i++) {
            RainParticle drop = rainParticles.get(i);
            drop.update();
            if (drop.x > worldWidth || drop.y > worldHeight) {
                rainParticles.set(i, RainParticle.createRandom(worldWidth, worldHeight));
            }
        }

        // --- Detección de colisión con el NPC ---
        if (!transitionTriggered && player.getCollisionRectangle().intersects(portalNpc.getBounds())) {
            transitionTriggered = true;
            // Detener todos los sonidos del jugador para evitar que sigan sonando en la transición
            player.stopAllSounds();
            timer.stop();
            if (backgroundSound != null) {
                backgroundSound.stop();
            }
            if (levelTransitionListener != null) {
                levelTransitionListener.onLevelTransitionRequested();
            }
            return;
        }

        repaint();
    }

    private void spawnFootParticles(Rectangle feet) {
        int numParticles = 2;
        for (int i = 0; i < numParticles; i++) {
            float x = feet.x + (float) (Math.random() * feet.width);
            float y = feet.y + feet.height;
            float dx = (float) (Math.random() - 0.5);
            float dy = -(float) (Math.random() * 1 + 0.5f);
            float maxLife = 20;
            int colorRGB = 0x777777;
            float size = 5 + (float) (Math.random() * 3);
            footParticles.add(new Particle(x, y, dx, dy, maxLife, colorRGB, size));
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
            // Deshabilitar el salto en esta fase
            // case KeyEvent.VK_W:
            // case KeyEvent.VK_UP:
            //     player.jump();
            //     break;
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
