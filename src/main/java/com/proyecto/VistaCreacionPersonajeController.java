package com.proyecto;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;


import java.net.URL;

public class VistaCreacionPersonajeController {

    @FXML
    private TextField nombre;

    @FXML
    private TextField salud;

    @FXML
    private TextField ataque;

    @FXML
    private StackPane rootPane;

    @FXML
    public void initialize() {
      
        URL gifUrl = getClass().getResource("/com/imagenes/Estadisticas.png");
        if (gifUrl == null) {
            System.err.println("Error: No se pudo encontrar el archivo portada.gif");
            return;
        }

      
        ImageView fondo = new ImageView(gifUrl.toExternalForm());
        fondo.setFitWidth(640);
        fondo.setFitHeight(480);
        fondo.setPreserveRatio(false);

      
        rootPane.getChildren().add(0, fondo);
    }

    @FXML
    private void confirmarDatos(ActionEvent event) {
        String nombreTexto = nombre.getText();
        String saludTexto = salud.getText();
        String ataqueTexto = ataque.getText();

        if (nombreTexto.isEmpty() || saludTexto.isEmpty() || ataqueTexto.isEmpty()) {
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText("Datos incompletos");
            alerta.setContentText("Por favor, rellena todos los campos antes de continuar.");
            alerta.showAndWait();
        } else {
            try {
                int saludValor = Integer.parseInt(saludTexto);
                int ataqueValor = Integer.parseInt(ataqueTexto);

                Protagonista protagonista = new Protagonista(nombreTexto, saludValor, ataqueValor, 10, 5);
                App.setProtagonista(protagonista);
                App.setRoot("vistaJuego");
            } catch (NumberFormatException e) {
                Alert alerta = new Alert(AlertType.ERROR);
                alerta.setTitle("Error");
                alerta.setHeaderText("Formato incorrecto");
                alerta.setContentText("Salud y ataque deben ser números enteros.");
                alerta.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}