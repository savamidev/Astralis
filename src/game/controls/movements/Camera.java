package game.controls.movements;

public class Camera {
    private Player player;
    private int screenWidth;
    private int screenHeight;
    private int worldWidth;
    private int worldHeight;

    public Camera(Player player, int screenWidth, int screenHeight, int worldWidth, int worldHeight) {
        this.player = player;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public int getOffsetX() {
        int desired = player.getX() - screenWidth / 2;
        if (desired < 0) return 0;
        if (desired > worldWidth - screenWidth) return worldWidth - screenWidth;
        return desired;
    }

    public int getOffsetY() {
        int desired = player.getY() - screenHeight / 2;
        if (desired < 0) return 0;
        if (desired > worldHeight - screenHeight) return worldHeight - screenHeight;
        return desired;
    }

    public void setScreenSize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    public void setWorldSize(int width, int height) {
        this.worldWidth = width;
        this.worldHeight = height;
    }
}
