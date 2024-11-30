package com.example.reciclo;

public class Logro {
    private String nombre;
    private String descripcion; // Breve descripción del logro
    private boolean alcanzado;
    private int icono; // ID del recurso del ícono asociado al logro

    // Constructor básico
    public Logro(String nombre) {
        this.nombre = nombre;
        this.descripcion = "";
        this.alcanzado = false; // Por defecto, el logro no ha sido alcanzado
        this.icono = 0; // Por defecto, no se asigna ícono
    }

    // Constructor completo con descripción e ícono
    public Logro(String nombre, String descripcion, int icono) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.alcanzado = false; // Por defecto, el logro no ha sido alcanzado
        this.icono = icono;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isAlcanzado() {
        return alcanzado;
    }

    public void setAlcanzado(boolean alcanzado) {
        this.alcanzado = alcanzado;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }

    // Método para marcar el logro como alcanzado
    public void marcarComoAlcanzado() {
        this.alcanzado = true;
    }
}