package com.agendadigital.clases;

public class Materia {
    private String cod_mat,nombre;

    public Materia(String cod_mat, String nombre) {
        this.cod_mat = cod_mat;
        this.nombre = nombre;
    }

    public String getCod_mat() {
        return cod_mat;
    }

    public String getNombre() {
        return nombre;
    }
}
