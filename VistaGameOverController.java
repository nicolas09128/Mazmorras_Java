package com.proyecto;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class VistaGameOverController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView fondoGameOver;
    @FXML
    private Label labelGameOver;
    @FXML
    private Label labelEnemigosMatados;

    public void initialize() {
        // Cargar imagen de fondo
        Image fondo = new Image(getClass().getResource("/com/imagenes/GameOver.png").toExternalForm());
        fondoGameOver.setImage(fondo);
        // El texto principal ya está en el FXML
    }

    public void setEnemigosMatados(int cantidad) {
        labelEnemigosMatados.setText("Enemigos derrotados: " + cantidad);
    }
}
