package com.agendadigital.clases;

public class Publicidad {

    private String url;
    private String nombre;
    private String img;
    private String ubicacion;
    private String codigoPublicidad;

    public Publicidad(String url, String nombre ,String img, String ubicacion, String codigoPublicidad) {
        this.url = url;
        this.nombre = nombre;
        this.img = img;
        this.ubicacion = ubicacion;
        this.codigoPublicidad = codigoPublicidad;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getCodigoPublicidad() {
        return codigoPublicidad;
    }

    public void setCodigoPublicidad(String codigoPublicidad) {
        this.codigoPublicidad = codigoPublicidad;
    }
}
