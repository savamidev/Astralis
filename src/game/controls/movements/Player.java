package game.controls.movements;

import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.net.URL;

/**
 * Representa al jugador y gestiona sus movimientos, animaciones y acciones (como salto y dash).
 */
public class Player {
    private enum State {IDLE, WALKING_LEFT, WALKING_RIGHT, JUMPING_LEFT, JUMPING_RIGHT, FALLING}
    private State currentState;
    private int x, y;
    private int width, height;
    private int dx, dy;
    private boolean jumping;
    private boolean wasInAir = false;

    private AnimationPlayer idleAnimation;
    private AnimationPlayer moveLeftAnimation;
    private AnimationPlayer moveRightAnimation;
    private AnimationPlayer jumpLeftAnimation;
    private AnimationPlayer jumpRightAnimation;
    private AnimationPlayer fallAnimation;
    private AnimationPlayer currentAnimation;

    private final int SPEED = 5;
    private final int JUMP_STRENGTH = -30; // negativo para subir
    private final double GRAVITY = 2;
    private final int TERMINAL_VELOCITY = 20;
    private int floorY;
    private int worldWidth;

    private PlayerState state;
    private int currentJumpCount;

    private static final int COUNT_IDLE = 19;
    private static final int COUNT_LEFT = 19;
    private static final int COUNT_RIGHT = 19;
    private static final int COUNT_JUMP = 15;
    private static final int COUNT_FALL = 19;
    private static final long FRAME_DELAY = 50;

    // Campos para el audio
    private Clip walkingClip;
    private Clip jumpClip;

    // Campos para dash (habilitado al tener la sandía)
    private boolean dashing = false;
    private long dashStartTime = 0;
    private final int DASH_DURATION = 200; // duración en milisegundos
    private final int DASH_SPEED = 20;     // velocidad del dash

    /**
     * Constructor que inicializa el jugador, sus animaciones y su posición inicial.
     * @param startX Posición inicial en X.
     * @param floorY Posición Y del suelo.
     * @param worldWidth Ancho del mundo.
     */
    public Player(int startX, int floorY, int worldWidth) {
        this.x = startX;
        this.floorY = floorY;
        this.worldWidth = worldWidth;
        this.dx = 0;
        this.dy = 0;
        this.jumping = false;
        this.currentState = State.IDLE;
        state = new PlayerState();
        currentJumpCount = 0;

        int newWidth = 150;
        int newHeight = 150;

        idleAnimation = new AnimationPlayer(loadAnimationImages("/resources/imagen/personajemove/2BlueWizardIdle/", COUNT_IDLE, newWidth, newHeight), FRAME_DELAY);
        moveLeftAnimation = new AnimationPlayer(loadAnimationImages("/resources/imagen/personajemove/2BlueWizardWalkLeft/", COUNT_LEFT, newWidth, newHeight), FRAME_DELAY);
        moveRightAnimation = new AnimationPlayer(loadAnimationImages("/resources/imagen/personajemove/2BlueWizardWalkRight/", COUNT_RIGHT, newWidth, newHeight), FRAME_DELAY);
        jumpLeftAnimation = new AnimationPlayer(loadAnimationImages("/resources/imagen/personajemove/2BlueWizardJumpLeft/", COUNT_JUMP, newWidth, newHeight), FRAME_DELAY);
        jumpRightAnimation = new AnimationPlayer(loadAnimationImages("/resources/imagen/personajemove/2BlueWizardJumpRight/", COUNT_JUMP, newWidth, newHeight), FRAME_DELAY);
        fallAnimation = new AnimationPlayer(loadAnimationImages("/resources/imagen/personajemove/2BlueWizardIdle/", COUNT_FALL, newWidth, newHeight), FRAME_DELAY);

        currentAnimation = idleAnimation;

        width = newWidth;
        height = newHeight;
        this.y = floorY - height;
    }

    private Image[] loadAnimationImages(String basePath, int count, int newWidth, int newHeight) {
        Image[] frames = new Image[count];
        for (int i = 1; i <= count; i++) {
            try {
                Image originalImage = new ImageIcon(getClass().getResource(basePath + i + ".png")).getImage();
                frames[i - 1] = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            } catch (Exception e) {
                System.err.println("Error al cargar imagen: " + basePath + i + ".png - " + e.getMessage());
                frames[i - 1] = null;
            }
        }
        return frames;
    }

    private void updateState() {
        if (jumping) {
            if (dx < 0) currentState = State.JUMPING_LEFT;
            else if (dx > 0) currentState = State.JUMPING_RIGHT;
        } else {
            if (dx < 0) currentState = State.WALKING_LEFT;
            else if (dx > 0) currentState = State.WALKING_RIGHT;
            else currentState = State.IDLE;
        }
        switch (currentState) {
            case WALKING_LEFT:
                currentAnimation = (moveLeftAnimation != null) ? moveLeftAnimation : idleAnimation;
                break;
            case WALKING_RIGHT:
                currentAnimation = (moveRightAnimation != null) ? moveRightAnimation : idleAnimation;
                break;
            case JUMPING_LEFT:
                currentAnimation = (jumpLeftAnimation != null) ? jumpLeftAnimation : idleAnimation;
                break;
            case JUMPING_RIGHT:
                currentAnimation = (jumpRightAnimation != null) ? jumpRightAnimation : idleAnimation;
                break;
            case FALLING:
                currentAnimation = (fallAnimation != null) ? fallAnimation : idleAnimation;
                break;
            case IDLE:
                currentAnimation = idleAnimation;
                break;
        }
    }

