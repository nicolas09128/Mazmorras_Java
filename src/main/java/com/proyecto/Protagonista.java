package com.proyecto;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Clase que representa el protagonista del juego.
 * Contiene los atributos y métodos específicos del personaje controlado por el usuario.
 *
 * @author Nicolás
 * @version 1.0
 * @since 2025-04-22
 */
public class Protagonista extends Personaje {

    private ImageView imagen;
    private boolean mirandoDerecha = true; 

    /**
     * Constructor del protagonista.
     * @param nombre Nombre del protagonista
     * @param salud Salud inicial
     * @param ataque Ataque inicial
     * @param defensa Defensa inicial
     * @param velocidad Velocidad inicial
     */
    public Protagonista(String nombre, int salud, int ataque, int defensa, int velocidad) {
        super(nombre, salud, ataque, defensa, velocidad);
        setSaludMaxima(salud); // Guardar la salud máxima
       
        // Inicializar el ImageView del protagonista con la imagen predeterminada
        this.imagen = new ImageView(new Image(getClass().getResource("/com/imagenes/posibles_personajes/caballero.gif").toExternalForm()));
        if (this.imagen.getImage() == null) {
            System.err.println("Advertencia: No se pudo cargar la imagen del protagonista. Verifica la ruta.");
        }

        // Configurar el tamaño del ImageView
        this.imagen.setFitWidth(32);
        this.imagen.setFitHeight(32);
    }

    @Override
    public ImageView getImagen() {
        return imagen;
    }

    public void mover(int nuevaFila, int nuevaColumna, boolean moverDerecha) {
        setFila(nuevaFila);
        setColumna(nuevaColumna);

        // Cambiar la imagen según la dirección del movimiento
        cambiarImagenMovimiento(moverDerecha);

        // Asegurarse de que el tamaño del personaje sea consistente
        this.imagen.setFitWidth(32);
        this.imagen.setFitHeight(32);
    }

    private void cambiarImagenMovimiento(boolean derecha) {
        if (derecha) {
            this.imagen.setImage(new Image(getClass().getResource("/com/imagenes/posibles_personajes/derecha.gif").toExternalForm()));
            mirandoDerecha = true;
        } else {
            this.imagen.setImage(new Image(getClass().getResource("/com/imagenes/posibles_personajes/caballero.gif").toExternalForm()));
            mirandoDerecha = false;
        }
    }

 

    public void restaurarImagenBase() {
        this.imagen.setImage(new Image(getClass().getResource("/com/imagenes/posibles_personajes/caballero.gif").toExternalForm()));
    }


    
}