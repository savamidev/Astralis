package game.controls.movements;

/**
 * Representa el estado del jugador, incluyendo la cantidad de vidas y la posesión de objetos
 * como la sandía y la llave.
 */
public class PlayerState {
    private int life;
    private boolean hasSandia;
    private boolean hasLlave;

    /**
     * Crea una nueva instancia de PlayerState con los valores iniciales:
     * <ul>
     *   <li>life = 1</li>
     *   <li>hasSandia = false</li>
     *   <li>hasLlave = false</li>
     * </ul>
     */
    public PlayerState() {
        this.life = 1;
        this.hasSandia = false;
        this.hasLlave = false;
    }

    /**
     * Obtiene la cantidad actual de vidas del jugador.
     *
     * @return Número de vidas.
     */
    public int getLife() {
        return life;
    }

    /**
     * Reduce la cantidad de vidas del jugador en la cantidad especificada.
     *
     * @param amount Número de vidas a restar.
     */
    public void reduceLife(int amount) {
        life -= amount;
    }

    /**
     * Verifica si el jugador ha perdido todas sus vidas.
     *
     * @return {@code true} si el jugador está muerto (life <= 0), {@code false} en caso contrario.
     */
    public boolean isDead() {
        return life <= 0;
    }

    /**
     * Verifica si el jugador posee la sandía.
     *
     * @return {@code true} si el jugador tiene la sandía; {@code false} en caso contrario.
     */
    public boolean hasSandia() {
        return hasSandia;
    }

    /**
     * Establece el estado de posesión de la sandía.
     *
     * @param hasSandia {@code true} si el jugador posee la sandía; {@code false} en caso contrario.
     */
    public void setSandia(boolean hasSandia) {
        this.hasSandia = hasSandia;
    }

    /**
     * Verifica si el jugador posee la llave.
     *
     * @return {@code true} si el jugador tiene la llave; {@code false} en caso contrario.
     */
    public boolean hasLlave() {
        return hasLlave;
    }

    /**
     * Establece el estado de posesión de la llave.
     *
     * @param hasLlave {@code true} si el jugador posee la llave; {@code false} en caso contrario.
     */
    public void setLlave(boolean hasLlave) {
        this.hasLlave = hasLlave;
    }

    /**
     * Reinicia el estado del jugador a los valores iniciales.
     */
    public void reset() {
        this.life = 1;
        this.hasSandia = false;
        this.hasLlave = false;
    }
}
