package com.example.reciclo;

public class ActividadReciente {
    private String descripcion;
    private String fecha;

    public ActividadReciente(String descripcion, String fecha) {
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }
}