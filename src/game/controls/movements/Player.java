package game.controls.movements;

import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.net.URL;

/**
 * Representa al jugador en el juego, gestionando sus movimientos, animaciones,
 * sonidos y colisiones, así como el estado interno durante la partida.
 */
public class Player {

    private enum State {IDLE, WALKING_LEFT, WALKING_RIGHT, JUMPING_LEFT, JUMPING_RIGHT, FALLING}
    private State currentState;
    private int x, y;
    private int width, height;
    private int dx, dy;
    private boolean jumping;
    private boolean wasInAir = false;

    // Variable que indica si el jugador está "vivo"
    private boolean alive = true;

    private AnimationPlayer idleAnimation;
    private AnimationPlayer moveLeftAnimation;
    private AnimationPlayer moveRightAnimation;
    private AnimationPlayer jumpLeftAnimation;
    private AnimationPlayer jumpRightAnimation;
    private AnimationPlayer fallAnimation;
    private AnimationPlayer currentAnimation;

    private final int SPEED = 7;  // Velocidad base
    private double speedMultiplier = 1.0; // Multiplicador de velocidad (por defecto 1.0)
    private final int JUMP_STRENGTH = -25;
    private final double GRAVITY = 2;
    private final int TERMINAL_VELOCITY = 20;
    private int floorY;  // Suelo fijo
    private int worldWidth;

    private PlayerState state;
    private int currentJumpCount;

    // Campos para audio
    private Clip walkingClip;
    private Clip jumpClip;

    // Dash
    private boolean dashing = false;
    private long dashStartTime = 0;
    private final int DASH_DURATION = 200;
    private final int DASH_SPEED = 20;

    // Bandera para controlar la reproducción del sonido de pisadas
    private boolean footstepSoundEnabled = true;

    // Constantes para frames de animación
    private static final int COUNT_IDLE = 4;
    private static final int COUNT_LEFT = 6;
    private static final int COUNT_RIGHT = 6;
    private static final int COUNT_JUMP = 6;
    private static final int COUNT_FALL = 4;
    private static final long FRAME_DELAY = 300;

