package com.agendadigital.ui.gallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

import com.agendadigital.Interfaces.Comunicador;
import com.agendadigital.R;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Estudiante;
import com.agendadigital.clases.Globals;

import java.util.ArrayList;

public class Lista_alu_Fragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private ListView lvListaAlumnosBoletin;
    private AdminSQLite adm;
    private ArrayList<Estudiante> estudiantes;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);

        View root = inflater.inflate(R.layout.fragment_lista_alu, container, false);
        adm = new AdminSQLite(getContext(),"agenda",null, 1);
        lvListaAlumnosBoletin = root.findViewById(R.id.lvListaAlumnosBoletinfg);
        llenarLista();
        /*
        ArrayList<String> elementos = new ArrayList<>();
        final ArrayList<Integer> codigos = new ArrayList<>();
        elementos.add("Mercado Cespedes Roberto");
        codigos.add(4);
        elementos.add("Meneses Ribera Luis");
        codigos.add(5);
        elementos.add("Meneses Ribera Sofia");
        codigos.add(6);
        elementos.add("Juan Perez");
        codigos.add(7);
        llenarLista();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,elementos);
        lvListaAlumnosBoletin.setAdapter(adapter);
*/
        /*final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        lvListaAlumnosBoletin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Globals.estudiante = estudiantes.get(position);
                Navigation.findNavController(view).navigate(R.id.kardexPagoFragment);
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
                estudiantes.add(new Estudiante(cursor.getString(0),cursor.getString(1)));
                nombres.add(cursor.getString(1));
            }while (cursor.moveToNext());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, nombres);
            lvListaAlumnosBoletin.setAdapter(adapter);
        }

    }


}