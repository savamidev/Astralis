package game.controls.movements;

import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.net.URL;

/**
 * Representa al jugador y gestiona sus movimientos, animaciones y acciones.
 * <p>
 * La clase administra estados internos como el movimiento, salto, dash, reproducción de sonidos y animaciones.
 * </p>
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

    private final int SPEED = 7;  // Velocidad base
    private double speedMultiplier = 1.0; // Multiplicador de velocidad (1.0 por defecto)
    private final int JUMP_STRENGTH = -25;
    private final double GRAVITY = 2;
    private final int TERMINAL_VELOCITY = 20;
    private int floorY;  // Suelo fijo (puede usarse cuando no se detecta colisión con tiles)
    private int worldWidth;

    private PlayerState state;
    private int currentJumpCount;

    // Constantes para la cantidad de frames en cada animación.
    private static final int COUNT_IDLE = 4;
    private static final int COUNT_LEFT = 6;
    private static final int COUNT_RIGHT = 6;
    private static final int COUNT_JUMP = 6;
    private static final int COUNT_FALL = 4;
    private static final long FRAME_DELAY = 300;

    // Campos para el audio.
    private Clip walkingClip;
    private Clip jumpClip;

    // Campos para dash.
    private boolean dashing = false;
    private long dashStartTime = 0;
    private final int DASH_DURATION = 200;
    private final int DASH_SPEED = 20;

    /**
     * Crea una instancia de Player con la posición inicial, el nivel del suelo y el ancho del mundo.
     *
     * @param startX     Posición inicial en X.
     * @param floorY     Coordenada Y del suelo.
     * @param worldWidth Ancho total del mundo del juego.
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

    /**
     * Carga un conjunto de imágenes para la animación a partir de una ruta base.
     *
     * @param basePath Ruta base donde se encuentran las imágenes.
     * @param count    Número de imágenes a cargar.
     * @param newWidth Ancho deseado para escalar las imágenes.
     * @param newHeight Alto deseado para escalar las imágenes.
     * @return Un array de {@link Image} con los frames de la animación.
     */
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

    /**
     * Actualiza el estado actual del jugador y selecciona la animación correspondiente en función del movimiento.
     */
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

    /**
     * Carga un clip de audio desde un recurso.
     *
     * @param path Ruta del recurso de audio.
     * @return Un objeto {@link Clip} con el audio cargado, o {@code null} si ocurre un error.
     */
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

    /**
     * Inicia la reproducción del sonido de caminar en bucle.
     * Solo se inicia si el jugador no está en el aire.
     */
    private void startWalkingSound() {
        if (walkingClip == null) {
            walkingClip = loadClip("/resources/sound/personaje/paso.wav");
        }
        if (walkingClip != null && !walkingClip.isActive()) {
            walkingClip.setFramePosition(0);
            walkingClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * Detiene la reproducción del sonido de caminar.
     */
    public void stopWalkingSound() {
        if (walkingClip != null && walkingClip.isActive()) {
            walkingClip.stop();
        }
    }

    /**
     * Detiene todos los clips de audio asociados al jugador.
     * Esto incluye el sonido de caminar y el de salto, y cierra los clips para que no sigan sonando.
     */
    public void stopAllSounds() {
        stopWalkingSound();
        if (jumpClip != null && jumpClip.isRunning()) {
            jumpClip.stop();
        }
        if (walkingClip != null) {
            walkingClip.close();
        }
        if (jumpClip != null) {
            jumpClip.close();
        }
    }

    /**
     * Indica si el jugador está actualmente saltando.
     *
     * @return {@code true} si el jugador está en salto; {@code false} en caso contrario.
     */
    public boolean isJumping() {
        return jumping;
    }

    /**
     * Mueve al jugador hacia la izquierda y reproduce el sonido de caminar si no está en el aire.
     */
    public void moveLeft() {
        dx = -(int)(SPEED * speedMultiplier);
        if (!jumping) {
            startWalkingSound();
        }
    }

    /**
     * Mueve al jugador hacia la derecha y reproduce el sonido de caminar si no está en el aire.
     */
    public void moveRight() {
        dx = (int)(SPEED * speedMultiplier);
        if (!jumping) {
            startWalkingSound();
        }
    }

    /**
     * Detiene el movimiento horizontal y el sonido de caminar.
     */
    public void stop() {
        dx = 0;
        stopWalkingSound();
    }

    public void moveDown() { }

    public void stopDown() { }

    /**
     * Realiza un salto; permite doble salto si el jugador posee la sandía.
     * Se detiene el sonido de caminar antes de iniciar el salto.
     */
    public void jump() {
        int availableJumps = state.hasSandia() ? 2 : 1;
        if (currentJumpCount < availableJumps) {
            stopWalkingSound();
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

    /**
     * Reproduce el sonido de aterrizaje al finalizar el salto.
     */
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
     * Actualiza la posición, el estado y la animación del jugador.
     * Aplica la gravedad, controla el salto y restablece la posición si alcanza el suelo.
     * Este método sigue utilizando la condición de posición (y + height >= floorY)
     * para casos de suelo fijo, pero en terrenos definidos por colisiones (por ejemplo, tile 1)
     * se puede invocar el método onLanding() desde GamePanel.
     */
    public void update() {
        updateState();
        currentAnimation.update();

        x += dx;
        y += dy;

        dy += GRAVITY;
        if (dy > TERMINAL_VELOCITY)
            dy = TERMINAL_VELOCITY;

        // Si el jugador está saltando, se detiene el sonido de caminar.
        if (jumping) {
            stopWalkingSound();
        }

        // Si el jugador alcanza o supera el suelo fijo
        if (y + height >= floorY) {
            if (wasInAir) {
                onLanding();
            }
            y = floorY - height;
            dy = 0;
            jumping = false;
            currentJumpCount = 0;
        }

        if (dashing && System.currentTimeMillis() - dashStartTime >= DASH_DURATION) {
            dashing = false;
            dx = 0;
            System.out.println("Dash finalizado.");
        }

        x = Math.max(0, Math.min(x, worldWidth - width));
    }

    /**
     * Ejecuta un dash horizontal si el jugador posee la sandía.
     * Durante el dash se incrementa la velocidad horizontal.
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

    /**
     * Método público que debe llamarse cuando se detecta que el jugador aterriza sobre un terreno,
     * ya sea por comparación de posición o mediante colisión (por ejemplo, tile de valor 1).
     * Reproduce el sonido de aterrizaje y, si el jugador sigue moviéndose horizontalmente, reinicia el sonido de caminar.
     */
    public void onLanding() {
        if (wasInAir) {
            playLandingSound();
            wasInAir = false;
        }
        if (dx != 0) {
            startWalkingSound();
        }
    }

    /**
     * Obtiene el área de colisión del jugador (para colisiones laterales).
     *
     * @return Un objeto Rectangle que representa la zona de colisión.
     */
    public Rectangle getCollisionRectangle() {
        int hitboxWidth = width / 3;
        int hitboxHeight = height - 40;
        int hitboxX = x + (width - hitboxWidth) / 2;
        int hitboxY = y + 40;
        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    /**
     * Obtiene el área de colisión de los pies del jugador (para detectar aterrizajes).
     *
     * @return Un objeto Rectangle que representa la zona de colisión de los pies.
     */
    public Rectangle getFeetRectangle() {
        int feetWidth = width / 2;
        int feetHeight = 5;
        int feetX = x + (width - feetWidth) / 2;
        int feetY = y + height - feetHeight;
        return new Rectangle(feetX, feetY, feetWidth, feetHeight);
    }

    /**
     * Obtiene el área de colisión de la cabeza del jugador.
     *
     * @return Un objeto Rectangle que representa la zona de colisión de la cabeza.
     */
    public Rectangle getHeadRectangle() {
        int headWidth = width / 4;
        int headHeight = 1;
        int headX = x + (width - headWidth) / 2;
        int headY = y + 20;
        return new Rectangle(headX, headY, headWidth, headHeight);
    }

    /**
     * Retorna el ancho del jugador.
     *
     * @return El ancho en píxeles.
     */
    public int getWidth() { return width; }

    /**
     * Retorna el alto del jugador.
     *
     * @return El alto en píxeles.
     */
    public int getHeight() { return height; }

    /**
     * Establece la posición del jugador.
     *
     * @param newX Nueva posición en X.
     * @param newY Nueva posición en Y.
     */
    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    /**
     * Retorna la posición X actual del jugador.
     *
     * @return La coordenada X.
     */
    public int getX() { return x; }

    /**
     * Retorna la posición Y actual del jugador.
     *
     * @return La coordenada Y.
     */
    public int getY() { return y; }

    /**
     * Retorna la imagen actual del jugador según la animación en curso.
     *
     * @return La imagen del frame actual.
     */
    public Image getImage() {
        return (currentAnimation != null) ? currentAnimation.getCurrentFrame() : null;
    }

    /**
     * Retorna el estado actual del jugador.
     *
     * @return Una instancia de {@link PlayerState} con la información del estado.
     */
    public PlayerState getPlayerState() { return state; }

    /**
     * Retorna la velocidad vertical actual.
     *
     * @return La velocidad vertical (dy).
     */
    public int getDy() { return dy; }

    /**
     * Reinicia el movimiento vertical, deteniendo el salto.
     */
    public void resetVerticalMotion() {
        dy = 0;
        jumping = false;
        currentJumpCount = 0;
    }

    /**
     * Retorna la velocidad horizontal actual.
     *
     * @return La velocidad horizontal (dx).
     */
    public int getDx() {
        return dx;
    }

    /**
     * Aplica el efecto de las botas: aumenta la velocidad del jugador.
     * En este caso, incrementa el multiplicador de velocidad.
     */
    public void applyBoots() {
        speedMultiplier = 1.5;
    }
}
