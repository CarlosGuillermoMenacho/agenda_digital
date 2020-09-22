package com.agendadigital.clases;

public class Licencia {
    private String id;
    private String codigo;
    private String cod_tut;
    private String f_sol;
    private String h_sol;
    private String f_ini;
    private String f_fin;
    private String obse;
    private String nombre;
    private String estado;

    public Licencia(){
        this.id = null;
        this.codigo = null;
        this.cod_tut = null;
        this.f_sol = null;
        this.f_ini = null;
        this.f_fin = null;
        this.obse = null;
        this.nombre = null;
        this.estado = null;
    }
    public Licencia(String id,String codigo, String cod_tut, String f_sol, String h_sol, String f_ini, String f_fin, String obse,String nombre,String estado) {
        this.id = id;
        this.codigo = codigo;
        this.cod_tut = cod_tut;
        this.f_sol = f_sol;
        this.h_sol = h_sol;
        this.f_ini = f_ini;
        this.f_fin = f_fin;
        this.obse = obse;
        this.nombre = nombre;
        this.estado = estado;
    }
    public String getId() {
        return id;
    }
    public String getCodigo() {
        return codigo;
    }
    public String getCod_tut() {
        return cod_tut;
    }

    public String getF_sol() {
        return f_sol;
    }
    public String getH_sol() {
        return h_sol;
    }

    public String getF_ini() {
        return f_ini;
    }
    public String getF_fin() {
        return f_fin;
    }
    public String getObse() {
        return obse;
    }
    public String getNombre() {
        return nombre;
    }
    public String getEstado() {
        return estado;
    }
}
