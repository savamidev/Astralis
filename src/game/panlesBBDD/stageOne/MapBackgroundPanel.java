package game.map;

import game.controls.movements.Camera;
import javax.swing.*;
import java.awt.*;

public class MapBackgroundPanel extends JPanel {
    private game.map.SOMap mapa;
    private Camera camera;

    public MapBackgroundPanel(game.map.SOMap soMap, Camera camera) {
        this.mapa = soMap;
        this.camera = camera;
        setOpaque(true); // Asegúrate de que el panel sea opaco
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        camera.setScreenSize(getWidth(), getHeight()); // Actualiza el tamaño de la pantalla en la cámara
        Graphics2D g2d = (Graphics2D) g.create();

        // Obtener el desplazamiento de la cámara
        int offsetX = camera.getOffsetX();
        int offsetY = camera.getOffsetY();

        // Dibujar la imagen de fondo, ajustando su posición según la cámara
        Image bg = mapa.getVisualImage();
        if (bg != null) {
            // Ajustar la posición del fondo en relación con la cámara
            g2d.drawImage(bg, -offsetX, -offsetY, this);
        } else {
            // Si no hay imagen de fondo, dibujar un fondo gris
            g2d.setColor(Color.GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        g2d.dispose(); // Liberar recursos de Graphics2D
    }
}