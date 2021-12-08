package com.agendadigital.clases;

public class NotificacionDir {
    private String mensaje;
    private String fecha;
    private String hora;
    private int tipo;

    public NotificacionDir(String mensaje, String fecha, String hora, int tipo) {

        this.mensaje = mensaje;
        this.fecha = fecha;
        this.hora = hora;
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public int getTipo() {
        return tipo;
    }
}
