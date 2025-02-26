package game.controls.movements;

import javax.swing.*;
import java.awt.*;

public class Player {

    // Enum para los diferentes estados del jugador (caminar, saltar, caer, etc.)
    private enum State {IDLE, WALKING_LEFT, WALKING_RIGHT, JUMPING_LEFT, JUMPING_RIGHT, FALLING}
    private State currentState; // Estado actual del jugador

    private int x, y; // Coordenadas del jugador
    private int width, height; // Dimensiones del jugador
    private int dx, dy; // Velocidades del jugador en el eje X (dx) y Y (dy)
    private boolean jumping; // Indica si el jugador está saltando
    private boolean fallFaster; // Indica si el jugador está cayendo más rápido

    // Animaciones para los diferentes movimientos del jugador
    private AnimationPlayer idleAnimation;
    private AnimationPlayer moveLeftAnimation;
    private AnimationPlayer moveRightAnimation;
    private AnimationPlayer jumpLeftAnimation;
    private AnimationPlayer jumpRightAnimation;
    private AnimationPlayer fallAnimation;
    private AnimationPlayer currentAnimation; // Animación actual

    // Constantes de control del jugador
    private final int SPEED = 10; // Velocidad de movimiento
    private final int JUMP_STRENGTH = -30; // Fuerza del salto
    private final double GRAVITY = 2; // Gravedad que afecta al jugador
    private final int TERMINAL_VELOCITY = 20; // Velocidad máxima de caída
    private final int FLOOR_Y = 920; // Altura del suelo en el eje Y

    // Cantidad de frames por cada animación
    private static final int COUNT_IDLE = 19;
    private static final int COUNT_LEFT = 19;
    private static final int COUNT_RIGHT = 19;
    private static final int COUNT_JUMP = 15;
    private static final int COUNT_FALL = 19;

    // Retardo entre frames de animación (en milisegundos)
    private static final long FRAME_DELAY = 50;

    // Constructor del jugador, inicializa su posición y carga las animaciones
    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.dx = 0;
        this.dy = 0;
        this.jumping = false;
        this.fallFaster = false;
        this.currentState = State.IDLE;

        // Cargar las animaciones para los diferentes estados
        idleAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardIdle/", COUNT_IDLE), FRAME_DELAY);
        moveLeftAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardWalkLeft/", COUNT_LEFT), FRAME_DELAY);
        moveRightAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardWalkRight/", COUNT_RIGHT), FRAME_DELAY);
        jumpLeftAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardJumpLeft/", COUNT_JUMP), FRAME_DELAY);
        jumpRightAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardJumpRight/", COUNT_JUMP), FRAME_DELAY);
        fallAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardIdle/", COUNT_FALL), FRAME_DELAY);

        currentAnimation = idleAnimation; // Establece la animación por defecto como la de inactividad

        // Si la animación tiene una imagen cargada, establece las dimensiones del jugador
        if (currentAnimation.getCurrentFrame() != null) {
            width = currentAnimation.getCurrentFrame().getWidth(null);
            height = currentAnimation.getCurrentFrame().getHeight(null);
        } else {
            // Si no se puede cargar la imagen, establece dimensiones predeterminadas
            width = 50;
            height = 50;
        }

        this.y = FLOOR_Y - height; // Asegura que el jugador comienza en el suelo
    }

    // Carga las imágenes de animación para un conjunto específico de frames
    private Image[] loadAnimationImages(String basePath, int count) {
        Image[] frames = new Image[count];
        int newWidth = 200;
        int newHeight = 200;

        for (int i = 1; i <= count; i++) {
            try {
                // Carga cada imagen y la escala al tamaño deseado
                Image originalImage = new ImageIcon(getClass().getResource(basePath + i + ".png")).getImage();
                Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                frames[i - 1] = scaledImage;
            } catch (Exception e) {
                // Si no se puede cargar una imagen, muestra un error
                System.err.println("Error al cargar imagen: " + basePath + i + ".png - " + e.getMessage());
                frames[i - 1] = null; // Asigna null si falla la carga
            }
        }
        return frames;
    }

    // Actualiza el estado actual del jugador según su movimiento
    private void updateState() {
        if (jumping) {
            if (dx < 0) { // Movimiento hacia la izquierda mientras salta
                currentState = State.JUMPING_LEFT;
            } else if (dx > 0) { // Movimiento hacia la derecha mientras salta
                currentState = State.JUMPING_RIGHT;
            }
        } else {
            if (dx < 0) currentState = State.WALKING_LEFT; // Movimiento hacia la izquierda
            else if (dx > 0) currentState = State.WALKING_RIGHT; // Movimiento hacia la derecha
            else currentState = State.IDLE; // Inactividad si no se mueve
        }

        // Selecciona la animación correspondiente según el estado
        switch (currentState) {
            case WALKING_LEFT -> currentAnimation = (moveLeftAnimation != null) ? moveLeftAnimation : idleAnimation;
            case WALKING_RIGHT -> currentAnimation = (moveRightAnimation != null) ? moveRightAnimation : idleAnimation;
            case JUMPING_LEFT -> currentAnimation = (jumpLeftAnimation != null) ? jumpLeftAnimation : idleAnimation;
            case JUMPING_RIGHT -> currentAnimation = (jumpRightAnimation != null) ? jumpRightAnimation : idleAnimation;
            case FALLING -> currentAnimation = (fallAnimation != null) ? fallAnimation : idleAnimation;
            case IDLE -> currentAnimation = idleAnimation;
        }
    }

    // Métodos de movimiento del jugador
    public void moveLeft() { dx = -SPEED; }
    public void moveRight() { dx = SPEED; }
    public void stop() { dx = 0; } // Detiene el movimiento en el eje X
    public void jump() {
        if (!jumping) {
            dy = JUMP_STRENGTH; // Aplica la fuerza del salto
            jumping = true; // Marca que el jugador está saltando
        }
    }
    public void moveDown() { fallFaster = true; } // Activa la caída más rápida
    public void stopDown() { fallFaster = false; } // Detiene la caída rápida

    // Actualiza la posición y animación del jugador
    public void update() {
        updateState(); // Actualiza el estado del jugador
        currentAnimation.update(); // Actualiza la animación

        // Actualiza la posición del jugador según la velocidad
        x += dx;
        y += dy;

        if (jumping) {
            dy += GRAVITY; // Aplica la gravedad si está saltando
            if (dy > TERMINAL_VELOCITY) dy = TERMINAL_VELOCITY; // Limita la velocidad de caída
        }

        // Asegura que el jugador no pase del suelo
        if (y >= FLOOR_Y - height) {
            y = FLOOR_Y - height;
            dy = 0;
            jumping = false; // Marca que el jugador ha aterrizado
        }

        // Limita el movimiento del jugador dentro de los límites de la pantalla
        if (x < 0) x = 0;
        if (x > 1920 - width) x = 1920 - width;
    }

    // Métodos para obtener la posición y la imagen del jugador
    public int getX() { return x; }
    public int getY() { return y; }
    public Image getImage() { return (currentAnimation != null) ? currentAnimation.getCurrentFrame() : null; }
}
