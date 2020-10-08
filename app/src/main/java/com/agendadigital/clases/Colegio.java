package com.agendadigital.clases;

public class Colegio {
    private String cod_col;
    private String nombre;
    private String ip;
    private String turno;
    private String cod_par;
    private String hor;

    public Colegio() {
        this.cod_col = null;
        this.nombre = null;
        this.ip = null;
        this.turno = null;
        this.cod_par = null;
        this.hor = null;
    }
    public Colegio(String cod_col, String nombre, String ip, String turno) {
        this.cod_col = cod_col;
        this.nombre = nombre;
        this.ip = ip;
        this.turno = turno;
    }
    public String getCodigo() {
        return cod_col;
    }

    public void setCodigo(String codigo) {
        this.cod_col = cod_col;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String codigo) {
        this.cod_col = ip;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String codigo) {
        this.cod_col = turno;
    }

    public String getCod_par() {
        return cod_par;
    }

    public void setCod_par(String codigo) {
        this.cod_col = cod_par;
    }

    public String getHor() {
        return hor;
    }

    public void setHor(String codigo) {
        this.cod_col = hor;
    }
}
