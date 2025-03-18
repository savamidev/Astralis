package game.controls.movements;

import game.audio.BackgroundSound;
import game.effects.FogParticleSystem;
import game.objects.FinalNPC;
import game.objects.Stalactite;
import game.panlesBBDD.map.map1.DeathStyledDialog;
import game.panlesBBDD.map.map1.TileMap;
import game.panlesBBDD.map.map1.CollisionManager;
import game.video.TransitionVideoPanel;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GamePanelLevel3 extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Player player;
    private Image backgroundImage;
    private Camera camera;

    // Dimensiones del mundo y pantalla visible
    private int worldWidth = 3840;
    private int worldHeight = 1080;
    private int screenWidth = 1920;
    private int screenHeight = 1080;

    // Parámetros del nivel
    private final int floorY = 850;
    private final int initialStartY = 850;
    private final boolean debugMode = false;

    // Sistema de colisiones
    private TileMap tileMap;
    private CollisionManager collisionManager;

    // Efecto de partículas al andar
    private List<game.effects.Particle> footParticles;

    // Sistema de partículas de niebla
    private FogParticleSystem fogSystem;

    // Objetos adicionales
    private FinalNPC finalNPC;
    private List<Stalactite> stalactites;

    // Audio de fondo
    private BackgroundSound backgroundSound;

    // Offset para “bajar” el área de colisión
    private final int collisionOffset = 20;

    public GamePanelLevel3() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setOpaque(false);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        // Inicializar jugador y sus habilidades
        player = new Player(200, initialStartY, worldWidth);
        player.getPlayerState().setSandia(true);
        player.applyBoots();

        camera = new Camera(player, screenWidth, screenHeight, worldWidth, worldHeight);

        // Cargar fondo
        URL bgUrl = getClass().getResource("/resources/imagen/Mapa3.png");
        if (bgUrl != null) {
            backgroundImage = new ImageIcon(bgUrl).getImage();
        } else {
            backgroundImage = null;
        }

        // Inicializar audio de fondo
        backgroundSound = new BackgroundSound("/resources/sound/background/background3.wav");
        try {
            backgroundSound.play();
        } catch(Exception ex) {
            System.err.println("Error al reproducir audio de fondo: " + ex.getMessage());
            try {
                java.lang.reflect.Field clipField = backgroundSound.getClass().getDeclaredField("clip");
                clipField.setAccessible(true);
                Clip clip = (Clip) clipField.get(backgroundSound);
                if (clip != null) {
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                } else {
                    System.err.println("Fallback error: clip es null.");
                }
            } catch(Exception e2) {
                System.err.println("Error en fallback del audio: " + e2.getMessage());
            }
        }

        // Inicializar sistema de colisiones
        int tileSize = 40;
        tileMap = new TileMap("/resources/Map03.csv", tileSize);
        collisionManager = new CollisionManager(tileMap);

        // Inicializar NPC final
        finalNPC = new FinalNPC(3170, 490, 550, 550, "/resources/imagen/Astralis.png");

        // Inicializar lista de stalactitas
        stalactites = new ArrayList<>();
        stalactites.add(new Stalactite(2500, -40, 50, 150, "/resources/imagen/estalactita.png"));
        stalactites.add(new Stalactite(1100, -40, 50, 150, "/resources/imagen/estalactita.png"));
        stalactites.add(new Stalactite(600, -40, 50, 150, "/resources/imagen/estalactita.png"));

        footParticles = new ArrayList<>();

        // Configurar el sistema de niebla para cubrir el 100% del mapa:
        fogSystem = new FogParticleSystem(0, 0, worldHeight);
        fogSystem.setSpawnWidth(worldWidth);

        timer = new Timer(16, this);
        timer.start();
    }

    // Retorna el rectángulo de colisión ajustado
    private Rectangle getAdjustedCollisionRectangle() {
        Rectangle r = player.getCollisionRectangle();
        return new Rectangle(r.x, r.y + collisionOffset, r.width, r.height - collisionOffset);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int tileSize = tileMap.getTileSize();
        int oldX = player.getX();
        int oldY = player.getY();

        // Actualizar movimiento del jugador
        player.update();
        int newX = player.getX();
        int newY = player.getY();

        // Procesar colisión horizontal
        player.setPosition(newX, oldY);
        if (collisionManager.isColliding(getAdjustedCollisionRectangle())) {
            player.setPosition(oldX, oldY);
            player.stop();
            newX = oldX;
        }

        // Procesar colisión vertical
        player.setPosition(newX, newY);
        if (collisionManager.isColliding(getAdjustedCollisionRectangle())) {
            if (player.getDy() < 0) { // Colisión en la cabeza
                Rectangle headRect = player.getHeadRectangle();
                int collisionRow = headRect.y / tileSize;
                int correctedY = (collisionRow + 1) * tileSize;
                player.setPosition(newX, correctedY);
            } else { // Colisión en los pies
                Rectangle feetRect = player.getFeetRectangle();
                int collisionRow = (feetRect.y + feetRect.height) / tileSize;
                int correctedY = collisionRow * tileSize - player.getHeight();
                player.setPosition(newX, correctedY);
                player.onLanding(); // Se llama onLanding para reproducir el sonido de pisadas al aterrizar
            }
            player.resetVerticalMotion();
        }

        // Comprobar colisión letal (tiles tipo 2)
        Rectangle playerRect = getAdjustedCollisionRectangle();
        int startCol = playerRect.x / tileSize;
        int endCol = (playerRect.x + playerRect.width) / tileSize;
        int startRow = playerRect.y / tileSize;
        int endRow = (playerRect.y + playerRect.height) / tileSize;
        boolean collisionWithType2 = false;
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                if (tileMap.getTileAt(col * tileSize, row * tileSize) == 2) {
                    collisionWithType2 = true;
                    break;
                }
            }
        }
        if (collisionWithType2) {
            triggerDeath();
            return;
        }

        // Actualizar partículas de pisada
        for (int i = 0; i < footParticles.size(); i++) {
            game.effects.Particle p = footParticles.get(i);
            p.update();
            if (!p.isAlive()) {
                footParticles.remove(i);
                i--;
            }
        }
        if (player.getDx() != 0 && !player.isJumping()) {
            spawnFootParticles(player.getFeetRectangle());
        }

        // Actualizar stalactitas
        for (Stalactite s : stalactites) {
            s.update(getAdjustedCollisionRectangle());
            if (s.checkCollision(getAdjustedCollisionRectangle())) {
                triggerDeath();
                return;
            }
        }

        // Actualizar el sistema de niebla (cubre todo el mapa)
        fogSystem.update(player);

        // Comprobar colisión con el NPC final
        if (getAdjustedCollisionRectangle().intersects(finalNPC.getBounds())) {
            triggerFinalTransition();
        }

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        camera.setScreenSize(getWidth(), getHeight());
        int camOffsetX = camera.getOffsetX();
        int camOffsetY = camera.getOffsetY();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(-camOffsetX, -camOffsetY);

        // Dibujar fondo
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, worldWidth, worldHeight, this);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, worldWidth, worldHeight);
        }

        // (Opcional debug) Dibujar mapa de colisiones
        if (debugMode && tileMap != null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            drawTileMap(g2);
            g2.setComposite(originalComposite);
        }

        // Dibujar jugador
        Image playerImg = player.getImage();
        if (playerImg != null) {
            g2.drawImage(playerImg, player.getX(), player.getY(), player.getWidth(), player.getHeight(), this);
        } else {
            g2.setColor(Color.RED);
            g2.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }

        // (Opcional debug) Dibujar área de colisión ajustada
        if (debugMode) {
            Rectangle adjustedRect = getAdjustedCollisionRectangle();
            g2.setColor(Color.RED);
            g2.draw(adjustedRect);
            g2.setColor(Color.BLUE);
            g2.draw(player.getHeadRectangle());
            g2.setColor(Color.GREEN);
            g2.draw(player.getFeetRectangle());
        }

        // Dibujar partículas de pisada
        for (game.effects.Particle p : footParticles) {
            float alpha = p.getAlpha();
            Color footColor = new Color((p.colorRGB >> 16) & 0xFF,
                    (p.colorRGB >> 8) & 0xFF,
                    p.colorRGB & 0xFF,
                    (int)(alpha * 255));
            g2.setColor(footColor);
            g2.fillOval((int)p.x, (int)p.y, (int)p.size, (int)p.size);
        }

        // Dibujar NPC final
        finalNPC.draw(g2);
        // Dibujar stalactitas
        for (Stalactite s : stalactites) {
            s.draw(g2);
        }
        // Dibujar el sistema de niebla
        fogSystem.draw(g2);

        g2.dispose();
    }

    // Método para dibujar el mapa de colisiones (debug)
    private void drawTileMap(Graphics2D g2d) {
        int tileSize = tileMap.getTileSize();
        int[][] tiles = tileMap.getTiles();
        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles[row].length; col++) {
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

    private void spawnFootParticles(Rectangle feet) {
        int numParticles = 2;
        for (int i = 0; i < numParticles; i++) {
            float x = feet.x + (float)(Math.random() * feet.width);
            float y = feet.y + feet.height;
            float dx = (float)(Math.random() - 0.5);
            float dy = -(float)(Math.random() * 1 + 0.5f);
            float life = 20;
            int colorRGB = 0x777777;
            float size = 5 + (float)(Math.random() * 3);
            footParticles.add(new game.effects.Particle(x, y, dx, dy, life, colorRGB, size));
        }
    }

    private void triggerFinalTransition() {
        System.out.println("NPC final colisionado: iniciando transición final con video...");

        // Eliminar el KeyListener para evitar nuevos eventos
        removeKeyListener(this);

        // Detener timer y sonidos, y deshabilitar la reproducción de pisadas
        timer.stop();
        player.disableFootstepSound();
        player.setAlive(false);
        player.stopAllSounds();
        try {
            if (backgroundSound != null) {
                backgroundSound.stop();
            }
        } catch(Exception ex) {
            System.err.println("Error al detener el audio de fondo: " + ex.getMessage());
        }

        TransitionVideoPanel videoPanel = new TransitionVideoPanel(() -> {
            System.out.println("Video final completado. Finalizando juego...");
            System.exit(0);
        });
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(videoPanel);
        frame.revalidate();
        videoPanel.requestFocusInWindow();
    }

    private void triggerDeath() {
        System.out.println("El jugador ha muerto por colisión letal.");
        timer.stop();
        try {
            if (backgroundSound != null) {
                backgroundSound.stop();
            }
        } catch(Exception ex) {
            System.err.println("Error al detener el audio de fondo: " + ex.getMessage());
        }
        player.setAlive(false);
        player.stop();
        player.stopWalkingSound();
        player.stopAllSounds();
        for (Stalactite s : stalactites) {
            s.reset();
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        DeathStyledDialog dsd = new DeathStyledDialog(owner, () -> {
            player.setPosition(200, initialStartY - player.getHeight());
            player.getPlayerState().reset();
            player.getPlayerState().setSandia(true);
            player.applyBoots();
            player.setAlive(true);
            for (Stalactite s : stalactites) {
                s.reset();
            }
            try {
                if (backgroundSound != null) {
                    backgroundSound.play();
                }
            } catch(Exception ex) {
                System.err.println("Error al reproducir audio de fondo: " + ex.getMessage());
            }
            timer.start();
            System.out.println("Jugador reubicado tras la muerte.");
        });
        dsd.showDialog();
    }

    @Override
    public void keyPressed(KeyEvent e) {
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
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_A, KeyEvent.VK_LEFT, KeyEvent.VK_D, KeyEvent.VK_RIGHT -> player.stop();
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> player.stopDown();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }
}
