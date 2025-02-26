# Quirk

Quirk es un juego 2D desarrollado en Java que integra diversas tecnologías y módulos para ofrecer una experiencia interactiva con animaciones, audio y video. Este repositorio contiene el código fuente completo, organizado en paquetes, que implementan la lógica del juego, la interfaz gráfica y la gestión de recursos multimedia.

---

## Índice

- [Características](#características)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Tecnologías y Herramientas](#tecnologías-y-herramientas)
- [Descripción Técnica](#descripción-técnica)
  - [Pantalla de Introducción](#pantalla-de-introducción)
  - [Reproducción de Audio y Video](#reproducción-de-audio-y-video)
  - [Sistema de Animación y Movimiento](#sistema-de-animación-y-movimiento)
  - [Integración y Flujo Principal](#integración-y-flujo-principal)
- [Cómo Ejecutar](#cómo-ejecutar)
- [Licencia](#licencia)

---

## Características

- **Pantalla de Introducción Dinámica:**  
  - Animación de título con efecto *fade-in* y *fade-out*.
  - Menú interactivo con botones para iniciar el juego o salir.
  - Reproducción continua de audio de introducción.

- **Reproducción Multimedia:**  
  - Integración de JavaFX en Swing para reproducir videos.
  - Cambio automático de la pantalla de video al finalizar la reproducción.

- **Sistema de Animación y Movimiento:**  
  - Animaciones basadas en secuencias de imágenes para diferentes estados del jugador (idle, caminar, saltar, caer).
  - Control de física básica (gravedad, velocidad, límites de pantalla) y detección de colisiones con el suelo.

- **Interfaz de Usuario en Java Swing:**  
  - Uso de `CardLayout` para gestionar la navegación entre diferentes paneles (introducción, video, juego).
  - Panel de juego que implementa un bucle de actualización a ~60 FPS.

---

## Estructura del Proyecto


---

## Tecnologías y Herramientas

- **Java SE:**  
  Implementación de la lógica del juego, interfaz gráfica y multimedia.

- **Swing:**  
  Desarrollo de la interfaz gráfica y gestión de eventos.

- **JavaFX:**  
  Integración con Swing a través de `JFXPanel` para la reproducción de video.

- **CardLayout:**  
  Gestión dinámica de pantallas y paneles para la navegación en la aplicación.

---

## Descripción Técnica

### Pantalla de Introducción

- **IntroMother.java:**  
  Actúa como panel principal para la introducción. Configura un `BorderLayout` y carga una imagen de fondo. Inicia la reproducción del audio mediante la clase `IntroAudio`.  
  - **CardLayout Interno:** Se utiliza para la transición entre el panel del título (*Titulo.java*) y el menú (*MenuPanel.java*).

- **Titulo.java:**  
  Muestra la imagen del título y utiliza un `Timer` para crear efectos de *fade-in* y *fade-out*. Al finalizar la animación, se cambia automáticamente al menú de botones.

- **MenuPanel.java:**  
  Presenta dos botones:
  - **START:** Detiene el audio, crea (si es necesario) y cambia al panel de video.
  - **EXIT:** Finaliza la aplicación.

### Reproducción de Audio y Video

- **IntroAudio.java:**  
  Utiliza la API `javax.sound.sampled` para cargar y reproducir archivos de audio. Reproduce el audio en bucle y ofrece métodos para iniciar y detener la reproducción.

- **VideoIntro.java:**  
  Integra JavaFX en Swing mediante `JFXPanel` para reproducir un video. Configura `MediaPlayer` y `MediaView` para mostrar el video en pantalla completa. Al finalizar el video, se regresa al panel de introducción.

- **VideoPanel.java:**  
  Contiene la instancia de `VideoIntro` y se encarga de iniciar la reproducción del video al mostrarse.

### Sistema de Animación y Movimiento

- **AnimationPlayer.java:**  
  Gestiona las animaciones del jugador alternando entre distintos frames (imágenes) basados en un retardo definido.

- **Player.java:**  
  Define la lógica del jugador, incluyendo:
  - **Estados del Jugador:** Idle, caminar hacia la izquierda/derecha, saltar y caer.
  - **Física Básica:** Velocidad, fuerza del salto, gravedad y límites del suelo.
  - **Carga y Escalado de Imágenes:** Se cargan imágenes desde recursos y se escalan para las animaciones.

- **GamePanel.java:**  
  Implementa el bucle de juego mediante un `Timer` que actualiza la lógica del jugador, procesa la entrada del teclado y redibuja la pantalla. Permite mover el jugador en todas las direcciones y ejecutar acciones como saltar o detener el movimiento.

### Integración y Flujo Principal

- **RunnerOne.java:**  
  Es el punto de entrada de la aplicación. Configura un `JFrame` con un `CardLayout` para alternar entre las distintas pantallas del juego (por ejemplo, la introducción y el video).  
  - Inicialmente se muestra el panel de introducción (`IntroMother`), que permite la transición hacia otros módulos (como el video o el juego).

---

## Cómo Ejecutar

1. **Requisitos:**
   - Java SE 8 o superior.
   - Configuración adecuada para utilizar JavaFX (incluido en Java 8; en versiones posteriores puede requerir dependencias adicionales).

2. **Compilación:**
   - Clona el repositorio:
     ```bash
     git clone https://github.com/tu-usuario/tu-repositorio.git
     ```
   - Navega al directorio del proyecto y compílalo utilizando tu IDE favorito (como IntelliJ IDEA o Eclipse) o desde la línea de comandos:
     ```bash
     javac -d bin src/**/*.java
     ```

3. **Ejecución:**
   - Ejecuta la clase principal `RunnerOne`:
     ```bash
     java -cp bin game.RunnerOne
     ```
   - La ventana del juego se abrirá en resolución 1920x1080, mostrando la pantalla de introducción con audio, animaciones y la opción de iniciar el video o salir.

---

## Licencia

Distribuye la licencia que corresponda a tu proyecto. Por ejemplo, si utilizas la Licencia MIT:

```text
MIT License

Copyright (c)

Permission is hereby granted, free of charge, to any person obtaining a copy...
