package com.proyecto;

public class Celda {

    public enum Tipo {
        PARED, SUELO, ENTRADA, SALIDA
    }

    private Tipo tipo;

    public Celda(Tipo tipo) {
        this.tipo = tipo;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
}