    // Método para cargar un Clip desde un recurso
    private Clip loadClip(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("No se encontró el archivo de audio: " + path);
                return null;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Métodos para manejar el sonido al caminar
    private void startWalkingSound() {
        if (walkingClip == null) {
            walkingClip = loadClip("/resources/sound/personaje/paso.wav");
        }
        if (walkingClip != null && !walkingClip.isActive()) {
            walkingClip.setFramePosition(0);
            walkingClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void stopWalkingSound() {
        if (walkingClip != null && walkingClip.isActive()) {
            walkingClip.stop();
        }
    }

    public void moveLeft() {
        dx = -SPEED;
        startWalkingSound();
    }

    public void moveRight() {
        dx = SPEED;
        startWalkingSound();
    }

    public void stop() {
        dx = 0;
        stopWalkingSound();
    }

    public void moveDown() { }
    public void stopDown() { }

    /**
     * Realiza un salto; si se posee la sandía se permite un doble salto.
     */
    public void jump() {
        int availableJumps = state.hasSandia() ? 2 : 1;
        if (currentJumpCount < availableJumps) {
            if (jumpClip == null) {
                jumpClip = loadClip("/resources/sound/personaje/jump.wav");
            }
            if (jumpClip != null && !jumpClip.isActive()) {
                jumpClip.setFramePosition(0);
                jumpClip.start();
            }
            dy = JUMP_STRENGTH;
            jumping = true;
            currentJumpCount++;
            wasInAir = true;
        }
    }

    // Método auxiliar para reproducir el sonido de aterrizaje (one-shot)
    private void playLandingSound() {
        try {
            URL url = getClass().getResource("/resources/sound/personaje/landing.wav");
            if (url == null) {
                System.err.println("No se encontró el archivo landing.wav");
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            Clip landingClip = AudioSystem.getClip();
            landingClip.open(audioStream);
            landingClip.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza el estado, posición y animación del jugador, así como el dash si está activo.
     */
    public void update() {
        updateState();
        currentAnimation.update();

        x += dx;
        y += dy;

        dy += GRAVITY;
        if (dy > TERMINAL_VELOCITY)
            dy = TERMINAL_VELOCITY;

        if (y + height >= floorY) {
            if (wasInAir) {
                playLandingSound();
                wasInAir = false;
            }
            y = floorY - height;
            dy = 0;
            jumping = false;
            currentJumpCount = 0;
        }

        // Procesar dash: si ha pasado la duración, se termina el dash
        if (dashing && System.currentTimeMillis() - dashStartTime >= DASH_DURATION) {
            dashing = false;
            dx = 0;
            System.out.println("Dash finalizado.");
        }

        x = Math.max(0, Math.min(x, worldWidth - width));
    }

    /**
     * Ejecuta un dash horizontal si el jugador tiene la sandía.
     */
    public void dash() {
        if (!state.hasSandia()) {
            System.out.println("Dash no activado: no se tiene la sandía.");
            return;
        }
        if (!dashing) {
            dashing = true;
            dashStartTime = System.currentTimeMillis();
            if (dx == 0) {
                dx = DASH_SPEED;
            } else {
                dx = (dx > 0) ? DASH_SPEED : -DASH_SPEED;
            }
            System.out.println("Dash activado.");
        }
    }

    public Rectangle getCollisionRectangle() {
        int hitboxWidth = width / 3;
        int hitboxHeight = height - 10;
        int hitboxX = x + (width - hitboxWidth) / 2;
        int hitboxY = y + (height - hitboxHeight) / 2;
        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    public Rectangle getFeetRectangle() {
        int feetWidth = width / 2;
        int feetHeight = 5;
        int feetX = x + (width - feetWidth) / 2;
        int feetY = y + height - feetHeight;
        return new Rectangle(feetX, feetY, feetWidth, feetHeight);
    }

    /**
     * Devuelve el área de colisión de la cabeza del jugador.
     * @return Rectángulo correspondiente.
     */
    public Rectangle getHeadRectangle() {
        int headWidth = width / 2;
        int headHeight = 5;
        int headX = x + (width - headWidth) / 2;
        int headY = y;
        return new Rectangle(headX, headY, headWidth, headHeight);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public Image getImage() {
        return (currentAnimation != null) ? currentAnimation.getCurrentFrame() : null;
    }
    public PlayerState getPlayerState() { return state; }
    public int getDy() { return dy; }
    public void resetVerticalMotion() {
        dy = 0;
        jumping = false;
        currentJumpCount = 0;
    }
    public int getDx() {
        return dx;
    }
}
