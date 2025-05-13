package com.proyecto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.proyecto.Escenario.Observador;

import javafx.scene.image.ImageView;

public abstract class Personaje {
    private String nombre;
    private int salud;
    private int ataque;
    private int defensa;
    private int velocidad;
    private int fila;
    private int columna;
    private int saludMaxima;

    public Personaje(String nombre, int salud, int ataque, int defensa, int velocidad) {
        this.nombre = nombre;
        this.salud = salud;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.saludMaxima = salud; // Por defecto, la salud máxima es la inicial
    }

private List<Observador> observadores = new ArrayList<>();
    
// Métodos para añadir/eliminar observadores

protected void notificarObservadores() {
    for (Observador obs : observadores) {
        obs.actualizar();
    }
} 

private Queue<Personaje> colaTurnos;

private void inicializarColaTurnos(Collection<? extends Personaje> enemigos, Personaje protagonista) {
    List<Personaje> todos = new ArrayList<>();
    todos.add(protagonista);
    todos.addAll(enemigos);
    
    // Ordenar por velocidad descendente
    todos.sort((p1, p2) -> Integer.compare(p2.getVelocidad(), p1.getVelocidad()));
    
    colaTurnos = new LinkedList<>(todos);
}

private void avanzarTurno(Object turnoActual) {
    Personaje actual = colaTurnos.poll();
    colaTurnos.add(actual);
    if (turnoActual instanceof javafx.scene.control.Label) {
        ((javafx.scene.control.Label) turnoActual).setText("Turno: " + actual.getNombre());
    } else {
        throw new IllegalArgumentException("turnoActual must be of type Label");
    }
}
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getSalud() {
        return salud;
    }

    public void setSalud(int salud) {
        this.salud = salud;
    }

    public int getAtaque() {
        return ataque;
    }

    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }

    public int getDefensa() {
        return defensa;
    }

    public void setDefensa(int defensa) {
        this.defensa = defensa;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public int getSaludMaxima() {
        return saludMaxima;
    }

    public void setSaludMaxima(int saludMaxima) {
        this.saludMaxima = saludMaxima;
    }

    public abstract ImageView getImagen();
}