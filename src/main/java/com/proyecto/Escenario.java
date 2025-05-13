package com.proyecto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Escenario {

    private char[][] matriz;
    private int filas;
    private int columnas;

    public Escenario() {
        crearEscenarioPorDefecto();
    }
  public boolean esPared(int fila, int columna) {
        // Replace this logic with the actual implementation to check if a cell is a wall
        return getMatriz()[fila][columna] == '#';
    }
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
        

     // Implementar la lógica para cargar el escenario desde un archivo
     // Lee el archivo y llenar la matriz con los datos
    public void cargarDesdeArchivo(String rutaArchivo) throws IOException{
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass(). getResourceAsStream(rutaArchivo)))){
            List<String> lineas = reader.lines().collect(Collectors.toList());
            this.filas = lineas.size();
            this.columnas = lineas.get(0).length();
            this.matriz = new char[filas][columnas];

            for (int i = 0; i < filas; i++) {
                matriz[i] = lineas.get(i).toCharArray();
            }
        }
       
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public char[][] getMatriz() {
        return matriz;
    }

    public interface Observador {
        void actualizar();
    }
}