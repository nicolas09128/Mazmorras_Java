package com.proyecto;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class VistaInicioController {

    @FXML
    private TextField nombreProtagonista;

    @FXML
    private Button iniciarJuego;

    @FXML
    private void iniciarJuego(ActionEvent event) {
        System.out.println("Iniciando juego con protagonista: " + nombreProtagonista.getText());
    }
}