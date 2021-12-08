package com.agendadigital.clases;

import java.util.ArrayList;

public class Notificaciones {
    private ArrayList<Notificacion> notificaciones;


    public Notificaciones() {
        notificaciones = new ArrayList<>();
    }

    public void add(Notificacion notificacion){
        this.notificaciones.add(notificacion);
    }
    public Notificacion getNotificacion(int pos){
        return notificaciones.get(pos);
    }



    public int size(){
       return this.notificaciones.size();
    }
}
