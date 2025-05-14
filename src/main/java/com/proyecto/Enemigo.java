package com.proyecto;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase que representa un enemigo en el juego.
 * Hereda de Personaje y añade atributos y métodos específicos de los enemigos.
 *
 * @author Nicolás
 * @version 1.0
 * @since 2025-04-22
 */
public class Enemigo extends Personaje {

    /** Percepción del enemigo (alcance para detectar al protagonista). */
    private int percepcion;
    /** Imagen principal del enemigo. */
    private ImageView imagen;
    /** Imagen alternativa (por ejemplo, mirando a la derecha). */
    private ImageView imagenDerecha;
    /** Hilo de movimiento automático del enemigo. */
    private Thread hiloMovimiento;

    private static final Image IMAGEN_ENEMIGO;
    private static final Image IMAGEN_ENEMIGO_DERECHA;

    static {
        URL enemigoUrl = Enemigo.class.getResource("/com/imagenes/posibles_personajes/enemigo.gif");
        if (enemigoUrl != null) {
            IMAGEN_ENEMIGO = new Image(enemigoUrl.toExternalForm());
        } else {
            System.err.println("Advertencia: No se pudo cargar enemigo.gif. Usando imagen por defecto.");
            IMAGEN_ENEMIGO = new Image("https://via.placeholder.com/32"); // Imagen de respaldo
        }

        URL enemigoDerechaUrl = Enemigo.class.getResource("/com/imagenes/posibles_personajes/enemigo_derecha.gif");
        if (enemigoDerechaUrl != null) {
            IMAGEN_ENEMIGO_DERECHA = new Image(enemigoDerechaUrl.toExternalForm());
        } else {
            System.err.println("Advertencia: No se pudo cargar enemigo_derecha.gif. Usando imagen por defecto.");
            IMAGEN_ENEMIGO_DERECHA = new Image("https://via.placeholder.com/32"); // Imagen de respaldo
        }
    }

    /**
     * Constructor de Enemigo con todos los parámetros.
     * @param nombre Nombre del enemigo
     * @param salud Salud inicial
     * @param ataque Ataque inicial
     * @param defensa Defensa inicial
     * @param velocidad Velocidad inicial
     * @param percepcion Alcance de percepción
     */
    public Enemigo(String nombre, int salud, int ataque, int defensa, int velocidad, int percepcion) {
        super(nombre, salud, ataque, defensa, velocidad);
        this.percepcion = percepcion;

        this.imagen = new ImageView(IMAGEN_ENEMIGO);
        this.imagen.setFitWidth(100);
        this.imagen.setFitHeight(100);

        this.imagenDerecha = new ImageView(IMAGEN_ENEMIGO_DERECHA);
        this.imagenDerecha.setFitWidth(100);
        this.imagenDerecha.setFitHeight(100);
    }

    public Enemigo(String nombre, int salud, int ataque, int defensa) {
        super(nombre, salud, ataque, defensa, 0); // Velocidad predeterminada como 0

        // Ruta de la imagen del enemigo
        String rutaImagen = "/com/imagenes/posibles_personajes/enemigo.gif";
        try {
            // Intentar cargar la imagen
            this.imagen = new ImageView(new Image(getClass().getResource(rutaImagen).toExternalForm()));
        } catch (NullPointerException e) {
            System.err.println("Error: No se pudo encontrar la imagen en la ruta: " + rutaImagen);
            // Usar una imagen predeterminada en caso de error
            this.imagen = new ImageView();
        }

        // Configurar el tamaño del ImageView
        this.imagen.setFitWidth(32);
        this.imagen.setFitHeight(32);
    }

    /**
     * Constructor de Enemigo con posición inicial.
     * @param nombre Nombre del enemigo
     * @param salud Salud inicial
     * @param ataque Ataque inicial
     * @param defensa Defensa inicial
     * @param percepcion Alcance de percepción
     * @param fila Fila inicial
     * @param columna Columna inicial
     */
    public Enemigo(String nombre, int salud, int ataque, int defensa, int percepcion, int fila, int columna) {
        super(nombre, salud, ataque, defensa, 0); // Velocidad predeterminada como 0
        this.percepcion = percepcion;
        setFila(fila);
        setColumna(columna);
        setSaludMaxima(salud); // Guardar la salud máxima

        // Ruta de la imagen del enemigo
        String rutaImagen = "/com/imagenes/posibles_personajes/enemigo.gif";
        try {
            this.imagen = new ImageView(new Image(getClass().getResource(rutaImagen).toExternalForm()));
        } catch (NullPointerException e) {
            System.err.println("Error: No se pudo encontrar la imagen en la ruta: " + rutaImagen);
            this.imagen = new ImageView();
        }

        // Configurar el tamaño del ImageView
        this.imagen.setFitWidth(32);
        this.imagen.setFitHeight(32);
    }

    /**
     * Devuelve la percepción del enemigo.
     * @return Valor de percepción
     */
    public int getPercepcion() {
        return percepcion;
    }