    /**
     * Crea una instancia de Player con la posición inicial, el nivel del suelo y el ancho del mundo.
     *
     * @param startX    Posición inicial en X del jugador.
     * @param floorY    Coordenada Y del suelo.
     * @param worldWidth Ancho total del mundo.
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
     * Carga y escala las imágenes para una animación a partir de una ruta base.
     *
     * @param basePath Ruta base de los archivos de imagen.
     * @param count    Número de imágenes (frames) a cargar.
     * @param newWidth Ancho al que se escalarán las imágenes.
     * @param newHeight Alto al que se escalarán las imágenes.
     * @return Array de imágenes escaladas.
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
     * Actualiza el estado interno del jugador y selecciona la animación adecuada
     * según la dirección y acción (caminar, saltar, caer, etc.).
     */
    private void updateState() {
        if (jumping) {
            if (dx < 0)
                currentState = State.JUMPING_LEFT;
            else if (dx > 0)
                currentState = State.JUMPING_RIGHT;
        } else {
            if (dx < 0)
                currentState = State.WALKING_LEFT;
            else if (dx > 0)
                currentState = State.WALKING_RIGHT;
            else
                currentState = State.IDLE;
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
     * Carga un clip de audio desde el recurso especificado.
     *
     * @param path Ruta del recurso de audio.
     * @return El clip de audio cargado, o {@code null} si ocurre un error.
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
     * Inicia la reproducción del sonido de pisadas si el jugador está vivo y el sonido está habilitado.
     */
    public void startWalkingSound() {
        if (!alive || !footstepSoundEnabled) return;
        if (walkingClip == null) {
            walkingClip = loadClip("/resources/sound/personaje/paso.wav");
        }
        if (walkingClip != null && !walkingClip.isActive()) {
            walkingClip.setFramePosition(0);
            walkingClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * Detiene y cierra el clip de audio de pisadas.
     */
    public void stopWalkingSound() {
        if (walkingClip != null) {
            if (walkingClip.isActive()) {
                walkingClip.stop();
            }
            walkingClip.flush();
            walkingClip.close();
            walkingClip = null;
        }
    }

    /**
     * Detiene todos los sonidos asociados al jugador, incluyendo el de salto y pisadas.
     */
    public void stopAllSounds() {
        stopWalkingSound();
        if (jumpClip != null && jumpClip.isRunning()) {
            jumpClip.stop();
            jumpClip.flush();
            jumpClip.close();
            jumpClip = null;
        }
    }

    /**
     * Deshabilita el sonido de pisadas, útil para gestionar transiciones en el juego.
     */
    public void disableFootstepSound() {
        footstepSoundEnabled = false;
        stopWalkingSound();
    }

    /**
     * Indica si el jugador se encuentra en estado de salto.
     *
     * @return {@code true} si el jugador está saltando; {@code false} en caso contrario.
     */
    public boolean isJumping() {
        return jumping;
    }

    /**
     * Mueve al jugador hacia la izquierda y activa el sonido de pisadas si no está saltando.
     */
    public void moveLeft() {
        if (!alive) return;
        dx = -(int)(SPEED * speedMultiplier);
        if (!jumping) {
            startWalkingSound();
        }
    }

    /**
     * Mueve al jugador hacia la derecha y activa el sonido de pisadas si no está saltando.
     */
    public void moveRight() {
        if (!alive) return;
        dx = (int)(SPEED * speedMultiplier);
        if (!jumping) {
            startWalkingSound();
        }
    }

    /**
     * Detiene el movimiento horizontal del jugador y detiene el sonido de pisadas.
     */
    public void stop() {
        dx = 0;
        stopWalkingSound();
    }

    /**
     * Método sin implementación para mover al jugador hacia abajo.
     */
    public void moveDown() { }

    /**
     * Método sin implementación para detener el movimiento hacia abajo.
     */
    public void stopDown() { }

    /**
     * Realiza un salto si el jugador está vivo y tiene saltos disponibles.
     * Reproduce el sonido de salto y actualiza el estado físico.
     */
    public void jump() {
        if (!alive) return;
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
     * Reproduce el sonido de aterrizaje y actualiza el estado interno al aterrizar.
     */
    private void playLandingSound() {
        try {
            URL url = getClass().getResource("/resources/sound/personaje/landing.wav");
            if (url == null) {
                System.err.println("No se encontró landing.wav");
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
     * Actualiza la posición, animaciones, física y estados del jugador.
     * Gestiona la gravedad, colisiones con el suelo y finalización de dash.
     */
    public void update() {
        if (!alive) return;
        updateState();
        currentAnimation.update();

        x += dx;
        y += dy;

        dy += GRAVITY;
        if (dy > TERMINAL_VELOCITY)
            dy = TERMINAL_VELOCITY;

        if (jumping) {
            stopWalkingSound();
        }

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
     * Realiza un movimiento rápido (dash) si el jugador está vivo y posee la sandía.
     * El dash se activa y finaliza en función de la duración definida.
     */
    public void dash() {
        if (!alive) return;
        if (!state.hasSandia()) {
            System.out.println("Dash no activado: no se tiene sandía.");
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
     * Gestiona las acciones al aterrizar, reproduciendo el sonido correspondiente y reactivando el sonido de pisadas.
     */
    public void onLanding() {
        if (wasInAir) {
            playLandingSound();
            wasInAir = false;
        }
        if (dx != 0 && alive && footstepSoundEnabled) {
            startWalkingSound();
        }
    }

    /**
     * Retorna el rectángulo de colisión del jugador para detectar colisiones con otros objetos.
     *
     * @return Un objeto Rectangle que representa la hitbox del jugador.
     */
    public Rectangle getCollisionRectangle() {
        int hitboxWidth = width / 3;
        int hitboxHeight = height - 40;
        int hitboxX = x + (width - hitboxWidth) / 2;
        int hitboxY = y + 40;
        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    /**
     * Retorna el rectángulo correspondiente a la zona de los pies del jugador,
     * utilizado para detectar colisiones con el suelo.
     *
     * @return Un objeto Rectangle representando la zona de los pies.
     */
    public Rectangle getFeetRectangle() {
        int feetWidth = width / 2;
        int feetHeight = 5;
        int feetX = x + (width - feetWidth) / 2;
        int feetY = y + height - feetHeight;
        return new Rectangle(feetX, feetY, feetWidth, feetHeight);
    }

    /**
     * Retorna el rectángulo que representa la zona de la cabeza del jugador,
     * utilizado para detectar colisiones superiores.
     *
     * @return Un objeto Rectangle correspondiente a la cabeza.
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
     * @return Ancho en píxeles.
     */
    public int getWidth() { return width; }

    /**
     * Retorna el alto del jugador.
     *
     * @return Alto en píxeles.
     */
    public int getHeight() { return height; }

    /**
     * Establece la posición del jugador.
     *
     * @param newX Nueva coordenada X.
     * @param newY Nueva coordenada Y.
     */
    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    /**
     * Obtiene la coordenada X actual del jugador.
     *
     * @return Valor de X en píxeles.
     */
    public int getX() { return x; }

    /**
     * Obtiene la coordenada Y actual del jugador.
     *
     * @return Valor de Y en píxeles.
     */
    public int getY() { return y; }

    /**
     * Retorna la imagen actual del jugador según la animación activa.
     *
     * @return La imagen correspondiente al frame actual, o {@code null} si no hay animación.
     */
    public Image getImage() {
        return (currentAnimation != null) ? currentAnimation.getCurrentFrame() : null;
    }

    /**
     * Retorna el estado actual del jugador, que incluye información sobre vidas y objetos.
     *
     * @return Una instancia de {@link PlayerState} con el estado del jugador.
     */
    public PlayerState getPlayerState() { return state; }

    /**
     * Obtiene la velocidad vertical actual del jugador.
     *
     * @return Valor de la velocidad vertical (dy).
     */
    public int getDy() { return dy; }

    /**
     * Reinicia el movimiento vertical del jugador, deteniendo el salto y reseteando la cuenta de saltos.
     */
    public void resetVerticalMotion() {
        dy = 0;
        jumping = false;
        currentJumpCount = 0;
    }

    /**
     * Obtiene la velocidad horizontal actual del jugador.
     *
     * @return Valor de la velocidad horizontal (dx).
     */
    public int getDx() {
        return dx;
    }

    /**
     * Aplica el efecto de las botas, incrementando el multiplicador de velocidad.
     */
    public void applyBoots() {
        speedMultiplier = 1.5;
    }

    /**
     * Establece el estado "vivo" del jugador y detiene los sonidos activos si se indica que ha muerto.
     *
     * @param alive {@code true} si el jugador está vivo; {@code false} en caso contrario.
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
        if (!alive) {
            stopWalkingSound();
        }
    }

    /**
     * Indica si el jugador se encuentra vivo.
     *
     * @return {@code true} si el jugador está vivo; {@code false} en caso contrario.
     */
    public boolean isAlive() {
        return alive;
    }
}
