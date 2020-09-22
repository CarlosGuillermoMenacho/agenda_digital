package com.agendadigital.Fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.agendadigital.Adapters.AdapterLicencias;
import com.agendadigital.R;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Estudiante;
import com.agendadigital.clases.Globals;
import com.agendadigital.ui.gallery.GalleryViewModel;

import java.util.ArrayList;

public class Lista_alu_lic_Fragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private ListView lvListaAlumnosBoletin;
    private AdminSQLite adm;
    private ArrayList<Estudiante> estudiantes;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);

        View root = inflater.inflate(R.layout.fragment_lista_alu_lic, container, false);
        adm = new AdminSQLite(getContext(),"agenda",null, 1);
        lvListaAlumnosBoletin = root.findViewById(R.id.lvListaAlumnosBoletinfg);
        llenarLista();


        lvListaAlumnosBoletin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Globals.estudiante = estudiantes.get(position);
                Navigation.findNavController(view).navigate(R.id.fragmentLicencias);
            }
        });

        return root;
    }
    private void llenarLista() {
        Cursor cursor = adm.estudiantes(Globals.user.getCodigo());
        estudiantes = new ArrayList<>();
        ArrayList<String> nombres = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                estudiantes.add(new Estudiante(cursor.getString(0),cursor.getString(1),
                        cursor.getString(8),cursor.getString(2)));
               /* nombres.add(cursor.getString(1)+"\nCol.:"+cursor.getString(2));*/

            }while (cursor.moveToNext());

            AdapterLicencias adapter = new AdapterLicencias(getContext(), estudiantes);
            lvListaAlumnosBoletin.setAdapter(adapter);
        }

    }


}