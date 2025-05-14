package com.proyecto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 * Clase que representa el escenario o tablero del juego.
 * Gestiona la matriz de celdas, la visualización, la colocación de personajes y la carga desde archivo.
 * 
 * @author Nicolás
 * @version 1.0
 * @since 2025-04-22
 */
public class Escenario {

    /** Matriz de caracteres que representa el mapa. */
    private char[][] matriz;
    /** Número de filas del escenario. */
    private int filas;
    /** Número de columnas del escenario. */
    private int columnas;

    /**
     * Constructor. Crea el escenario por defecto.
     */
    public Escenario() {
        crearEscenarioPorDefecto();
    }

    /**
     * Indica si la celda dada es una pared.
     * @param fila Fila a comprobar
     * @param columna Columna a comprobar
     * @return true si es pared, false en caso contrario
     */
    public boolean esPared(int fila, int columna) {
        // Replace this logic with the actual implementation to check if a cell is a wall
        return getMatriz()[fila][columna] == '#';
    }

    /**
     * Crea el escenario por defecto (matriz fija).
     */
    private void crearEscenarioPorDefecto() {
        filas = 7;
        columnas = 20;
        matriz = new char[][] {
            "####################".toCharArray(),
            "#..................#".toCharArray(),
            "#..................#".toCharArray(),
            "#..................#".toCharArray(),
            "#..................#".toCharArray(),
            "#..................#".toCharArray(),
            "####################".toCharArray()
        };
    }

    /**
     * Genera la vista visual del escenario como un GridPane.
     * @return GridPane con las imágenes del escenario
     */
    public GridPane generarVista() {
        GridPane gridPane = new GridPane();
        Image sueloImagen = new Image(getClass().getResource("/com/imagenes/floor_light.png").toExternalForm());
        Image muroImagen = new Image(getClass().getResource("/com/imagenes/Wall_front.png").toExternalForm());

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                ImageView celda = matriz[i][j] == '#' ? new ImageView(muroImagen) : new ImageView(sueloImagen);
                celda.setFitWidth(32);
                celda.setFitHeight(70);
                gridPane.add(celda, j, i);
            }
        }

        return gridPane;
    }

    /**
     * Coloca un personaje en la posición indicada del GridPane y actualiza la matriz.
     * @param personaje Personaje a colocar
     * @param fila Fila destino
     * @param columna Columna destino
     * @param gridPane GridPane visual
     */
    public void colocarPersonaje(Personaje personaje, int fila, int columna, GridPane gridPane) {
        ImageView imagen = personaje.getImagen();

        // Limpiar visualmente la celda anterior
        gridPane.getChildren().removeIf(node -> {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeCol = GridPane.getColumnIndex(node);
            return nodeRow != null && nodeCol != null &&
                   nodeRow == personaje.getFila() && nodeCol == personaje.getColumna() &&
                   node instanceof ImageView &&
                   node.getUserData() != null && node.getUserData().equals("personaje");
        });

        // Limpiar la matriz en la posición anterior
        matriz[personaje.getFila()][personaje.getColumna()] = '.'; // o espacio vacío

        // Marcar la nueva posición del personaje en la matriz
        if (personaje instanceof Protagonista) {
            matriz[fila][columna] = 'P';
        } else if (personaje instanceof Enemigo) {
            matriz[fila][columna] = 'E';
        }

        // Marcar la imagen del personaje para el renderizado
        imagen.setUserData("personaje");
        gridPane.add(imagen, columna, fila);

        // Actualizar coordenadas del personaje
        personaje.setFila(fila);
        personaje.setColumna(columna);
    }

    /**
     * Carga el escenario desde un archivo de texto.
     * @param rutaArchivo Ruta del archivo dentro de resources
     * @throws IOException Si ocurre un error de lectura
     */
    public void cargarDesdeArchivo(String rutaArchivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(rutaArchivo)))) {
            List<String> lineas = reader.lines().collect(Collectors.toList());
            this.filas = lineas.size();
            this.columnas = lineas.get(0).length();
            this.matriz = new char[filas][columnas];

            for (int i = 0; i < filas; i++) {
                matriz[i] = lineas.get(i).toCharArray();
            }
        }
    }

    /**
     * Devuelve el número de filas del escenario.
     * @return Número de filas
     */
    public int getFilas() {
        return filas;
    }

    /**
     * Devuelve el número de columnas del escenario.
     * @return Número de columnas
     */
    public int getColumnas() {
        return columnas;
    }

    /**
     * Devuelve la matriz de caracteres del escenario.
     * @return Matriz del escenario
     */
    public char[][] getMatriz() {
        return matriz;
    }

    /**
     * Interfaz para observadores del escenario (patrón observador).
     */
    public interface Observador {
        /** Método llamado al actualizar el escenario. */
        void actualizar();
    }
}