    /**
     * Devuelve la imagen principal del enemigo.
     * @return ImageView del enemigo
     */
    @Override
    public ImageView getImagen() {
        return imagen;
    }

    /**
     * Mueve al enemigo a una nueva posición.
     * @param nuevaFila Fila destino
     * @param nuevaColumna Columna destino
     */
    public void mover(int nuevaFila, int nuevaColumna) {
        setFila(nuevaFila);
        setColumna(nuevaColumna);
    }

    public void moverHacia(int filaObjetivo, int columnaObjetivo) {
        if (getFila() < filaObjetivo) {
            setFila(getFila() + 1);
        } else if (getFila() > filaObjetivo) {
            setFila(getFila() - 1);
        }

        if (getColumna() < columnaObjetivo) {
            setColumna(getColumna() + 1);
        } else if (getColumna() > columnaObjetivo) {
            setColumna(getColumna() - 1);
        }
    }

    public void moverAleatorio() {
        Random random = new Random();
        int direccion = random.nextInt(4);
        switch (direccion) {
            case 0:
                setFila(getFila() - 1); 
                break;
            case 1:
                setFila(getFila() + 1); 
                break;
            case 2:
                setColumna(getColumna() - 1); 
                break;
            case 3:
                setColumna(getColumna() + 1); 
                break;
        }
    }

    public static List<Enemigo> cargarEnemigosDesdeFichero(String rutaFichero) {
        List<Enemigo> enemigos = new ArrayList<>();
        InputStream inputStream = Enemigo.class.getResourceAsStream(rutaFichero);
        if (inputStream == null) {
            throw new IllegalArgumentException("Fichero de enemigos no encontrado: " + rutaFichero);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 6) {
                    String nombre = partes[0].trim();
                    int salud = Integer.parseInt(partes[1].trim());
                    int ataque = Integer.parseInt(partes[2].trim());
                    int defensa = Integer.parseInt(partes[3].trim());
                    int velocidad = Integer.parseInt(partes[4].trim());
                    int percepcion = Integer.parseInt(partes[5].trim());
                    enemigos.add(new Enemigo(nombre, salud, ataque, defensa, velocidad, percepcion));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return enemigos;
    }

    /**
     * Inicia el movimiento automático del enemigo en un hilo.
     * @param escenario Escenario de juego
     * @param protagonista Protagonista
     * @param gridPane GridPane visual
     * @param enemigos Lista de enemigos
     */
    public void iniciarMovimientoAutomatico(Escenario escenario, Protagonista protagonista, GridPane gridPane, List<Enemigo> enemigos) {
        hiloMovimiento = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000); 

                    final int[] nuevaFila = {getFila()};
                    final int[] nuevaColumna = {getColumna()};

                    int distancia = Math.abs(getFila() - protagonista.getFila()) +
                                    Math.abs(getColumna() - protagonista.getColumna());

                    if (distancia <= percepcion) {
                        // Perseguir al protagonista
                        if (getFila() < protagonista.getFila()) nuevaFila[0]++;
                        else if (getFila() > protagonista.getFila()) nuevaFila[0]--;

                        if (getColumna() < protagonista.getColumna()) nuevaColumna[0]++;
                        else if (getColumna() > protagonista.getColumna()) nuevaColumna[0]--;
                    } else {
                        // Movimiento aleatorio
                        Random random = new Random();
                        int direccion = random.nextInt(4);
                        switch (direccion) {
                            case 0: nuevaFila[0]--; break;
                            case 1: nuevaFila[0]++; break;
                            case 2: nuevaColumna[0]--; break;
                            case 3: nuevaColumna[0]++; break;
                        }
                    }

                    // Validar que la nueva posición no sea un muro, ni el protagonista, ni otro enemigo
                    boolean libre = false;
                    if (nuevaFila[0] >= 0 && nuevaFila[0] < escenario.getFilas() &&
                        nuevaColumna[0] >= 0 && nuevaColumna[0] < escenario.getColumnas() &&
                        escenario.getMatriz()[nuevaFila[0]][nuevaColumna[0]] != '#') {

                        // No pisar protagonista
                        if (!(protagonista.getFila() == nuevaFila[0] && protagonista.getColumna() == nuevaColumna[0])) {
                            // No pisar otro enemigo
                            libre = true;
                            for (Enemigo otro : enemigos) {
                                if (otro != this && otro.getFila() == nuevaFila[0] && otro.getColumna() == nuevaColumna[0] && otro.getSalud() > 0) {
                                    libre = false;
                                    break;
                                }
                            }
                        }
                    }

                    if (libre) {
                        setFila(nuevaFila[0]);
                        setColumna(nuevaColumna[0]);

                        Platform.runLater(() -> {
                            escenario.colocarPersonaje(this, nuevaFila[0], nuevaColumna[0], gridPane);
                        });
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        hiloMovimiento.setDaemon(true);
        hiloMovimiento.start();
    }

    /**
     * Detiene el movimiento automático del enemigo.
     */
    public void detenerMovimiento() {
        if (hiloMovimiento != null && hiloMovimiento.isAlive()) {
            hiloMovimiento.interrupt();
        }
    }
}
