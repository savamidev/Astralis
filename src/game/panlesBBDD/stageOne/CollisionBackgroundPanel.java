package game.map;

import game.controls.movements.Camera;
import javax.swing.*;
import java.awt.*;

public class CollisionBackgroundPanel extends JPanel {
    private game.map.SOMap mapa;
    private Camera camera;

    public CollisionBackgroundPanel(game.map.SOMap mapa, Camera camera) {
        this.mapa = mapa;
        this.camera = camera;
        setOpaque(false);
        setFocusable(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        camera.setScreenSize(getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D) g.create();
        int offsetX = camera.getOffsetX();
        int offsetY = camera.getOffsetY();
        g2d.translate(-offsetX, -offsetY);
        Image colImg = mapa.getCollisionImage();
        if (colImg != null) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.drawImage(colImg, 0, 0, this);
        }
        g2d.dispose();
    }
}
