package com.agendadigital.clases;

import android.content.Context;

import java.util.ArrayList;

public class Licencias {
    private ArrayList<Licencia> licencias;

    public Licencias(Context context) {
        AdminSQLite adm = new AdminSQLite(context,"agenda",null,1);
        this.licencias = new ArrayList<>(adm.obt_licencias());
    }

    public ArrayList<Licencia> getLicencias() {
        return licencias;
    }
}
