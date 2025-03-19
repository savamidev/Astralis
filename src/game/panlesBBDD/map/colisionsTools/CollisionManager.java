package game.panlesBBDD.map.colisionsTools;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la detección de colisiones en el juego.
 * <p>
 * La clase construye una lista de áreas de colisión a partir de un TileMap,
 * considerando los tiles que representan obstáculos. Proporciona métodos para
 * verificar si un rectángulo colisiona con alguna de estas áreas.
 * </p>
 */
public class CollisionManager {
    private List<Rectangle> collisionAreas;

    /**
     * Crea una instancia de CollisionManager a partir del TileMap.
     * <p>
     * Se recorren todos los tiles del mapa y se agregan a la lista aquellas celdas
     * cuyo valor sea 1 (indicando un obstáculo). También se imprimen mensajes para
     * otros valores (por ejemplo, el valor 2 para agua).
     * </p>
     *
     * @param tileMap El mapa de tiles desde el cual extraer las áreas de colisión.
     */
    public CollisionManager(TileMap tileMap) {
        collisionAreas = new ArrayList<>();
        int tileSize = tileMap.getTileSize();
        int[][] tiles = tileMap.getTiles();
        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles[row].length; col++) {
                if (tiles[row][col] == 1) {
                    collisionAreas.add(new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize));
                    System.out.println("Colisión detectada en tile (" + row + "," + col + ")");
                } else if (tiles[row][col] == 2) {
                    System.out.println("Agua en tile (" + row + "," + col + ")");
                }
            }
        }
    }

    /**
     * Verifica si el rectángulo especificado colisiona con alguna de las áreas de colisión.
     *
     * @param rect El rectángulo a verificar.
     * @return {@code true} si hay colisión; {@code false} en caso contrario.
     */
    public boolean isColliding(Rectangle rect) {
        for (Rectangle area : collisionAreas) {
            if (rect.intersects(area)) {
                // Se encontró la primera colisión; se retorna inmediatamente.
                return true;
            }
        }
        return false;
    }


    /**
     * Verifica si hay colisión en la parte inferior del rectángulo, considerando un desplazamiento vertical.
     * <p>
     * Este método se puede utilizar para detectar colisiones en los pies del jugador.
     * </p>
     *
     * @param rect El rectángulo a verificar.
     * @param dy   Desplazamiento vertical a aplicar.
     * @return {@code true} si hay colisión en la parte inferior; {@code false} en caso contrario.
     */
    public boolean isCollidingBelow(Rectangle rect, int dy) {
        Rectangle movedRect = new Rectangle(rect.x, rect.y + dy, rect.width, rect.height);
        for (Rectangle area : collisionAreas) {
            if (rect.intersects(area) && rect.y + rect.height <= area.y + dy) {
                return true;
            }
        }
        return false;
    }
}
