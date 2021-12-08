package com.agendadigital.clases;

public class Cursos {
    private String cod_curso,
            cod_par,nombre;

    public Cursos(String cod_curso, String cod_par, String nombre) {
        this.cod_curso = cod_curso;
        this.cod_par = cod_par;
        this.nombre = nombre;
    }

    public String getCod_curso() {
        return cod_curso;
    }

    public String getCod_par() {
        return cod_par;
    }

    public String getNombre() {
        return nombre;
    }
}
