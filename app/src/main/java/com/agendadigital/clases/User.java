package com.agendadigital.clases;

public class User {
    private String codigo;
    private String nombre;
    private String tipo;

    public User(){
        this.codigo = null;
        this.nombre = null;
        this.tipo = null;
    }
    public User(String codigo, String nombre, String tipo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }


    public String getCodigo() {
        return codigo;
    }

    public String getTipo() {
        return tipo;
    }
}
