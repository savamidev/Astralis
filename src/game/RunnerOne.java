package game;

import game.panlesBBDD.menuPrincipal.IntroMother;
import javax.swing.*;
import java.awt.*;

public class RunnerOne extends JFrame {

    public RunnerOne() {
        initUI();
    }

    private void initUI() {
        setTitle("Quirk");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Nivel externo: CardLayout para intercambiar IntroMother y VideoPanel
        CardLayout outerLayout = new CardLayout();
        JPanel outerContainer = new JPanel(outerLayout);
        outerContainer.setOpaque(false);

        // Solo se agrega la intro inicialmente.
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
