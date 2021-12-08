package com.agendadigital.clases;

public class Alumno {
    private String cod_cur, cod_par,cod_alu,nombre;

    public Alumno(String cod_cur, String cod_par, String cod_alu, String nombre) {
        this.cod_cur = cod_cur;
        this.cod_par = cod_par;
        this.cod_alu = cod_alu;
        this.nombre = nombre;
    }

    public String getCod_cur() {
        return cod_cur;
    }

    public String getCod_par() {
        return cod_par;
    }

    public String getCod_alu() {
        return cod_alu;
    }

    public String getNombre() {
        return nombre;
    }
}
