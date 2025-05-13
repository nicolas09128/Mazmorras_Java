package com.proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

    private static Scene scene;
    private static Protagonista protagonista;
    private static Escenario escenario;

    @Override
    public void start(Stage stage) throws IOException {
        URL gifUrl = getClass().getResource("/com/imagenes/portada.gif");
        if (gifUrl == null) {
            System.err.println("Error: No se pudo encontrar el archivo portada.gif");
            return;
        }   

        // Configurar la pantalla de inicio con el fondo GIF
        ImageView gifBackground = new ImageView(gifUrl.toExternalForm());
        gifBackground.setFitWidth(640);
        gifBackground.setFitHeight(480);
        gifBackground.setPreserveRatio(false);

        Parent root = loadFXML("vistaInicio");
        root.setStyle("-fx-background-color: transparent;");

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(gifBackground, root);

        scene = new Scene(stackPane, 640, 480);
        stage.setScene(scene);

        // Solo permitir transición desde la pantalla de inicio
        scene.setOnKeyPressed(event -> {
            if (scene.getRoot() == stackPane) {
                manejarTransicionInicio(event);
            }
        });
        scene.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && scene.getRoot() == stackPane) {
                manejarTransicionInicio(null);
            }
        });

        stage.show();
    }

    private void manejarTransicionInicio(KeyEvent event) {
        try {
            if (protagonista == null) {
                setRoot("vistaCreacionPersonaje");
            } else {
                setRoot("vistaJuego");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void setRoot(String fxml) throws IOException {
        Parent root = loadFXML(fxml);
        scene.setRoot(root);
        
        // Solicitar foco para la nueva vista
        if (root instanceof GridPane) {
            ((GridPane) root).setFocusTraversable(true);
            ((GridPane) root).requestFocus();
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/proyecto/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void setProtagonista(Protagonista nuevoProtagonista) {
        protagonista = nuevoProtagonista;
    }

    public static Protagonista getProtagonista() {
        return protagonista;
    }

    public static Escenario getEscenario() {
        return escenario;
    }

    public static void setEscenario(Escenario nuevoEscenario) {
        escenario = nuevoEscenario;
    }

    public static void main(String[] args) {
        launch();
    }
}