package com.example.reciclo;

public class Actividad {
    private String codigoBarra;
    private String fechaHora;

    public Actividad(String codigoBarra, String fechaHora) {
        this.codigoBarra = codigoBarra;
        this.fechaHora = fechaHora;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public String getFechaHora() {
        return fechaHora;
    }
}