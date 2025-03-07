package game.collision;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import game.map.TileMap;

public class CollisionManager {
    private List<Rectangle> collisionAreas;

    // Se construye a partir del TileMap extrayendo los tiles con valor 1 (obstáculos)
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

    // Verifica si un rectángulo colisiona con alguna de las áreas
    public boolean isColliding(Rectangle rect) {
        for (Rectangle area : collisionAreas) {
            if (rect.intersects(area))
                return true;
        }
        return false;
    }

    // Verifica si hay colisión abajo (solo los pies del jugador)
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
