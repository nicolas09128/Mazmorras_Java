package com.proyecto;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

/**
 * Controlador de la pantalla de inicio del juego.
 * Permite introducir el nombre del protagonista y comenzar la partida.
 *
 * @author Nicolás
 * @version 1.0
 * @since 2025-04-22
 */
public class VistaInicioController {

    /** Campo de texto para el nombre del protagonista. */
    @FXML
    private TextField nombreProtagonista;

    /** Botón para iniciar el juego. */
    @FXML
    private Button iniciarJuego;

    /**
     * Acción que se ejecuta al pulsar el botón de iniciar juego.
     * @param event Evento de acción
     */
    @FXML
    private void iniciarJuego(ActionEvent event) {
        System.out.println("Iniciando juego con protagonista: " + nombreProtagonista.getText());
    }
}