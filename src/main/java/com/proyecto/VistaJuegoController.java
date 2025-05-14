package com.proyecto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.GridPane;

/**
 * Controlador de la vista principal del juego.
 * Gestiona el tablero, el movimiento, el combate y el flujo de la partida.
 * 
 * @author Nicolás
 * @version 1.0
 * @since 2025-04-22
 */
public class VistaJuegoController {

    /** GridPane que representa el escenario visual. */
    @FXML
    private GridPane escenario;

    /** Labels de estadísticas del protagonista. */
    @FXML
    private Label nombreProtagonista, ataqueProtagonista, defensaProtagonista, turnoActual;

    /** Barra de vida del protagonista. */
    @FXML
    private ProgressBar vidaProtagonista;

    /** Labels y barra de vida del enemigo seleccionado. */
    @FXML
    private Label nombreEnemigo, ataqueEnemigo, defensaEnemigo;
    @FXML
    private ProgressBar vidaEnemigo;

    /** Modelo de escenario. */
    private Escenario esc;
    /** Protagonista del juego. */
    private Protagonista protagonista;
    /** Lista de enemigos activos. */
    private List<Enemigo> enemigos;
    /** Índice del turno actual. */
    private int turnoActualIndex;
    /** Tamaño de celda en píxeles. */
    private static final int TAMANO_CELDA = 32; // Tamaño de celda en píxeles
    /** Enemigo actualmente en combate. */
    private Enemigo enemigoEnCombate = null;
    /** Indica si se está en combate por turnos. */
    private boolean enCombate = false;
    /** Controla si ya se realizó un clic este turno. */
    private boolean clicRealizadoEsteTurno = false;
    /** Contador de enemigos derrotados. */
    private int enemigosMatados = 0; // Contador de enemigos derrotados

    /**
     * Inicializa la vista y el modelo al cargar la pantalla de juego.
     */
    @FXML
    public void initialize() {
        escenario.setFocusTraversable(true);
        escenario.requestFocus();

        esc = App.getEscenario();
        if (esc == null) {
            esc = new Escenario();
            App.setEscenario(esc);
        }

        GridPane vista = esc.generarVista();
        escenario.getChildren().clear();
        escenario.getChildren().addAll(vista.getChildren());

        protagonista = App.getProtagonista();
        // Validación de posición del protagonista
        boolean posicionValida = true;
        if (protagonista != null) {
            int f = protagonista.getFila();
            int c = protagonista.getColumna();
            if (f < 0 || f >= esc.getFilas() || c < 0 || c >= esc.getColumnas() || esc.esPared(f, c)) {
                posicionValida = false;
            }
        }
        if (protagonista == null) {
            // Si no existe, crear uno por defecto
            protagonista = new Protagonista("Heroe", 100, 20, 10, 5);
            App.setProtagonista(protagonista);
        }
        // Si la posición no es válida, solo ajustar la posición, NO crear un nuevo protagonista
        if (!posicionValida) {
            // Buscar la primera celda libre
            int filaLibre = 1, colLibre = 1;
            outer: for (int i = 0; i < esc.getFilas(); i++) {
                for (int j = 0; j < esc.getColumnas(); j++) {
                    if (!esc.esPared(i, j)) {
                        filaLibre = i;
                        colLibre = j;
                        break outer;
                    }
                }
            }
            protagonista.setFila(filaLibre);
            protagonista.setColumna(colLibre);
            esc.colocarPersonaje(protagonista, filaLibre, colLibre, escenario);
        } else {
            esc.colocarPersonaje(protagonista, protagonista.getFila(), protagonista.getColumna(), escenario);
        }
        actualizarInformacionProtagonista(protagonista);

        enemigos = cargarEnemigos();
        // Validación de posición para cada enemigo
        List<int[]> posicionesOcupadas = new ArrayList<>();
        posicionesOcupadas.add(new int[]{protagonista.getFila(), protagonista.getColumna()});
        for (Enemigo enemigo : enemigos) {
            boolean posValida = true;
            int f = enemigo.getFila();
            int c = enemigo.getColumna();
            // Verifica si está fuera de límites o en una pared o en la misma celda que el protagonista u otro enemigo ya colocado
            if (f < 0 || f >= esc.getFilas() || c < 0 || c >= esc.getColumnas() || esc.esPared(f, c)) {
                posValida = false;
            } else {
                for (int[] pos : posicionesOcupadas) {
                    if (pos[0] == f && pos[1] == c) {
                        posValida = false;
                        break;
                    }
                }
            }
            if (!posValida) {
                // Buscar la primera celda libre que no sea pared ni esté ocupada
                outer: for (int i = 0; i < esc.getFilas(); i++) {
                    for (int j = 0; j < esc.getColumnas(); j++) {
                        if (esc.esPared(i, j)) continue;
                        boolean ocupada = false;
                        for (int[] pos : posicionesOcupadas) {
                            if (pos[0] == i && pos[1] == j) {
                                ocupada = true;
                                break;
                            }
                        }
                        if (!ocupada) {
                            enemigo.setFila(i);
                            enemigo.setColumna(j);
                            break outer;
                        }
                    }
                }
            }
            posicionesOcupadas.add(new int[]{enemigo.getFila(), enemigo.getColumna()});
            esc.colocarPersonaje(enemigo, enemigo.getFila(), enemigo.getColumna(), escenario);
            enemigo.iniciarMovimientoAutomatico(esc, protagonista, escenario, enemigos);
        }

        turnoActualIndex = 0;
        actualizarTurno();

        iniciarMovimientoEnemigos();
    }

