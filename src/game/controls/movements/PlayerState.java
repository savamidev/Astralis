package game.controls.movements;

/**
 * Representa el estado del jugador, incluyendo la cantidad de vidas y la posesión de objetos.
 */
public class PlayerState {
    private int life;
    private boolean hasSandia;
    private boolean hasLlave;

    /**
     * Crea una nueva instancia de PlayerState.
     * <p>
     * Inicialmente, el jugador tiene una sola vida y no posee ni la sandía ni la llave.
     * </p>
     */
    public PlayerState() {
        this.life = 1; // Una sola vida.
        this.hasSandia = false;
        this.hasLlave = false;
    }

    /**
     * Obtiene la cantidad de vidas actuales del jugador.
     *
     * @return El número de vidas.
     */
    public int getLife() {
        return life;
    }

    /**
     * Reduce la cantidad de vidas del jugador en la cantidad especificada.
     *
     * @param amount La cantidad de vidas a restar.
     */
    public void reduceLife(int amount) {
        life -= amount;
    }

    /**
     * Verifica si el jugador ha perdido todas sus vidas.
     *
     * @return {@code true} si el jugador está muerto; de lo contrario, {@code false}.
     */
    public boolean isDead() {
        return life <= 0;
    }

    /**
     * Verifica si el jugador posee la sandía.
     *
     * @return {@code true} si el jugador tiene la sandía; de lo contrario, {@code false}.
     */
    public boolean hasSandia() {
        return hasSandia;
    }

    /**
     * Establece el estado de posesión de la sandía.
     *
     * @param hasSandia {@code true} si el jugador obtiene la sandía; {@code false} si se la quita.
     */
    public void setSandia(boolean hasSandia) {
        this.hasSandia = hasSandia;
    }

    /**
     * Verifica si el jugador posee la llave.
     *
     * @return {@code true} si el jugador tiene la llave; de lo contrario, {@code false}.
     */
    public boolean hasLlave() {
        return hasLlave;
    }

    /**
     * Establece el estado de posesión de la llave.
     *
     * @param hasLlave {@code true} si el jugador obtiene la llave; {@code false} si se la quita.
     */
    public void setLlave(boolean hasLlave) {
        this.hasLlave = hasLlave;
    }
}
