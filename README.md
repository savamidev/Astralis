# 🎮 Astralis: Documentación Técnica Completa del Juego

> **Versión:** 1.0.0  
> **Fecha:** 2025-03-19  
> **Autores:** [savamidev]  
> **Copyright:** © 2025  
> **Licencia:** Todos los derechos reservados.

---

## 📚 Tabla de Contenidos

- [Visión General](#visión-general)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Descripción de Paquetes y Clases](#descripción-de-paquetes-y-clases)
  - [game.objects](#gameobjects)
  - [game.controls.movements](#gamecontrolsmovements)
  - [game.effects](#gameeffects)
  - [game.audio](#gameaudio)
  - [game.panlesBBDD.map.colisionsTools](#gamepanlesbddmapcolisionstools)
  - [game.panlesBBDD.map.map1](#gamepanlesbddmapmap1)
  - [game.listeners](#gamelisteners)
  - [Niveles y Transiciones](#niveles-y-transiciones)
  - [game.video](#gamevideo)
  - [Introducción y Menú](#introducción-y-menú)
  - [Otros Componentes](#otros-componentes)
- [Flujo del Juego y Transiciones](#flujo-del-juego-y-transiciones)
- [Detalles Técnicos y Herramientas](#detalles-técnicos-y-herramientas)
- [Consideraciones Legales y Uso](#consideraciones-legales-y-uso)
- [Conclusión](#conclusión)

---

## 📖 Visión General

_Astralis_ es un juego 2D desarrollado en Java que combina acción, plataformas y efectos visuales avanzados. El juego se destaca por:

- **Animaciones fluidas:** Secuencias de imágenes que permiten transiciones suaves entre estados.
- **Efectos visuales dinámicos:** Simulación de niebla, hojas, lluvia, rayos y otros efectos mediante sistemas de partículas.
- **Integración de Audio y Video:** Uso de JavaFX para la reproducción de videos de transición y final, y de `javax.sound.sampled` para gestionar audio en bucle y con efectos de fade‑in.
- **Transiciones entre niveles:** Cada nivel cuenta con un ambiente propio y se conecta mediante diálogos, videos y efectos visuales.
- **Interacción con NPCs y Obstáculos:** Personajes como PortalNPC y FinalNPC gestionan mensajes y eventos que desencadenan transiciones y mecánicas letales.

Esta documentación ofrece una visión completa de la arquitectura, el flujo del juego y el diseño de cada uno de los módulos y clases del proyecto.

---

## 🗂️ Estructura del Proyecto

El código se organiza en múltiples paquetes, cada uno responsable de aspectos específicos del juego:

- **game.objects**  
  Define objetos fundamentales del juego, como coleccionables, NPCs y obstáculos (ej. estalactitas).

- **game.controls.movements**  
  Contiene la lógica del movimiento del jugador, las animaciones, la cámara y los paneles de juego para cada nivel.

- **game.effects**  
  Implementa efectos visuales avanzados mediante sistemas de partículas: niebla, hojas, lluvia, rayos, entre otros.

- **game.audio**  
  Gestiona la reproducción de música de fondo y efectos de sonido, aplicando técnicas de fade‑in para transiciones suaves.

- **game.panlesBBDD.map.colisionsTools**  
  Se encarga de la detección de colisiones a partir de un mapa de tiles, permitiendo interacciones precisas entre el jugador y el entorno.

- **game.panlesBBDD.map.map1**  
  Incluye componentes de la interfaz gráfica, como el diálogo de muerte estilizado.

- **game.listeners**  
  Define interfaces para la gestión de eventos y transiciones de niveles.

- **Niveles y Transiciones**  
  Contiene los paneles y contenedores para cada nivel (stageOne, stageTwo, stageThree) y gestiona las transiciones mediante `CardLayout`.

- **game.video**  
  Integra videos en la experiencia del juego (transición, intro y final) mediante JavaFX embebido en Swing.

- **Introducción y Menú**  
  Gestiona la pantalla de introducción, el audio y el menú principal con botones gráficos y efectos de fade‑in en el título.

- **Otros Componentes**  
  La clase principal (RunnerOne) inicia la aplicación en una ventana personalizada con una barra de título estilizada y navegación mediante `CardLayout`.

---

## 🔍 Descripción de Paquetes y Clases

### 🎁 game.objects

- **Collectible**  
  Representa los objetos que el jugador puede recoger, como sandía o botas. Cada objeto tiene un efecto de oscilación para hacerlo visualmente atractivo y se utiliza para modificar habilidades (doble salto o incremento de velocidad).

- **FinalNPC**  
  Define al NPC final que, al interactuar con el jugador, dispara la transición final mediante la reproducción de un video de cierre.

- **PortalNPC**  
  Gestiona la interacción y conversación con un NPC que guía al jugador a través de mensajes y activa transiciones hacia el siguiente nivel.

- **Stalactite**  
  Simula estalactitas que permanecen en estado de espera hasta que el jugador se acerca. Al detectar proximidad, vibran y luego caen, pudiendo matar al jugador si hay colisión. Incorpora efectos de sonido durante la caída.

---

### 🏃 game.controls.movements

- **AnimationPlayer**  
  Maneja la animación mediante la secuencia de frames de imágenes. Se encarga de actualizar el frame actual basado en un intervalo de tiempo, permitiendo animaciones cíclicas y fluidas.

- **Camera**  
  Calcula el desplazamiento (offset) para centrar al jugador en la pantalla sin que la vista se salga de los límites del mundo. Ajusta dinámicamente la vista conforme se mueve el jugador.

- **Player**  
  Gestiona el estado y movimiento del jugador. Controla acciones como caminar, saltar, correr, dash, y actualiza la animación y sonidos (pisadas, salto, aterrizaje). Implementa mecánicas de doble salto y dash condicionadas por los objetos recogidos.

- **GamePanel / GamePanelLevel2 / GamePanelLevel3**  
  Cada panel representa un nivel del juego. Se encargan de:
  - Actualizar la física del jugador.
  - Gestionar colisiones y efectos (partículas, niebla, lluvia, rayos, etc.).
  - Renderizar el entorno, fondos y objetos interactivos.
  - Coordinar la transición entre niveles mediante interacción con NPCs o colisiones letales.

---

### 🔥 game.effects

- **Particle & RainParticle**  
  Modelan partículas individuales con atributos de posición, velocidad, vida y tamaño. `RainParticle` extiende la funcionalidad para simular gotas de lluvia con variación en transparencia y velocidad.

- **LeafParticleEffect & RunGrassEffect**  
  Simulan partículas que representan hojas y pasto en movimiento. Estos efectos se generan en áreas específicas (por ejemplo, en los pies del jugador) para dar dinamismo al entorno.

- **FogParticleSystem**  
  Crea un sistema de partículas que simula niebla. Las partículas se generan y actualizan constantemente, creando una atmósfera densa y cambiante en función de la posición del jugador.

- **Lightning, LightningBranch & LightningSegment**  
  Conjuntamente, estas clases generan el efecto visual de un rayo. Se compone de un segmento principal y ramas laterales, cada una con grosor variable y leves perturbaciones para simular un rayo realista.

---

### 🔊 game.audio

- **BackgroundSound**  
  Reproduce la música de fondo del juego en bucle. Incorpora un efecto de fade‑in para aumentar gradualmente el volumen, proporcionando transiciones suaves al iniciar el nivel.

---

### 📐 game.panlesBBDD.map.colisionsTools

- **CollisionManager & TileMap**  
  El `TileMap` carga un mapa de tiles desde un archivo CSV (o genera uno por defecto) que define la geometría del mundo. El `CollisionManager` utiliza este mapa para determinar áreas de colisión, permitiendo que el jugador interactúe correctamente con el entorno y detecte colisiones letales.

---

### 💬 game.panlesBBDD.map.map1

- **DeathStyledDialog**  
  Es un diálogo de muerte estilizado que muestra una animación (GIF escalado) junto con música de muerte. Se activa cuando el jugador muere por colisión o caídas, y ofrece una transición para reiniciar el juego.

---

### 🔔 game.listeners

- **LevelTransitionListener**  
  Una interfaz que permite notificar a otros componentes cuando se solicita una transición de nivel, permitiendo una arquitectura desacoplada y modular en la gestión de eventos.

---

### 🎭 Niveles y Transiciones

- **PrinciPanel, PrinciPanelLevel2, PrinciPanelLevel3**  
  Cada uno de estos paneles es el contenedor principal para sus respectivos niveles. Utilizan un `CardLayout` para gestionar la transición entre niveles, que puede ser activada por interacciones con NPCs o eventos del juego (como colisiones o finalización de objetivos).

---

### 📹 game.video

- **TransitionVideoPanel & VideoFinal**  
  Estos paneles integran JavaFX en Swing a través de `JFXPanel` para reproducir videos en momentos clave del juego: transiciones entre niveles y el video final que concluye la experiencia.

---

### 🏠 Introducción y Menú

- **IntroAudio**  
  Gestiona la reproducción continua del audio de introducción, creando la atmósfera inicial del juego.

- **IntroMother**  
  Presenta la pantalla de introducción con una animación de fondo (GIF) y prepara la transición al menú principal.

- **MenuPanel**  
  Ofrece la interfaz del menú principal con botones gráficos estilizados (START y EXIT) que permiten al jugador iniciar el juego o salir.

- **Titulo**  
  Muestra el título del juego con un efecto de fade‑in, aportando un toque visual elegante y profesional.

- **VideoIntro & VideoPanel**  
  Reproducen el video introductorio que transita al panel principal del juego, integrando JavaFX para una experiencia multimedia completa.

---

### 🚀 Otros Componentes

- **RunnerOne**  
  Es la clase principal que arranca la aplicación. Inicia la ventana principal con una barra de título personalizada (degradado y botones circulares) y organiza la navegación mediante `CardLayout`.

---

## 🔄 Flujo del Juego y Transiciones

1. **Pantalla de Inicio e Introducción**
  - Se muestra la introducción mediante `IntroMother` y `Titulo`, con audio continuo gestionado por `IntroAudio`.
  - Después de un tiempo programado, se revela el `MenuPanel` con opciones para iniciar o salir.

2. **Transición al Juego**
  - Al pulsar el botón START, se detiene el audio introductorio y se reproduce un video de introducción mediante `VideoIntro` (integrado en `VideoPanel`).
  - Al finalizar el video, el juego transita al panel principal (`PrinciPanel`), iniciando el Nivel 1.

3. **Nivel 1**
  - En el `GamePanel`, el jugador interactúa con coleccionables que afectan sus habilidades (por ejemplo, doble salto o incremento de velocidad).
  - Se gestionan animaciones, colisiones y efectos visuales (partículas de hojas, oscilación de objetos) para ofrecer una experiencia inmersiva.

4. **Nivel 2**
  - En `GamePanelLevel2`, el ambiente se enriquece con efectos de lluvia, advertencias visuales de rayos y la interacción con el `PortalNPC`.
  - Una vez activado, el NPC dispara la transición a través de un video de transición (`TransitionVideoPanel`).

5. **Nivel 3 y Final**
  - En `GamePanelLevel3`, se utiliza un `TileMap` para definir el entorno.
  - Se aplican efectos ambientales como niebla (con `FogParticleSystem`) y obstáculos letales (estalactitas).
  - La interacción con el `FinalNPC` dispara el video final (`VideoFinal`), que concluye la experiencia del juego.

---

## ⚙️ Detalles Técnicos y Herramientas

- **Herramientas Utilizadas:**
  - **Java SE & JDK 17:** Base de desarrollo y compilación.
  - **Swing & JavaFX:** Creación de interfaces gráficas y reproducción de video embebido.
  - **javax.sound.sampled:** Gestión de audio, reproducción en bucle y efectos de transición.
  - **Herramientas de Edición:** Software para la edición y creación de imágenes, GIFs y videos (para recursos visuales de alta calidad).

- **Patrones de Diseño Aplicados:**
  - **MVC (Modelo-Vista-Controlador):** Separa la lógica del juego de la interfaz de usuario para facilitar mantenimiento y escalabilidad.
  - **Observer/Listener:** Uso de `LevelTransitionListener` para notificar eventos de cambio de nivel de manera desacoplada.
  - **Factory:** Métodos de creación para partículas en sistemas como `RainParticle`, generando efectos dinámicos de forma modular.

- **Recursos Visuales y UI:**
  - Se han integrado iconos, imágenes en formato PNG y GIF, y videos en MP4 para ofrecer una experiencia visual atractiva.
  - La interfaz utiliza elementos con transparencia, degradados y animaciones de fade‑in que aportan un aspecto moderno y profesional.

---

## 📌 Consideraciones Legales y Uso

- **Copyright:**  
  Todos los recursos gráficos, de audio y video de _Astralis_ son propiedad exclusiva del autor.
- **Licencia:**  
  Este proyecto está protegido por derechos de autor y su reproducción o uso comercial requiere autorización previa.
- **Uso del Proyecto:**  
  _Astralis_ se ha desarrollado para demostrar técnicas avanzadas en programación y diseño de videojuegos. Cualquier modificación o distribución debe mantener la atribución original.

---

## 📝 Conclusión

_Astralis_ es un juego innovador que integra animaciones fluidas, efectos visuales dinámicos y audio envolvente para ofrecer una experiencia de juego única. La arquitectura modular y el uso de patrones de diseño facilitan su mantenimiento y futuras expansiones, permitiendo la integración de nuevos niveles, efectos y mejoras de rendimiento.

Esta documentación técnica en Markdown abarca desde la visión general y la estructura del proyecto hasta una descripción detallada de cada módulo y el flujo completo del juego. Se espera que este documento sirva de guía para la comprensión, mantenimiento y evolución del proyecto.

---

**Contacto:**  
Para consultas o colaboraciones, puedes escribir a [tuemail@dominio.com](mailto:tuemail@dominio.com).

---

© 2025 Astralis Studios. Todos los derechos reservados.
