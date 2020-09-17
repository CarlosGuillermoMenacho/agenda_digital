package com.agendadigital.clases;

import android.content.Context;

import java.util.ArrayList;

public class Usuarios {
    private ArrayList<User> usuarios;

    public Usuarios(Context context) {
        AdminSQLite adm = new AdminSQLite(context,"agenda",null,1);
        this.usuarios = new ArrayList<>(adm.users());
    }

    public ArrayList<User> getUsuarios() {
        return usuarios;
    }
}
