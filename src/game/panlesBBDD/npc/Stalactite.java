package game.objects;

import java.awt.*;
import javax.swing.ImageIcon;
import java.net.URL;
import javax.sound.sampled.*;

/**
 * La clase Stalactite simula una estalactita que:
 * 1. Permanece en estado de espera hasta detectar que el jugador se acerca horizontalmente (dentro de detectionThreshold px).
 * 2. Una vez detectado, vibra (sacude su posición horizontal) durante 1 segundo.
 * 3. Luego cae verticalmente; si el jugador colisiona con ella durante la caída, se dispara la mecánica de muerte.
 * Al salir del área de juego, desaparece.
 */
public class Stalactite {
    private int initialX, initialY; // Posición inicial
    private int x, y;              // Posición actual
    private int width, height;
    private Image image;
    private boolean active;

    // Estados: 0 = esperando, 1 = vibrando, 2 = cayendo, 3 = desaparecida
    private int state;
    private long stateStartTime;
    private final long vibrationDuration = 1000; // 1 segundo de vibración
    private final int fallSpeed = 20;            // Velocidad de caída aumentada a 20 px/actualización
    // Rango de detección horizontal (ahora mayor)
    private final int detectionThreshold = 200;

    // Sonido de caída (suponiendo 2 s de duración)
    private Clip fallSound;
    private final String soundPath = "/resources/sound/estalactita.wav"; // Ajusta la ruta según corresponda

    public Stalactite(int x, int y, int width, int height, String imagePath) {
        this.initialX = x;
        this.initialY = y;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.active = true;
        this.state = 0; // esperando
        this.stateStartTime = System.currentTimeMillis();
        try {
            URL url = getClass().getResource(imagePath);
            if (url != null) {
                image = new ImageIcon(url).getImage();
            }
        } catch(Exception e) {
            System.err.println("Error al cargar la imagen de la estalactita: " + e.getMessage());
            image = null;
        }
        loadFallSound();
    }

    private void loadFallSound() {
        try {
            URL url = getClass().getResource(soundPath);
            if (url != null) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
                fallSound = AudioSystem.getClip();
                fallSound.open(audioStream);
            }
        } catch(Exception e) {
            System.err.println("Error al cargar el sonido de la estalactita: " + e.getMessage());
            fallSound = null;
        }
    }

    private void playFallSound() {
        if (fallSound != null) {
            fallSound.setFramePosition(0);
            fallSound.start();
        }
    }

    /**
     * Actualiza el estado de la estalactita en función de la posición del jugador.
     * @param playerBounds Área de colisión del jugador.
     */
    public void update(Rectangle playerBounds) {
        if (!active) return;

        // Estado 0: esperando. Si el jugador se acerca horizontalmente, pasa a vibrar.
        if (state == 0) {
            int stalactiteCenterX = initialX + width / 2;
            int playerCenterX = playerBounds.x + playerBounds.width / 2;
            if (Math.abs(playerCenterX - stalactiteCenterX) <= detectionThreshold) {
                state = 1; // vibrando
                stateStartTime = System.currentTimeMillis();
            }
        }
        // Estado 1: vibrando.
        else if (state == 1) {
            long elapsed = System.currentTimeMillis() - stateStartTime;
            if (elapsed < vibrationDuration) {
                x = initialX + (int)(Math.random() * 10 - 5);
            } else {
                x = initialX;
                state = 2;
                playFallSound();
            }
        }
        // Estado 2: cayendo.
        else if (state == 2) {
            y += fallSpeed;
            if (y > 1080) {
                active = false;
                state = 3;
            }
        }
    }

    /**
     * Dibuja la estalactita.
     * @param g Objeto Graphics para el dibujo.
     */
    public void draw(Graphics g) {
        if (!active) return;
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x, y, width, height);
        }
    }

    /**
     * Retorna el área de colisión de la estalactita.
     * @return Rectangle representando su hitbox.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Verifica si la estalactita está activa.
     * @return true si está activa, false en caso contrario.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Si la estalactita está cayendo y colisiona con el jugador, retorna true.
     * @param playerBounds Área de colisión del jugador.
     * @return true si hay colisión en estado de caída; false en otro caso.
     */
    public boolean checkCollision(Rectangle playerBounds) {
        if (active && state == 2) {
            return getBounds().intersects(playerBounds);
        }
        return false;
    }

    /**
     * Reinicia la estalactita a su estado inicial.
     */
    public void reset() {
        this.x = initialX;
        this.y = initialY;
        this.active = true;
        this.state = 0;
        this.stateStartTime = System.currentTimeMillis();
        // Reiniciar el sonido, si es necesario, reiniciándolo al cargarlo de nuevo
        if (fallSound != null && fallSound.isRunning()) {
            fallSound.stop();
        }
    }

    /**
     * Detiene el sonido de caída de la estalactita si se está reproduciendo.
     */
    public void stopFallSound() {
        if (fallSound != null && fallSound.isRunning()) {
            fallSound.stop();
        }
    }

}
