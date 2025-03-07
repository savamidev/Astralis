package game.controls.movements;

public class PlayerState {
    private int life;
    private boolean hasSandia;
    private boolean hasLlave;

    public PlayerState() {
        this.life = 1; // Una sola vida
        this.hasSandia = false;
        this.hasLlave = false;
    }

    public int getLife() {
        return life;
    }

    public void reduceLife(int amount) {
        life -= amount;
    }

    public boolean isDead() {
        return life <= 0;
    }

    public boolean hasSandia() {
        return hasSandia;
    }

    public void setSandia(boolean hasSandia) {
        this.hasSandia = hasSandia;
    }

    public boolean hasLlave() {
        return hasLlave;
    }

    public void setLlave(boolean hasLlave) {
        this.hasLlave = hasLlave;
    }
}
