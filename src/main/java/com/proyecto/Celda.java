package com.proyecto;

/**
 * Clase que representa una celda del escenario del juego.
 * Puede ser de tipo PARED, SUELO, ENTRADA o SALIDA.
 *
 * @author Nicolás
 * @version 1.0
 * @since 2025-04-22
 */
public class Celda {
    /** Tipos posibles de celda en el escenario. */
    public enum Tipo {
        PARED, SUELO, ENTRADA, SALIDA
    }
    /** Tipo de la celda. */
    private Tipo tipo;
    /**
     * Constructor de la celda.
     * @param tipo Tipo de celda
     */
    public Celda(Tipo tipo) {
        this.tipo = tipo;
    }
    /**
     * Devuelve el tipo de la celda.
     * @return Tipo de la celda
     */
    public Tipo getTipo() {
        return tipo;
    }
    /**
     * Establece el tipo de la celda.
     * @param tipo Nuevo tipo de celda
     */
    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
}