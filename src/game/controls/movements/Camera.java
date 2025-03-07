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

    // Se centra en el jugador usando su centro (mitad de ancho y alto)
    public int getOffsetX() {
        if (worldWidth <= screenWidth) {
            return (worldWidth - screenWidth) / 2;
        }
        int desired = player.getX() + player.getWidth() / 2 - screenWidth / 2;
        if (desired < 0) return 0;
        if (desired > worldWidth - screenWidth) return worldWidth - screenWidth;
        return desired;
    }

    public int getOffsetY() {
        if (worldHeight <= screenHeight) {
            return (worldHeight - screenHeight) / 2;
        }
        int desired = player.getY() + player.getHeight() / 2 - screenHeight / 2;
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
