package game.controls.movements;

import java.awt.Image;

public class AnimationPlayer {
    private Image[] frames;
    private int currentFrame;
    private long lastTime;
    private long delay;

    public AnimationPlayer(Image[] frames, long delay) {
        this.frames = frames;
        this.delay = delay;
        this.currentFrame = 0;
        this.lastTime = System.currentTimeMillis();
    }

    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastTime > delay) {
            currentFrame = (currentFrame + 1) % frames.length;
            lastTime = now;
        }
    }

    public Image getCurrentFrame() {
        return frames[currentFrame];
    }
}
