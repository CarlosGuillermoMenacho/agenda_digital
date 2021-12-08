package com.agendadigital.clases;

public class Estudiante {
    private String codigo;
    private String nombre;
    private String foto;
    private String colegio;
    private String hor;
    private String ip;
    private String cod_cur;
    private String cod_par;
    private String cantidadMensajes;

    public Estudiante() {
        this.codigo = null;
        this.nombre = null;
        this.foto = null;
        this.colegio = null;
        this.hor = null;
        this.ip = null;
        this.cod_cur = null;
        this.cod_par = null;
        this.cantidadMensajes = null;

    }



    public Estudiante(String codigo, String nombre, String ip, String cod_cur, String foto, String colegio, String cod_par, String hor, String cantidadMensajes) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.foto = foto;
        this.colegio = colegio;
        this.hor = hor;
        this.ip = ip;
        this.cod_cur = cod_cur;
        this.cod_par = cod_par;
        this.cantidadMensajes = cantidadMensajes;
    }
    public void setHor(String codigo) {
        this.codigo = codigo;
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

    public void setcantidadMensajes(String cantidadMensajes) {this.cantidadMensajes = cantidadMensajes;}

    public String getHor() {
        return hor;
    }

    public String getIp() {
        return ip;
    }
    public String getCod_cur() {
        return cod_cur;
    }

    public String getCod_par() {
        return cod_par;
    }

    public String getCantidadMensajes() {
        return cantidadMensajes;
    }
}
