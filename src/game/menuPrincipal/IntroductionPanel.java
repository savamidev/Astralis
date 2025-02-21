import game.menuPrincipal.Background;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IntroductionPanel extends JPanel {

    private JLabel nombreJuego;
    private CardLayout cardLayout;
    private JPanel panelContenedor;
    private Timer timerAnimacion;
    private Timer timerMenu;
    private Background background; // Fondo animado

    public IntroductionPanel(CardLayout cardLayout, JPanel panelContenedor) {
        this.cardLayout = cardLayout;
        this.panelContenedor = panelContenedor;


        background = new Background();
        setLayout(null);
        background.setBounds(0, 0, 800, 600);
        add(background);

        // Crear el nombre del juego
        nombreJuego = new JLabel("Quirk", SwingConstants.CENTER);
        nombreJuego.setFont(new Font("Pixel Font", Font.PLAIN, 50));
        nombreJuego.setForeground(new Color(1f, 1f, 1f, 0f));
        nombreJuego.setBounds(0, 100, 800, 100);
        add(nombreJuego);

        // Iniciar la animación y el cambio al menú
        iniciarAnimacion();
    }

    private void iniciarAnimacion() {
        timerAnimacion = new Timer(50, new ActionListener() {
            private int alpha = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (alpha < 255) {
                    alpha += 5;
                    nombreJuego.setForeground(new Color(1f, 1f, 1f, alpha / 255f));
                } else {
                    timerAnimacion.stop();
                    cambiarAMenu();
                }
            }
        });
        timerAnimacion.start();
    }

    private void cambiarAMenu() {
        timerMenu = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panelContenedor, "menu");
                timerMenu.stop();
            }
        });
        timerMenu.setRepeats(false);
        timerMenu.start();
    }
}

