package game.panlesBBDD.map.map1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import javax.sound.sampled.*;

public class DeathStyledDialog extends JDialog {
    // Variables para la animación
    private Timer timer;
    private float currentAlpha = 0.0f;
    private int step = 0;
    // Total steps: fade in: 20, hold: 40, fade out: 20, total = 80.
    private Runnable onClose;
    private Clip deathMusic;

    public DeathStyledDialog(Frame owner, Runnable onClose) {
        super(owner, true);
        this.onClose = onClose;
        setUndecorated(true);
        setSize(800, 600);
        setLocationRelativeTo(owner);
        // Asegurarse de que la ventana soporte opacidad
        setBackground(new Color(0,0,0,0)); // Fondo transparente

        // Agregar el JLabel con la imagen o GIF
        JLabel label = new JLabel(new ImageIcon(getClass().getResource("/resources/imagen/gameover.gif")));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(label, BorderLayout.CENTER);

        // Cargar la música de muerte
        loadDeathMusic();

        // Configurar el Timer para animar la opacidad: tick cada 50ms.
        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                step++;
                // Fade in: 0 <= step <= 20
                if (step <= 20) {
                    currentAlpha = step / 20.0f;
                } else if (step <= 60) { // Mantener opacidad total (hold) durante 40 ticks (2 s)
                    currentAlpha = 1.0f;
                } else if (step <= 80) { // Fade out: 60 < step <= 80
                    currentAlpha = (80 - step) / 20.0f;
                } else {
                    timer.stop();
                    // Detener la música
                    if (deathMusic != null) {
                        deathMusic.stop();
                        deathMusic.close();
                    }
                    setVisible(false);
                    dispose();
                    if (DeathStyledDialog.this.onClose != null) {
                        DeathStyledDialog.this.onClose.run();
                    }
                    return;
                }
                // Actualizar la opacidad de la ventana
                setOpacity(currentAlpha);
            }
        });
    }

    private void loadDeathMusic() {
        try {
            InputStream is = getClass().getResourceAsStream("/resources/sound/gameover.wav");
            if (is == null) {
                System.err.println("No se encontró la música de muerte en /resources/sound/death_music.wav");
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);
            deathMusic = AudioSystem.getClip();
            deathMusic.open(audioStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showDialog() {
        // Reiniciar la animación
        currentAlpha = 0.0f;
        step = 0;
        setOpacity(0.0f);
        // Iniciar la música
        if (deathMusic != null) {
            deathMusic.setFramePosition(0);
            deathMusic.start();
        }
        // Iniciar la animación
        timer.start();
        setVisible(true);
    }
}