    /**
     * Gestiona el movimiento del protagonista según la tecla pulsada.
     * @param event Evento de teclado
     */
    @FXML
    private void manejarMovimientoProtagonista(KeyEvent event) {
        if (protagonista == null || turnoActualIndex != 0) return; // Solo mover si es el turno del protagonista

        int nuevaFila = protagonista.getFila();
        int nuevaColumna = protagonista.getColumna();

        switch (event.getCode()) {
            case W:
            case UP:
                nuevaFila--;
                break;
            case S:
            case DOWN:
                nuevaFila++;
                break;
            case A:
            case LEFT:
                nuevaColumna--;
                break;
            case D:
            case RIGHT:
                nuevaColumna++;
                break;
            default:
                return;
        }

        // Si estamos en combate, solo permitir atacar al enemigoEnCombate si está en la dirección pulsada
        if (enCombate && enemigoEnCombate != null) {
            int ef = enemigoEnCombate.getFila();
            int ec = enemigoEnCombate.getColumna();
            // Solo permitir atacar si el movimiento es hacia la casilla del enemigo en combate
            if (nuevaFila == ef && nuevaColumna == ec) {
                ataqueTurnoCombate();
            }
            return;
        }

        // Si hay un enemigo en la casilla destino, inicia el combate (ataque de turno)
        Enemigo enemigo = obtenerEnemigoEnPosicion(nuevaFila, nuevaColumna);
        if (enemigo != null && enemigo.getSalud() > 0) {
            iniciarCombate(enemigo);
            return;
        }

        // Validar que no sea muro ni casilla ocupada por enemigo
        if (!esPosicionLibreParaProtagonista(nuevaFila, nuevaColumna)) {
            System.out.println("Movimiento bloqueado por obstáculo o enemigo.");
            return;
        }

        // if para que la trampilla te quite 20 de vida
    if (esc.getMatriz()[nuevaFila][nuevaColumna] == 'T') {
        int nuevaSalud = protagonista.getSalud() - 20;
        protagonista.setSalud(Math.max(0, nuevaSalud));
        esc.getMatriz()[nuevaFila][nuevaColumna] = '.';
        actualizarInformacionProtagonista(protagonista);
    if (protagonista.getSalud() <= 0) {
        mostrarGameOver();
        return;
    }
}

        
        protagonista.mover(nuevaFila, nuevaColumna, true);
        esc.colocarPersonaje(protagonista, nuevaFila, nuevaColumna, escenario);

        actualizarInformacionProtagonista(protagonista);
        avanzarTurno();
    }

    /**
     * Valida si una posición es libre para el protagonista.
     * @param fila Fila destino
     * @param columna Columna destino
     * @return true si es libre, false si está ocupada o es muro
     */
    private boolean esPosicionLibreParaProtagonista(int fila, int columna) {
        if (fila < 0 || fila >= esc.getFilas() || columna < 0 || columna >= esc.getColumnas())
            return false;
        if (esc.getMatriz()[fila][columna] == '#')
            return false;
        for (Enemigo enemigo : enemigos) {
            if (enemigo.getFila() == fila && enemigo.getColumna() == columna && enemigo.getSalud() > 0)
                return false;
        }
        return true;
    }

