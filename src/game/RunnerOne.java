package game;

import game.panlesBBDD.modelsIntro.introPack.IntroMother;
import javax.swing.*;
import java.awt.*;

public class RunnerOne extends JFrame {
    public RunnerOne() {
        initUI();
    }

    private void initUI() {
        setTitle("Astralis");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Crea un CardLayout para intercambiar los paneles.
        CardLayout outerLayout = new CardLayout();
        // Crea un panel contenedor que utiliza el CardLayout.
        JPanel outerContainer = new JPanel(outerLayout);
        outerContainer.setOpaque(true);

        // Crea el panel de introducción y lo agrega al contenedor.
        IntroMother introMother = new IntroMother(outerLayout, outerContainer);
        outerContainer.add(introMother, "IntroMother");

        setContentPane(outerContainer);
        outerLayout.show(outerContainer, "IntroMother");
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RunnerOne());
    }
}
