package game.controls.movements;

import game.objects.FinalNPC;
import game.objects.Stalactite;
import game.panlesBBDD.map.map1.DeathStyledDialog;
import game.video.TransitionVideoPanel;
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
    private Camera camera; // Cámara para seguir al jugador

    // Dimensiones del mundo y pantalla visible
    private int worldWidth = 3840;
    private int worldHeight = 1080;
    private int screenWidth = 1920;
    private int screenHeight = 1080;

    // Parámetros similares a GamePanelLevel2
    private final int floorY = 850;
    private final int initialStartY = 850;
    private final boolean debugMode = false;

    // Efecto de partículas al andar (foot particles)
    private List<game.effects.Particle> footParticles;

    // Nuevos objetos: NPC final y estalactita
    private FinalNPC finalNPC;
    private Stalactite stalactite;

    public GamePanelLevel3() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setOpaque(false);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        // Inicializar el jugador y forzar habilidades extra
        player = new Player(200, initialStartY, worldWidth);
        player.getPlayerState().setSandia(true);
        player.applyBoots();

        camera = new Camera(player, screenWidth, screenHeight, worldWidth, worldHeight);

        URL bgUrl = getClass().getResource("/resources/imagen/Mapa3.gif");
        if (bgUrl != null) {
            backgroundImage = new ImageIcon(bgUrl).getImage();
        } else {
            backgroundImage = null;
        }

        // Instanciar el NPC final y la estalactita (rutas y posiciones ajustadas)
        finalNPC = new FinalNPC(3170, 490, 550, 550, "/resources/imagen/Astralis.png");
        stalactite = new Stalactite(2500, 0, 50, 150, "/resources/imagen/cave.png");

        footParticles = new ArrayList<>();

        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.update();

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

        stalactite.update(player.getCollisionRectangle());
        if (stalactite.checkCollision(player.getCollisionRectangle())) {
            triggerDeath();
        }

        if (player.getCollisionRectangle().intersects(finalNPC.getBounds())) {
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

        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, worldWidth, worldHeight, this);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, worldWidth, worldHeight);
        }

        Image playerImg = player.getImage();
        if (playerImg != null) {
            g2.drawImage(playerImg, player.getX(), player.getY(), player.getWidth(), player.getHeight(), this);
        } else {
            g2.setColor(Color.RED);
            g2.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }

        for (game.effects.Particle p : footParticles) {
            float alpha = p.getAlpha();
            Color footColor = new Color((p.colorRGB >> 16) & 0xFF,
                    (p.colorRGB >> 8) & 0xFF,
                    p.colorRGB & 0xFF,
                    (int)(alpha * 255));
            g2.setColor(footColor);
            g2.fillOval((int)p.x, (int)p.y, (int)p.size, (int)p.size);
        }

        finalNPC.draw(g2);
        stalactite.draw(g2);

        g2.dispose();
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
            footParticles.add(new game.effects.Particle(x, y, dx, dy, maxLife, colorRGB, size));
        }
    }

    private void triggerFinalTransition() {
        System.out.println("NPC final colisionado: iniciando transición final con video...");
        timer.stop();
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
        System.out.println("El jugador ha sido alcanzado por la estalactita y muere.");
        timer.stop();
        player.setAlive(false);
        player.stop();
        player.stopWalkingSound();
        player.stopAllSounds();
        stalactite.stopFallSound();
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        DeathStyledDialog dsd = new DeathStyledDialog(owner, () -> {
            player.setPosition(200, initialStartY - player.getHeight());
            player.getPlayerState().reset();
            player.setAlive(true);
            stalactite.reset();
            timer.start();
            System.out.println("Jugador reubicado y estalactita reiniciada tras la muerte.");
        });
        dsd.showDialog();
    }


    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch(key) {
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
        switch(key) {
            case KeyEvent.VK_A, KeyEvent.VK_LEFT, KeyEvent.VK_D, KeyEvent.VK_RIGHT -> player.stop();
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> player.stopDown();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }
}