    /**
     * Realiza un ataque entre dos personajes y gestiona el resultado.
     * @param atacante Personaje que ataca
     * @param defensor Personaje que recibe el ataque
     */
    private void realizarAtaque(Personaje atacante, Personaje defensor) {
        int danio;
        if (atacante instanceof Enemigo) {
            danio = 20; // Daño fijo para el enemigo
        } else {
            danio = Math.max(0, atacante.getAtaque() - defensor.getDefensa());
        }
        defensor.setSalud(defensor.getSalud() - danio);
        // Verificar si el defensor murió
        if (defensor.getSalud() <= 0) {
            if (defensor instanceof Enemigo) {
                eliminarEnemigoDelMapa((Enemigo)defensor);
            } else if (defensor == protagonista) {
                mostrarGameOver();
            }
        }
        // Solo mostrar mensaje si NO estamos en combate y el atacante es el protagonista,
        // o si el atacante es un enemigo y no estamos en combate por turnos
        boolean mostrar = true;
        if (enCombate) {
            // Solo mostrar el mensaje si el atacante es el jugador
            mostrar = !(atacante instanceof Enemigo);
        }
        if (mostrar) {
            String mensaje = atacante.getNombre() + " atacó a " + defensor.getNombre() +
                    " causando " + danio + " de daño.\n" +
                    "Salud restante de " + defensor.getNombre() + ": " + defensor.getSalud();
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Combate");
                alert.setHeaderText(null);
                alert.setContentText(mensaje);
                alert.showAndWait();
            });
        }
    }

    /**
     * Inicia el sistema de turnos de enemigos (placeholder).
     */
    private void iniciarMovimientoEnemigos() {
        // Este método ahora solo inicia el sistema de turnos
        // Los enemigos se moverán/atacarán cuando sea su turno a través de avanzarTurno()
    }

    /**
     * Mueve a los enemigos según el turno y lógica de IA.
     */
    private void moverEnemigos() {
        if (enCombate) return; // Si estamos en combate por turnos, no mover enemigos
        for (Enemigo enemigo : enemigos) {
            if (enemigo.getSalud() <= 0) continue;
            // Es el turno de este enemigo específico (basado en turnoActualIndex)
            if (enemigos.indexOf(enemigo) + 1 == turnoActualIndex) {
                // Verificar si está adyacente al protagonista para atacar
                if (calcularDistancia(enemigo, protagonista) == 1) {
                    realizarAtaque(enemigo, protagonista);
                    actualizarBarraVida(vidaProtagonista, protagonista.getSalud() / 100.0);
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Turno Enemigo");
                    alert.setHeaderText(null);
                    alert.setContentText(enemigo.getNombre() + " te ha atacado durante su turno!");
                    alert.showAndWait();
                    if (protagonista.getSalud() <= 0) {
                        mostrarGameOver();
                        return;
                    }
                    // NO avanzar turno aquí, solo termina la acción del enemigo
                    return;
                }
                // Lógica de movimiento normal...
                int nuevaFila = enemigo.getFila();
                int nuevaColumna = enemigo.getColumna();
                if (calcularDistancia(enemigo, protagonista) <= enemigo.getPercepcion()) {
                    if (protagonista.getFila() > nuevaFila) nuevaFila++;
                    else if (protagonista.getFila() < nuevaFila) nuevaFila--;
                    if (protagonista.getColumna() > nuevaColumna) nuevaColumna++;
                    else if (protagonista.getColumna() < nuevaColumna) nuevaColumna--;
                } else {
                    Random random = new Random();
                    int direccion = random.nextInt(4);
                    switch (direccion) {
                        case 0: nuevaFila--; break;
                        case 1: nuevaFila++; break;
                        case 2: nuevaColumna--; break;
                        case 3: nuevaColumna++; break;
                    }
                }
                if (esPosicionLibreParaEnemigo(nuevaFila, nuevaColumna, enemigo)) {
                    enemigo.mover(nuevaFila, nuevaColumna);
                    esc.colocarPersonaje(enemigo, nuevaFila, nuevaColumna, escenario);
                }
            }
        }
    }

    /**
     * Valida si una posición es libre para un enemigo.
     * @param fila Fila destino
     * @param columna Columna destino
     * @param enemigoActual Enemigo que intenta moverse
     * @return true si es libre, false si está ocupada o es muro
     */
    private boolean esPosicionLibreParaEnemigo(int fila, int columna, Enemigo enemigoActual) {
        if (fila < 0 || fila >= esc.getFilas() || columna < 0 || columna >= esc.getColumnas())
            return false;
        if (esc.getMatriz()[fila][columna] == '#')
            return false;
        // No puede pisar al protagonista
        if (protagonista.getFila() == fila && protagonista.getColumna() == columna)
            return false;
        // No puede pisar a otro enemigo
        for (Enemigo otro : enemigos) {
            if (otro != enemigoActual && otro.getFila() == fila && otro.getColumna() == columna && otro.getSalud() > 0)
                return false;
        }
        return true;
    }

    /**
     * Devuelve el enemigo en una posición dada, o null si no hay ninguno.
     * @param fila Fila
     * @param columna Columna
     * @return Enemigo en esa posición o null
     */
    private Enemigo obtenerEnemigoEnPosicion(int fila, int columna) {
        for (Enemigo enemigo : enemigos) {
            if (enemigo.getFila() == fila && enemigo.getColumna() == columna) {
                return enemigo;
            }
        }
        return null;
    }

    /**
     * Calcula la distancia Manhattan entre dos personajes.
     * @param p1 Primer personaje
     * @param p2 Segundo personaje
     * @return Distancia Manhattan
     */
    private int calcularDistancia(Personaje p1, Personaje p2) {
        return Math.abs(p1.getFila() - p2.getFila()) + Math.abs(p1.getColumna() - p2.getColumna());
    }

    /**
     * Avanza el turno al siguiente personaje (jugador o enemigo).
     */
    private void avanzarTurno() {
        // Si estamos en combate, NO avanzar turno automáticamente
        if (enCombate) return;
        turnoActualIndex = (turnoActualIndex + 1) % (enemigos.size() + 1);
        // Ajustar si el índice es mayor que el número de enemigos vivos
        if (turnoActualIndex > enemigos.size()) {
            turnoActualIndex = 0;
        }
        clicRealizadoEsteTurno = false;
        actualizarTurno();
        // Si es turno de un enemigo, mover/atacar inmediatamente
        if (turnoActualIndex != 0) {
            // Verificar que el enemigo en este turno aún existe
            if (turnoActualIndex - 1 < enemigos.size()) {
                moverEnemigos();
            } else {
                // Si el enemigo ya no existe, pasar al siguiente turno
                avanzarTurno();
            }
        }
    }

    /**
     * Actualiza el texto del turno actual en la interfaz.
     */
    private void actualizarTurno() {
        if (turnoActualIndex == 0) {
            turnoActual.setText("Turno: Protagonista");
        } else {
            turnoActual.setText("Turno: Enemigo " + turnoActualIndex);
        }
    }


    
    /**
     * Carga la lista de enemigos iniciales.
     * @return Lista de enemigos
     */
    private List<Enemigo> cargarEnemigos() {
        List<Enemigo> enemigos = new ArrayList<>();
        enemigos.add(new Enemigo("Slime1", 50, 10, 5, 2, 3, 3));
        enemigos.add(new Enemigo("Slime2", 80, 15, 8, 2, 5, 5));
        return enemigos;
    }

    /**
     * Actualiza la información visual del protagonista.
     * @param protagonista Protagonista a mostrar
     */
    private void actualizarInformacionProtagonista(Protagonista protagonista) {
        nombreProtagonista.setText("Nombre: " + protagonista.getNombre());
        ataqueProtagonista.setText("Ataque: " + protagonista.getAtaque());
        defensaProtagonista.setText("Defensa: " + protagonista.getDefensa());
        vidaProtagonista.setProgress(protagonista.getSalud() / 100.0);
    }

    /**
     * Verifica el estado del juego (victoria o derrota).
     */
    private void verificarEstadoJuego() {
        if (protagonista.getSalud() <= 0) {
            mostrarGameOver();
        } else if (enemigos.isEmpty()) {
            mostrarVictoria();
        }
    }

    /**
     * Muestra la pantalla de Game Over.
     */
    private void mostrarGameOver() {
        Platform.runLater(() -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/proyecto/vistaGameOver.fxml"));
                javafx.scene.Parent root = loader.load();
                VistaGameOverController controller = loader.getController();
                controller.setEnemigosMatados(enemigosMatados);
                // Cambiar la raíz de la escena principal usando el GridPane escenario
                escenario.getScene().setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Muestra la pantalla de victoria (no usada, fin de juego es Game Over).
     */
    private void mostrarVictoria() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gestiona el ataque del protagonista durante el combate por turnos.
     */
    private void ataqueTurnoCombate() {
        if (!enCombate || enemigoEnCombate == null) return;
        // El jugador ataca
        realizarAtaque(protagonista, enemigoEnCombate);
        actualizarBarraVida(vidaEnemigo, enemigoEnCombate.getSalud() / (double)enemigoEnCombate.getSaludMaxima());
        if (enemigoEnCombate.getSalud() <= 0) {
            eliminarEnemigoDelMapa(enemigoEnCombate);
            enCombate = false;
            enemigoEnCombate = null;
            verificarEstadoJuego();
            return;
        }
        // Deshabilitar input del jugador hasta que el enemigo termine su ataque
        escenario.setDisable(true);
        Timeline enemigoAtaca = new Timeline(
            new KeyFrame(Duration.seconds(0.7), e -> {
                // El enemigo ataca SOLO, no debe haber ningún ataque automático del protagonista después
                realizarAtaque(enemigoEnCombate, protagonista);
                actualizarBarraVida(vidaProtagonista, protagonista.getSalud() / 100.0);
                if (protagonista.getSalud() <= 0) {
                    mostrarGameOver();
                    enCombate = false;
                    escenario.setDisable(false);
                    return;
                }
                // Rehabilitar input del jugador tras el ataque del enemigo
                escenario.setDisable(false);
                // NO LLAMAR a ataqueTurnoCombate ni a avanzarTurno aquí
            })
        );
        enemigoAtaca.setCycleCount(1);
        enemigoAtaca.play();
    }

    /**
     * Inicia el combate por turnos contra un enemigo.
     * @param enemigoObjetivo Enemigo a combatir
     */
    private void iniciarCombate(Enemigo enemigoObjetivo) {
        enCombate = true;
        enemigoEnCombate = enemigoObjetivo;
        mostrarInformacionEnemigo(enemigoObjetivo);
        
        // Mostrar mensaje indicando que es el turno del jugador
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Combate");
        alert.setHeaderText(null);
        alert.setContentText("¡Combate iniciado! Es tu turno de atacar.");
        alert.showAndWait();
    }

    /**
     * Muestra la información del enemigo seleccionado.
     * @param enemigo Enemigo a mostrar
     */
    private void mostrarInformacionEnemigo(Enemigo enemigo) {
        Platform.runLater(() -> {
            nombreEnemigo.setText("Nombre: " + enemigo.getNombre());
            ataqueEnemigo.setText("Ataque: " + enemigo.getAtaque());
            defensaEnemigo.setText("Defensa: " + enemigo.getDefensa());
            vidaEnemigo.setProgress(enemigo.getSalud() / (double)enemigo.getSaludMaxima());
        });
    }

    /**
     * Actualiza la barra de vida de un personaje.
     * @param barra Barra de progreso
     * @param progreso Valor de progreso (0-1)
     */
    private void actualizarBarraVida(ProgressBar barra, double progreso) {
        Platform.runLater(() -> {
            barra.setProgress(progreso);
            if (progreso < 0.2) {
                barra.setStyle("-fx-accent: red;");
            } else if (progreso < 0.5) {
                barra.setStyle("-fx-accent: orange;");
            } else {
                barra.setStyle("-fx-accent: green;");
            }
        });
    }

    /**
     * Elimina completamente a un enemigo del juego y verifica el fin de la partida.
     * @param enemigo Enemigo a eliminar
     */
    private void eliminarEnemigoDelMapa(Enemigo enemigo) {
        // Detener cualquier movimiento automático del enemigo
        enemigo.detenerMovimiento();
        // Eliminar de la lista de enemigos
        enemigos.remove(enemigo);
        // Eliminar visualmente del mapa
        Platform.runLater(() -> {
            escenario.getChildren().remove(enemigo.getImagen());
            esc.getMatriz()[enemigo.getFila()][enemigo.getColumna()] = '.';
        });
        // Limpiar la información de combate si este era el enemigo en combate
        if (enemigo == enemigoEnCombate) {
            nombreEnemigo.setText("Nombre: ");
            ataqueEnemigo.setText("Ataque: ");
            defensaEnemigo.setText("Defensa: ");
            vidaEnemigo.setProgress(0);
            enemigoEnCombate = null;
            enCombate = false;
        }
        enemigosMatados++;
        // Si ya no quedan enemigos, mostrar Game Over (fin del juego)
        if (enemigos.isEmpty()) {
            mostrarGameOver();
        }
    }
}