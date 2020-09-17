package com.agendadigital.clases;

public class Notificacion {
    private int codigo, cod_est , emisor,visto,cod_tutor;
    private String mensaje,fecha,hora;

    public Notificacion(int codigo, int cod_est, int emisor, int visto, int cod_tutor, String mensaje, String fecha, String hora) {
        this.codigo = codigo;
        this.cod_est = cod_est;
        this.emisor = emisor;
        this.visto = visto;
        this.cod_tutor = cod_tutor;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.hora = hora;
    }
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getCod_est() {
        return cod_est;
    }

    public int getEmisor() {
        return emisor;
    }

    public void setEmisor(int emisor) {
        this.emisor = emisor;
    }

    public int getVisto() {
        return visto;
    }

    public void setVisto(int visto) {
        this.visto = visto;
    }

    public int getCod_tutor() {
        return cod_tutor;
    }

    public void setCod_tutor(int cod_tutor) {
        this.cod_tutor = cod_tutor;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
