package com.agendadigital.clases;

public class Estudiante {
    private String codigo;
    private String nombre;
    private String foto;
    private String colegio;

    public Estudiante() {
        this.codigo = null;
        this.nombre = null;
        this.foto = null;
        this.colegio = null;

    }
    public Estudiante(String codigo, String nombre, String foto, String colegio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.foto = foto;
        this.colegio = colegio;

    }

    public String getFoto() {
        return foto;
    }

    public String getColegio() {
        return colegio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
