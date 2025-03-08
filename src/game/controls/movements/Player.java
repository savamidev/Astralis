package game.controls.movements;

import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.net.URL;

/**
 * Representa al jugador y gestiona sus movimientos, animaciones y acciones (como salto y dash).
 * <p>
 * La clase maneja la posición, velocidad, animaciones y sonidos asociados a las acciones
 * del jugador, permitiendo moverlo e interactuar con el entorno.
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

    private final int SPEED = 5;
    private final int JUMP_STRENGTH = -30; // Valor negativo para subir.
    private final double GRAVITY = 2;
    private final int TERMINAL_VELOCITY = 20;
    private int floorY;
    private int worldWidth;

    private PlayerState state;
    private int currentJumpCount;

    private static final int COUNT_IDLE = 4;
    private static final int COUNT_LEFT = 7;
    private static final int COUNT_RIGHT = 7;
    private static final int COUNT_JUMP = 7;
    private static final int COUNT_FALL = 4;
    private static final long FRAME_DELAY = 250;

    // Campos para el audio.
    private Clip walkingClip;
    private Clip jumpClip;

    // Campos para dash (habilitado si el jugador posee la sandía).
    private boolean dashing = false;
    private long dashStartTime = 0;
    private final int DASH_DURATION = 200; // Duración en milisegundos.
    private final int DASH_SPEED = 20;     // Velocidad del dash.

    /**
     * Crea una instancia de Player con su posición inicial, el suelo y el ancho del mundo.
     *
     * @param startX     Posición inicial en X.
     * @param floorY     Posición Y del suelo.
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
     * Carga un conjunto de imágenes para la animación a partir de una ruta base.
     *
     * @param basePath Ruta base de las imágenes.
     * @param count    Número de imágenes a cargar.
     * @param newWidth Nuevo ancho para escalar las imágenes.
     * @param newHeight Nuevo alto para escalar las imágenes.
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
     * Actualiza el estado actual del jugador y selecciona la animación correspondiente.
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
     * @return El clip cargado o {@code null} si no se pudo cargar.
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
     * Detiene el sonido de caminar.
     */
    private void stopWalkingSound() {
        if (walkingClip != null && walkingClip.isActive()) {
            walkingClip.stop();
        }
    }

    /**
     * Mueve al jugador hacia la izquierda y reproduce el sonido de caminar.
     */
    public void moveLeft() {
        dx = -SPEED;
        startWalkingSound();
    }

    /**
     * Mueve al jugador hacia la derecha y reproduce el sonido de caminar.
     */
    public void moveRight() {
        dx = SPEED;
        startWalkingSound();
    }

    /**
     * Detiene el movimiento horizontal y el sonido de caminar.
     */
    public void stop() {
        dx = 0;
        stopWalkingSound();
    }

    /**
     * Método para mover al jugador hacia abajo (sin implementación).
     */
    public void moveDown() { }

    /**
     * Método para detener el movimiento hacia abajo del jugador (sin implementación).
     */
    public void stopDown() { }

    /**
     * Realiza un salto. Permite un doble salto si el jugador posee la sandía.
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

    /**
     * Reproduce el sonido de aterrizaje cuando el jugador finaliza un salto.
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
     * <p>
     * Se aplican la gravedad y se gestionan las colisiones con el suelo,
     * además de controlar la duración del dash.
     * </p>
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

        // Procesa el dash: si ha pasado la duración, se finaliza.
        if (dashing && System.currentTimeMillis() - dashStartTime >= DASH_DURATION) {
            dashing = false;
            dx = 0;
            System.out.println("Dash finalizado.");
        }

        // Limita la posición horizontal dentro del mundo.
        x = Math.max(0, Math.min(x, worldWidth - width));
    }

    /**
     * Ejecuta un dash horizontal si el jugador posee la sandía.
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
     * Obtiene el área de colisión del cuerpo del jugador (para colisiones laterales).
     * <p>
     * Se ha modificado para desplazar la hitbox hacia abajo y reducirla en altura, de modo que
     * la parte superior (menos relevante para las colisiones laterales) no interfiera.
     * </p>
     *
     * @return Un rectángulo que representa la hitbox del jugador.
     */
    public Rectangle getCollisionRectangle() {
        int hitboxWidth = width / 3;
        int hitboxHeight = height - 40; // Se reduce la altura de la hitbox
        int hitboxX = x + (width - hitboxWidth) / 2;
        int hitboxY = y + 40;          // Se desplaza 40 píxeles hacia abajo
        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    /**
     * Obtiene el área de colisión de los pies del jugador, para detectar colisiones con el suelo.
     *
     * @return Un rectángulo que representa el área de los pies.
     */
    public Rectangle getFeetRectangle() {
        int feetWidth = width / 2;
        int feetHeight = 5;
        int feetX = x + (width - feetWidth) / 2;
        int feetY = y + height - feetHeight;
        return new Rectangle(feetX, feetY, feetWidth, feetHeight);
    }

    /**
     * Obtiene el área de colisión de la cabeza del jugador (para detectar colisiones al saltar).
     * <p>
     * Se ha modificado para reducirla drásticamente: el ancho es de 1/4 del total, la altura es de 1 píxel
     * y se posiciona 20 píxeles por debajo del borde superior.
     * </p>
     *
     * @return Un rectángulo que representa el área de la cabeza.
     */
    public Rectangle getHeadRectangle() {
        int headWidth = width / 4; // Reducción del ancho a 1/4 del total
        int headHeight = 1;        // Altura de 1 píxel
        int headX = x + (width - headWidth) / 2;
        int headY = y + 20;        // Se desplaza 20 píxeles hacia abajo
        return new Rectangle(headX, headY, headWidth, headHeight);
    }

    /**
     * Obtiene el ancho del jugador.
     *
     * @return El ancho.
     */
    public int getWidth() { return width; }

    /**
     * Obtiene la altura del jugador.
     *
     * @return La altura.
     */
    public int getHeight() { return height; }

    /**
     * Establece la posición del jugador.
     *
     * @param newX Nueva coordenada en X.
     * @param newY Nueva coordenada en Y.
     */
    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    /**
     * Obtiene la posición en X del jugador.
     *
     * @return La coordenada X.
     */
    public int getX() { return x; }

    /**
     * Obtiene la posición en Y del jugador.
     *
     * @return La coordenada Y.
     */
    public int getY() { return y; }

    /**
     * Obtiene la imagen actual del jugador según la animación en curso.
     *
     * @return La imagen del frame actual o {@code null} si no hay animación.
     */
    public Image getImage() {
        return (currentAnimation != null) ? currentAnimation.getCurrentFrame() : null;
    }

    /**
     * Obtiene el estado actual del jugador.
     *
     * @return Una instancia de {@link PlayerState} con la información del jugador.
     */
    public PlayerState getPlayerState() { return state; }

    /**
     * Obtiene la velocidad vertical actual del jugador.
     *
     * @return La velocidad en el eje Y.
     */
    public int getDy() { return dy; }

    /**
     * Reinicia el movimiento vertical del jugador, usado al aterrizar.
     */
    public void resetVerticalMotion() {
        dy = 0;
        jumping = false;
        currentJumpCount = 0;
    }

    /**
     * Obtiene la velocidad horizontal actual del jugador.
     *
     * @return La velocidad en el eje X.
     */
    public int getDx() {
        return dx;
    }
}
