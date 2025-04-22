package com.example.proyecto_m13;

public class Estudio {
    private int idEstudio;
    private String nombreEstudio;
    private String descripcionInstrucciones;

    public Estudio(int idEstudio, String nombreEstudio, String descripcionInstrucciones) {
        this.idEstudio = idEstudio;
        this.nombreEstudio = nombreEstudio;
        this.descripcionInstrucciones = descripcionInstrucciones;
    }

    public Estudio(int idEstudio) {
        this.idEstudio = idEstudio;
    }
//Getters y Setters

    public int getIdEstudio() {
        return idEstudio;
    }

    public void setIdEstudio(int idEstudio) {
        this.idEstudio = idEstudio;
    }

    public String getNombreEstudio() {
        return nombreEstudio;
    }

    public void setNombreEstudio(String nombreEstudio) {
        this.nombreEstudio = nombreEstudio;
    }

    public String getDescripcionInstrucciones() {
        return descripcionInstrucciones;
    }

    public void setDescripcionInstrucciones(String descripcionInstrucciones) {
        this.descripcionInstrucciones = descripcionInstrucciones;
    }

// To String por si necesitamos hacer pruebas
    @Override
    public String toString() {
        return "Estudio{" +
                "idEstudio=" + idEstudio +
                ", nombreEstudio='" + nombreEstudio + '\'' +
                ", descripcionInstrucciones='" + descripcionInstrucciones + '\'' +
                '}';
    }
}