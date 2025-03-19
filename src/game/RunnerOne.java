package game;

import game.panlesBBDD.modelsIntro.introPack.IntroMother;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class RunnerOne extends JFrame {

    private Point initialClick;

    public RunnerOne() {
        initUI();
    }

    private void initUI() {
        // Elimina las decoraciones nativas para usar una barra personalizada.
        setUndecorated(true);
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ================================
        // Barra de título personalizada con degradado
        // ================================
        JPanel titleBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int width = getWidth();
                int height = getHeight();
                Color startColor = new Color(60, 63, 65);
                Color endColor = new Color(43, 43, 43);
                GradientPaint gp = new GradientPaint(0, 0, startColor, width, height, endColor);
                g2.setPaint(gp);
                g2.fillRect(0, 0, width, height);
                g2.dispose();
            }
        };
        titleBar.setPreferredSize(new Dimension(getWidth(), 40));
        titleBar.setOpaque(false);

        // ================================
        // Panel izquierdo: icono y título, alineados verticalmente
        // ================================
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        // Usamos BoxLayout para alinear en el eje X y centrar verticalmente cada componente.
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        // Cargar y escalar el icono (16x16)
        URL iconURL = getClass().getResource("/resources/imagen/LOGOTIPO.png");
        JLabel iconLabel = new JLabel();
        if (iconURL != null) {
            ImageIcon originalIcon = new ImageIcon(iconURL);
            Image scaledIcon = originalIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledIcon));
        }
        // Alinear verticalmente
        iconLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Etiqueta del título
        JLabel titleLabel = new JLabel(" Astralis");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Añadir icono y título al panel izquierdo
        leftPanel.add(iconLabel);
        leftPanel.add(titleLabel);

        // ================================
        // Panel derecho: solo botón de cerrar
        // ================================
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setOpaque(false);
        CircleButton closeButton = new CircleButton(new Color(255, 95, 86));
        closeButton.addActionListener(e -> System.exit(0));
        rightPanel.add(closeButton);

        // Añadir paneles izquierdo y derecho a la barra de título
        titleBar.add(leftPanel, BorderLayout.WEST);
        titleBar.add(rightPanel, BorderLayout.EAST);

        // ================================
        // Permitir mover la ventana arrastrando la barra de título
        // ================================
        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        // ================================
        // Panel de contenido principal
        // ================================
        CardLayout outerLayout = new CardLayout();
        JPanel contentPanel = new JPanel(outerLayout);
        contentPanel.setOpaque(true);
        IntroMother introMother = new IntroMother(outerLayout, contentPanel);
        contentPanel.add(introMother, "IntroMother");

        // Ensamblar el panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(titleBar, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setVisible(true);
    }

    // Clase interna para el botón circular de cerrar
    private class CircleButton extends JButton {
        private Color circleColor;

        public CircleButton(Color color) {
            this.circleColor = color;
            setPreferredSize(new Dimension(16, 16));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(circleColor);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RunnerOne());
    }
}
