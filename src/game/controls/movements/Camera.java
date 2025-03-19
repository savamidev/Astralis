package game.controls.movements;

/**
 * Representa la cámara que sigue al jugador, calculando los desplazamientos necesarios
 * para centrar al jugador en el área visible sin salirse de los límites del mundo.
 */
public class Camera {
    private Player player;
    private int screenWidth;
    private int screenHeight;
    private int worldWidth;
    private int worldHeight;

    /**
     * Construye una nueva instancia de Camera.
     *
     * @param player       Referencia al jugador que la cámara seguirá.
     * @param screenWidth  Ancho del área visible (pantalla).
     * @param screenHeight Alto del área visible (pantalla).
     * @param worldWidth   Ancho total del mundo del juego.
     * @param worldHeight  Altura total del mundo del juego.
     */
    public Camera(Player player, int screenWidth, int screenHeight, int worldWidth, int worldHeight) {
        this.player = player;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    /**
     * Calcula el desplazamiento horizontal (offset X) para centrar al jugador en la pantalla.
     * Si el mundo es menor que la pantalla, centra la vista.
     *
     * @return Valor del offset horizontal.
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
     * Retorna un offset vertical fijo que centra la vista en el mundo,
     * sin seguir los movimientos verticales del jugador.
     *
     * @return Valor del offset vertical.
     */
    public int getOffsetY() {
        return (worldHeight - screenHeight) / 2;
    }

    /**
     * Actualiza las dimensiones del área visible.
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
