package game.map;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class SOMap {
    private Image visualImage;
    private Image collisionImage;
    private int width = 3000;
    private int height = 1080;

    public SOMap() {
        // Cargar la imagen visual del mapa como un GIF
        URL visualURL = getClass().getResource("/resources/background GIF.gif");
        if (visualURL != null) {
            System.out.println("Imagen visual encontrada: " + visualURL); // Depuración
            ImageIcon visualIcon = new ImageIcon(visualURL);
            visualImage = visualIcon.getImage();
        } else {
            System.err.println("No se encontró la imagen visual del mapa.");
        }

        // Cargar la imagen de colisiones del mapa
        URL collisionURL = getClass().getResource("/resources/map_collision.png");
        if (collisionURL != null) {
            System.out.println("Imagen de colisiones encontrada: " + collisionURL); // Depuración
            collisionImage = new ImageIcon(collisionURL).getImage();
        } else {
            System.err.println("No se encontró la imagen de colisiones del mapa.");
        }
    }

    public Image getVisualImage() {
        return visualImage;
    }

    public Image getCollisionImage() {
        return collisionImage;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}