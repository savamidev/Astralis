package game;

import game.panlesBBDD.modelsIntro.introPack.IntroMother;
import javax.swing.*;
import java.awt.*;

/**
 * RunnerOne es la clase principal que inicia la aplicación del juego.
 * <p>
 * Esta clase extiende JFrame y se encarga de configurar la interfaz gráfica principal,
 * estableciendo un contenedor con un CardLayout para gestionar la transición entre
 * la pantalla de introducción (IntroMother) y otros paneles del juego.
 * </p>
 */
public class RunnerOne extends JFrame {

    /**
     * Crea una instancia de RunnerOne y configura la interfaz gráfica.
     * <p>
     * Se invoca el método {@code initUI()} para inicializar todos los componentes
     * y establecer la ventana principal del juego.
     * </p>
     */
    public RunnerOne() {
        initUI();
    }

    /**
     * Configura la interfaz de usuario (UI) de la aplicación.
     * <p>
     * Este método establece el título, tamaño, operación de cierre, y posición de la ventana.
     * Además, crea un contenedor principal que utiliza un {@code CardLayout} para gestionar
     * los diferentes paneles de la aplicación, y agrega el panel de introducción (IntroMother).
     * </p>
     */
    private void initUI() {
        setTitle("Astralis"); // Establece el título de la ventana.
        setSize(1920, 1080);   // Establece el tamaño de la ventana (1920x1080).
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Finaliza la aplicación al cerrar la ventana.
        setLocationRelativeTo(null); // Centra la ventana en la pantalla.
        setResizable(false); // Impide que la ventana sea redimensionable.

        // Crea un CardLayout para intercambiar los paneles.
        CardLayout outerLayout = new CardLayout();
        // Crea un panel contenedor que utiliza el CardLayout.
        JPanel outerContainer = new JPanel(outerLayout);
        outerContainer.setOpaque(true); // Establece la opacidad del contenedor.

        // Crea el panel de introducción y lo agrega al contenedor con la etiqueta "IntroMother".
        IntroMother introMother = new IntroMother(outerLayout, outerContainer);
        outerContainer.add(introMother, "IntroMother");

        // Establece el contenedor principal de la ventana.
        setContentPane(outerContainer);
        // Muestra inicialmente el panel "IntroMother".
        outerLayout.show(outerContainer, "IntroMother");
        // Hace visible la ventana.
        setVisible(true);
    }

    /**
     * Método principal para iniciar la aplicación.
     * <p>
     * Se ejecuta en el hilo de eventos de Swing para asegurar que la UI se maneje correctamente.
     * </p>
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RunnerOne());
    }
}
