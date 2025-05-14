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

/**
 * Clase principal de la aplicación Mazmorra.
 * Inicia la interfaz gráfica, gestiona el cambio de escenas y almacena el protagonista y el escenario globales.
 * 
 * @author Nicolás
 * @version 1.0
 * @since 2025-04-22
 */
public class App extends Application {
    /** Escena principal de la aplicación. */
    private static Scene scene;
    /** Protagonista global del juego. */
    private static Protagonista protagonista;
    /** Escenario global del juego. */
    private static Escenario escenario;

    /**
     * Método principal de inicio de la aplicación JavaFX.
     * @param stage Ventana principal
     * @throws IOException Si ocurre un error al cargar recursos
     */
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

    /**
     * Cambia la raíz de la escena principal a la vista indicada.
     * @param fxml Nombre del archivo FXML (sin extensión)
     * @throws IOException Si ocurre un error al cargar el FXML
     */
    static void setRoot(String fxml) throws IOException {
        Parent root = loadFXML(fxml);
        scene.setRoot(root);
        
        // Solicitar foco para la nueva vista
        if (root instanceof GridPane) {
            ((GridPane) root).setFocusTraversable(true);
            ((GridPane) root).requestFocus();
        }
    }

    /**
     * Carga un archivo FXML y devuelve su raíz.
     * @param fxml Nombre del archivo FXML (sin extensión)
     * @return Nodo raíz del FXML
     * @throws IOException Si ocurre un error al cargar el FXML
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/proyecto/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Establece el protagonista global.
     * @param nuevoProtagonista Protagonista a establecer
     */
    public static void setProtagonista(Protagonista nuevoProtagonista) {
        protagonista = nuevoProtagonista;
    }

    /**
     * Obtiene el protagonista global.
     * @return Protagonista actual
     */
    public static Protagonista getProtagonista() {
        return protagonista;
    }

    /**
     * Obtiene el escenario global.
     * @return Escenario actual
     */
    public static Escenario getEscenario() {
        return escenario;
    }

    /**
     * Establece el escenario global.
     * @param nuevoEscenario Escenario a establecer
     */
    public static void setEscenario(Escenario nuevoEscenario) {
        escenario = nuevoEscenario;
    }

    /**
     * Método main. Lanza la aplicación JavaFX.
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch();
    }
}