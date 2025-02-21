package game;

import game.menuPrincipal.Background;
import game.menuPrincipal.MenuPanel;

import javax.swing.*;
import java.awt.*;

public class RunnerOne extends JFrame {

    private static int FRAME_WIDTH = 1920;
    private static int FRAME_HEIGHT = 1080;

    private CardLayout cardLayout;
    private JPanel panelContenedor;


    public RunnerOne() {

        setTitle("Juego - Menú Principal");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);
        panelContenedor.setLayout(null);


        setContentPane(new Background());
        
        setVisible(true);
    }



}
