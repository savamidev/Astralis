package game.controls.movements;

/**
 * Representa la cámara que sigue al jugador en el juego.
 * <p>
 * En esta versión, la cámara solo sigue el movimiento horizontal del jugador.
 * El offset vertical se mantiene fijo, centrado en el mundo.
 * </p>
 */
public class Camera {
    private Player player;
    private int screenWidth;
    private int screenHeight;
    private int worldWidth;
    private int worldHeight;

    /**
     * Crea una nueva instancia de Camera.
     *
     * @param player      El jugador a seguir.
     * @param screenWidth Ancho del área visible (pantalla).
     * @param screenHeight Altura del área visible (pantalla).
     * @param worldWidth  Ancho total del mundo del juego.
     * @param worldHeight Altura total del mundo del juego.
     */
    public Camera(Player player, int screenWidth, int screenHeight, int worldWidth, int worldHeight) {
        this.player = player;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    /**
     * Calcula el desplazamiento horizontal (offset X) para centrar al jugador.
     *
     * @return El offset horizontal.
     */
    public int getOffsetX() {
        if (worldWidth <= screenWidth) {
            return (worldWidth - screenWidth) / 2;
        }
        int desired = player.getX() + player.getWidth() / 2 - screenWidth / 2;
        if (desired < 0) return 0;
        if (desired > worldWidth - screenWidth) return worldWidth - screenWidth;
        return desired;
    }

    /**
     * Devuelve un offset vertical fijo, centrado en el mundo.
     * De esta forma, la cámara no sigue el salto del jugador.
     *
     * @return El offset vertical constante.
     */
    public int getOffsetY() {
        return (worldHeight - screenHeight) / 2;
    }

    /**
     * Actualiza las dimensiones del área visible (pantalla).
     *
     * @param width  Nuevo ancho de la pantalla.
     * @param height Nueva altura de la pantalla.
     */
    public void setScreenSize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    /**
     * Actualiza las dimensiones del mundo del juego.
     *
     * @param width  Nuevo ancho del mundo.
     * @param height Nueva altura del mundo.
     */
    public void setWorldSize(int width, int height) {
        this.worldWidth = width;
        this.worldHeight = height;
    }
}
