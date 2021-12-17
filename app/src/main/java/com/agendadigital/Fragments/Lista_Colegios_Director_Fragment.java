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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.agendadigital.R;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Colegio;
import com.agendadigital.clases.Globals;

import java.util.ArrayList;

public class Lista_Colegios_Director_Fragment extends Fragment {


    private ListView lvListaAlumnosBoletin;
    private AdminSQLite adm;
    private ArrayList<Colegio> colegios;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_lista_colegios_director, container, false);
        adm = new AdminSQLite(getContext(),"agenda",null, 1);
        lvListaAlumnosBoletin = root.findViewById(R.id.lvListaColegiosDirector);
        llenarLista();


        lvListaAlumnosBoletin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Globals.colegio = colegios.get(position);
                Navigation.findNavController(view).navigate(R.id.lista_Cursos_Dir_Fragment);
            }
        });

        return root;
    }
    private void llenarLista() {
        Cursor cursor = adm.colegiosDir(Globals.user.getCodigo());
        colegios = new ArrayList<>();
        ArrayList<String> nombres = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                colegios.add(new Colegio(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)));
             //   estudiantes.add(new Estudiante(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)));
                nombres.add(cursor.getString(0)+" Colegio: "+cursor.getString(1)+" Turno: "+cursor.getString(3));
            }while (cursor.moveToNext());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, nombres);
            lvListaAlumnosBoletin.setAdapter(adapter);
        }
    }


}