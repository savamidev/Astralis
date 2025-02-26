package game;

import game.panlesBBDD.modelsIntro.introPack.IntroMother;
import javax.swing.*;
import java.awt.*;

public class RunnerOne extends JFrame {

    // Constructor de RunnerOne, que inicializa la interfaz gráfica
    public RunnerOne() {
        initUI(); // Llama al método initUI para configurar la interfaz
    }

    // Configura la interfaz de usuario (UI)
    private void initUI() {
        setTitle("Quirk"); // Establece el título de la ventana
        setSize(1920, 1080); // Establece el tamaño de la ventana (1920x1080)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Define la acción cuando se cierra la ventana (termina la aplicación)
        setLocationRelativeTo(null); // Centra la ventana en la pantalla

        // Nivel externo: CardLayout para intercambiar los paneles (IntroMother y VideoPanel)
        CardLayout outerLayout = new CardLayout(); // Crea un objeto CardLayout para gestionar los paneles
        JPanel outerContainer = new JPanel(outerLayout); // Crea un panel que utiliza el CardLayout
        outerContainer.setOpaque(false); // Hace que el panel de contenedor sea transparente

        // Solo se agrega la pantalla de introducción (IntroMother) inicialmente
        IntroMother introMother = new IntroMother(outerLayout, outerContainer); // Crea el panel de introducción
        outerContainer.add(introMother, "IntroMother"); // Añade el panel de introducción al contenedor con una etiqueta "IntroMother"

        setContentPane(outerContainer); // Establece el contenedor principal de la ventana
        outerLayout.show(outerContainer, "IntroMother"); // Muestra el panel "IntroMother" al inicio
        setVisible(true); // Hace visible la ventana
    }

    // Método principal para ejecutar la aplicación
    public static void main(String[] args) {
        // Se ejecuta el código en el hilo de eventos de Swing para asegurar que la UI se maneje correctamente
        SwingUtilities.invokeLater(() -> new RunnerOne()); // Crea una nueva instancia de RunnerOne (la ventana del juego)
    }
}
