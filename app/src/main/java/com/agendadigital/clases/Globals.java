package com.agendadigital.clases;

import android.database.Cursor;

//Esta clase se puede utilizar para declarar variables globales, las cuales pueden ser usadas en cualquier parte de la actividad
public class Globals {
    public static User user = new User(); //En esta variable se almacenará el usuario seleccionado
    public static Estudiante estudiante = new Estudiante(); //En esta variable se almacenará el Estudiante seleccionado
    public static int menu;
    public static Cursor notificacioness;
    public static Notificaciones notificaciones;
}
