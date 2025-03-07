package game.controls.movements;

import javax.swing.*;
import java.awt.*;

public class Player {
    private enum State {IDLE, WALKING_LEFT, WALKING_RIGHT, JUMPING_LEFT, JUMPING_RIGHT, FALLING}
    private State currentState;
    private int x, y;
    private int width, height;
    private int dx, dy;
    private boolean jumping;

    private AnimationPlayer idleAnimation;
    private AnimationPlayer moveLeftAnimation;
    private AnimationPlayer moveRightAnimation;
    private AnimationPlayer jumpLeftAnimation;
    private AnimationPlayer jumpRightAnimation;
    private AnimationPlayer fallAnimation;
    private AnimationPlayer currentAnimation;

    private final int SPEED = 10;
    private final int JUMP_STRENGTH = -30; // negativo para subir
    private final double GRAVITY = 2;
    private final int TERMINAL_VELOCITY = 20;
    // El piso (suelo) se fija en 440
    private int floorY;
    // Se usa worldWidth para clamping horizontal (por ejemplo, 1920)
    private int worldWidth;

    private PlayerState state;
    private int currentJumpCount;

    private static final int COUNT_IDLE = 19;
    private static final int COUNT_LEFT = 19;
    private static final int COUNT_RIGHT = 19;
    private static final int COUNT_JUMP = 15;
    private static final int COUNT_FALL = 19;
    private static final long FRAME_DELAY = 50;

    // Constructor: se recibe startX, floorY (suelo fijo) y worldWidth (ancho de la pantalla)
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

        // Define claramente aquí las dimensiones deseadas:
        int newWidth = 150;  // <-- Ajusta al tamaño que quieras
        int newHeight = 150; // <-- Ajusta al tamaño que quieras

        idleAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardIdle/", COUNT_IDLE, newWidth, newHeight), FRAME_DELAY);
        moveLeftAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardWalkLeft/", COUNT_LEFT, newWidth, newHeight), FRAME_DELAY);
        moveRightAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardWalkRight/", COUNT_RIGHT, newWidth, newHeight), FRAME_DELAY);
        jumpLeftAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardJumpLeft/", COUNT_JUMP, newWidth, newHeight), FRAME_DELAY);
        jumpRightAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardJumpRight/", COUNT_JUMP, newWidth, newHeight), FRAME_DELAY);
        fallAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardIdle/", COUNT_FALL, newWidth, newHeight), FRAME_DELAY);

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

    public void jump() {
        int availableJumps = state.hasSandia() ? 2 : 1;
        if (currentJumpCount < availableJumps) {
            dy = JUMP_STRENGTH;
            jumping = true;
            currentJumpCount++;
        }
    }

    public void takeDamage() {
        state.reduceLife(1);
        if (state.isDead()) {
            System.out.println("El jugador ha muerto.");
        }
    }

    public void update() {
        updateState();
        currentAnimation.update();

        // Actualiza la posición
        x += dx;
        y += dy;

        // Aplicar gravedad
        dy += GRAVITY;
        if (dy > TERMINAL_VELOCITY)
            dy = TERMINAL_VELOCITY;

        // Si se sobrepasa el suelo, ajustar para que el jugador quede apoyado
        if (y + height >= floorY) {
            y = floorY - height;
            dy = 0;
            jumping = false;
            currentJumpCount = 0;
        }

        // Clampea la posición horizontal para que x esté entre 0 y (worldWidth - width)
        x = Math.max(0, Math.min(x, worldWidth - width));
    }

    public Rectangle getCollisionRectangle() {
        int hitboxWidth = width / 3;  // más estrecho para colisiones laterales
        int hitboxHeight = height - 10;
        int hitboxX = x + (width - hitboxWidth) / 2;
        int hitboxY = y + (height - hitboxHeight) / 2;
        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }


    // Método revisado y reducido al mínimo posible
    public Rectangle getFeetRectangle() {
        int feetWidth = width / 2; // más estrecho, para precisión horizontal
        int feetHeight = 5;        // mínimo absoluto para precisión vertical
        int feetX = x + (width - feetWidth) / 2;
        int feetY = y + height - feetHeight; // solo la parte inferior
        return new Rectangle(feetX, feetY, feetWidth, feetHeight);
    }


    public void moveLeft() { dx = -SPEED; }
    public void moveRight() { dx = SPEED; }
    public void stop() { dx = 0; }
    public void moveDown() { }
    public void stopDown() { }

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
