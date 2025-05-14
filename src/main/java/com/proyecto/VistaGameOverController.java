package com.proyecto;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * Controlador de la pantalla de Game Over.
 * Muestra la imagen de fondo y el número de enemigos derrotados al finalizar la partida.
 *
 * @author Nicolás
 * @version 1.0
 * @since 2025-04-22
 */
public class VistaGameOverController {
    /** Panel raíz de la vista. */
    @FXML
    private AnchorPane rootPane;
    /** Imagen de fondo de Game Over. */
    @FXML
    private ImageView fondoGameOver;
    /** Label principal de Game Over. */
    @FXML
    private Label labelGameOver;
    /** Label que muestra el número de enemigos derrotados. */
    @FXML
    private Label labelEnemigosMatados;

    /**
     * Inicializa la vista y carga la imagen de fondo.
     */
    public void initialize() {
        // Cargar imagen de fondo
        Image fondo = new Image(getClass().getResource("/com/imagenes/GameOver.png").toExternalForm());
        fondoGameOver.setImage(fondo);
        // El texto principal ya está en el FXML
    }

    /**
     * Establece el número de enemigos derrotados en la vista.
     * @param cantidad Número de enemigos eliminados
     */
    public void setEnemigosMatados(int cantidad) {
        labelEnemigosMatados.setText("Enemigos derrotados: " + cantidad);
    }
}
