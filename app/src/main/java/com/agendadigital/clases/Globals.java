package com.agendadigital.clases;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

//Esta clase se puede utilizar para declarar variables globales, las cuales pueden ser usadas en cualquier parte de la actividad
public class Globals {
    public static User user = new User(); //En esta variable se almacenará el usuario seleccionado
    public static User sUserNombres = new User();
    public static Estudiante estudiante = new Estudiante(); //En esta variable se almacenará el Estudiante seleccionado
    public static int menu;
    public static Cursor notificacioness;
    public static Notificaciones notificaciones;
    public static Colegio colegio = new Colegio(); //En esta variable se almacenará el Estudiante seleccionado





}
