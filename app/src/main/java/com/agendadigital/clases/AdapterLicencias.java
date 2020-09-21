package com.agendadigital.clases;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterLicencias extends RecyclerView<AdapterLicencias.ViewHolderLicencias> {

    ArrayList<String> nombresAlumno;
    ArrayList<String> imgAlumno;

    public AdapterLicencias(ArrayList<String> nombreAlumno, ArrayList<String> imgAlumno) {
        super();

        this.nombresAlumno = nombreAlumno;
        this.imgAlumno = imgAlumno

    }


    public class ViewHolderLicencias extends RecyclerView.ViewHolder {


        public ViewHolderLicencias(@NonNull View itemView) {
            super(itemView);
        }
    }
}
