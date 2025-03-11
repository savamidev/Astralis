package game.panlesBBDD.map.map1;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa el mapa de tiles del juego.
 * <p>
 * Esta clase carga un mapa desde un archivo CSV o, en caso de no encontrarse el archivo,
 * genera un mapa por defecto. Proporciona métodos para obtener información sobre los tiles,
 * como su tamaño y tipo en una posición determinada.
 * </p>
 */
public class TileMap {
    private int[][] tiles;
    private int tileSize;
    private int worldWidth;
    private int worldHeight;

    /**
     * Crea un TileMap a partir de un archivo CSV.
     *
     * @param csvFilePath Ruta del archivo CSV que define el mapa.
     * @param tileSize    Tamaño de cada tile en píxeles.
     */
    public TileMap(String csvFilePath, int tileSize) {
        this.tileSize = tileSize;
        InputStream is = getClass().getResourceAsStream(csvFilePath);
        if (is == null) {
            System.err.println("No se encontró el archivo CSV: " + csvFilePath + ". Se usará el mapa por defecto.");
            createDefaultMap();
            return;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            List<int[]> rowsList = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if(line.trim().isEmpty()){
                    continue;
                }
                String[] parts = line.split(",");
                int[] row = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    String token = parts[i].trim();
                    row[i] = token.isEmpty() ? 0 : Integer.parseInt(token);
                }
                rowsList.add(row);
            }
            tiles = rowsList.toArray(new int[rowsList.size()][]);
            worldWidth = tiles[0].length * tileSize;
            worldHeight = tiles.length * tileSize;
        } catch (Exception e) {
            e.printStackTrace();
            createDefaultMap();
        }
    }

    /**
     * Crea un mapa por defecto de 75 columnas x 27 filas (3000 x 1080).
     */
    private void createDefaultMap() {
        int columns = 75;
        int rows = 27;
        tiles = new int[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (row == 0 || row == rows - 1 || col == 0 || col == columns - 1) {
                    tiles[row][col] = 1;
                } else {
                    tiles[row][col] = 0;
                }
            }
        }
        worldWidth = columns * tileSize;
        worldHeight = rows * tileSize;
    }

    /**
     * Obtiene el tamaño de cada tile.
     *
     * @return El tamaño de los tiles en píxeles.
     */
    public int getTileSize() {
        return tileSize;
    }

    /**
     * Obtiene la matriz de tiles.
     *
     * @return Matriz de enteros representando el mapa.
     */
    public int[][] getTiles() {
        return tiles;
    }

    /**
     * Devuelve el tipo de tile en una posición específica (en píxeles).
     *
     * @param x Coordenada X en píxeles.
     * @param y Coordenada Y en píxeles.
     * @return El valor del tile, o -1 si la posición está fuera del mapa.
     */
    public int getTileAt(int x, int y) {
        int col = x / tileSize;
        int row = y / tileSize;
        if (row < 0 || row >= tiles.length || col < 0 || col >= tiles[0].length) {
            return -1;
        }
        return tiles[row][col];
    }

    /**
     * Obtiene el tipo de tile ubicado en el centro de un rectángulo.
     *
     * @param rect El rectángulo que se evalúa.
     * @return El valor del tile central.
     */
    public int getTileTypeAtRect(Rectangle rect) {
        int centerX = rect.x + rect.width / 2;
        int centerY = rect.y + rect.height / 2;
        return getTileAt(centerX, centerY);
    }

    /**
     * Obtiene el ancho total del mundo.
     *
     * @return El ancho del mundo en píxeles.
     */
    public int getWorldWidth() {
        return worldWidth;
    }

    /**
     * Obtiene la altura total del mundo.
     *
     * @return La altura del mundo en píxeles.
     */
    public int getWorldHeight() {
        return worldHeight;
    }
}
