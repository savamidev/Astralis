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
    private boolean fallFaster;

    private AnimationPlayer idleAnimation;
    private AnimationPlayer moveLeftAnimation;
    private AnimationPlayer moveRightAnimation;
    private AnimationPlayer jumpLeftAnimation;
    private AnimationPlayer jumpRightAnimation;
    private AnimationPlayer fallAnimation;
    private AnimationPlayer currentAnimation;

    private final int SPEED = 10;
    private final int JUMP_STRENGTH = -30;
    private final double GRAVITY = 2;
    private final int TERMINAL_VELOCITY = 20;
    private final int FLOOR_Y = 880;

    // Ancho del mundo (diferente del ancho de la ventana)
    private int worldWidth = 3000;

    private static final int COUNT_IDLE = 19;
    private static final int COUNT_LEFT = 19;
    private static final int COUNT_RIGHT = 19;
    private static final int COUNT_JUMP = 15;
    private static final int COUNT_FALL = 19;
    private static final long FRAME_DELAY = 50;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.dx = 0;
        this.dy = 0;
        this.jumping = false;
        this.fallFaster = false;
        this.currentState = State.IDLE;

        idleAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardIdle/", COUNT_IDLE), FRAME_DELAY);
        moveLeftAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardWalkLeft/", COUNT_LEFT), FRAME_DELAY);
        moveRightAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardWalkRight/", COUNT_RIGHT), FRAME_DELAY);
        jumpLeftAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardJumpLeft/", COUNT_JUMP), FRAME_DELAY);
        jumpRightAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardJumpRight/", COUNT_JUMP), FRAME_DELAY);
        fallAnimation = new AnimationPlayer(loadAnimationImages("/resources/2BlueWizardIdle/", COUNT_FALL), FRAME_DELAY);

        currentAnimation = idleAnimation;
        if (currentAnimation.getCurrentFrame() != null) {
            width = currentAnimation.getCurrentFrame().getWidth(null);
            height = currentAnimation.getCurrentFrame().getHeight(null);
        } else {
            width = 50;
            height = 50;
        }

        this.y = FLOOR_Y - height;
    }

    private Image[] loadAnimationImages(String basePath, int count) {
        Image[] frames = new Image[count];
        int newWidth = 200;
        int newHeight = 200;
        for (int i = 1; i <= count; i++) {
            try {
                Image originalImage = new ImageIcon(getClass().getResource(basePath + i + ".png")).getImage();
                Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                frames[i - 1] = scaledImage;
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
            case WALKING_LEFT -> currentAnimation = (moveLeftAnimation != null) ? moveLeftAnimation : idleAnimation;
            case WALKING_RIGHT -> currentAnimation = (moveRightAnimation != null) ? moveRightAnimation : idleAnimation;
            case JUMPING_LEFT -> currentAnimation = (jumpLeftAnimation != null) ? jumpLeftAnimation : idleAnimation;
            case JUMPING_RIGHT -> currentAnimation = (jumpRightAnimation != null) ? jumpRightAnimation : idleAnimation;
            case FALLING -> currentAnimation = (fallAnimation != null) ? fallAnimation : idleAnimation;
            case IDLE -> currentAnimation = idleAnimation;
        }
    }

    public void moveLeft() { dx = -SPEED; }
    public void moveRight() { dx = SPEED; }
    public void stop() { dx = 0; }
    public void jump() {
        if (!jumping) {
            dy = JUMP_STRENGTH;
            jumping = true;
        }
    }
    public void moveDown() { fallFaster = true; }
    public void stopDown() { fallFaster = false; }

    public void update() {
        updateState();
        currentAnimation.update();
        x += dx;
        y += dy;
        if (jumping) {
            dy += GRAVITY;
            if (dy > TERMINAL_VELOCITY) dy = TERMINAL_VELOCITY;
        }
        if (y < 0) {
            y = 0;
            dy = 0;
        }
        if (y >= FLOOR_Y - height) {
            y = FLOOR_Y - height;
            dy = 0;
            jumping = false;
        }
        // Limita el movimiento horizontal según el ancho del mundo
        if (x < 0) {
            x = 0;
            dx = 0;
        } else if (x > worldWidth - width) {
            x = worldWidth - width;
            dx = 0;
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Image getImage() { return (currentAnimation != null) ? currentAnimation.getCurrentFrame() : null; }
}
