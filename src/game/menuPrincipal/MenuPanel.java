package game.menuPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel {

    public MenuPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);


        JLabel titulo = new JLabel("Nombre del Juego");
        titulo.setFont(new Font("Pixel Font", Font.PLAIN, 50));
        titulo.setForeground(Color.WHITE);
        add(titulo);
        add(Box.createVerticalStrut(50));

        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Pixel Font", Font.PLAIN, 30));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Iniciar Juego");
            }
        });
        add(startButton);

        add(Box.createVerticalStrut(20));


        JButton endButton = new JButton("End");
        endButton.setFont(new Font("Pixel Font", Font.PLAIN, 30));
        endButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        add(endButton);
    }
}
