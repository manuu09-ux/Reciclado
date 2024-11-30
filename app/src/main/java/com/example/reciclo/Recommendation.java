package com.example.reciclo;

public class Recommendation {
    private String id;
    private String text;

    public Recommendation() {
        // Constructor vacío, necesario para Firebase
    }

    // Constructor con parámetros
    public Recommendation(String id, String text) {
        this.id = id;
        this.text = text;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}