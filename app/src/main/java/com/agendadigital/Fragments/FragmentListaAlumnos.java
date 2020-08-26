package com.agendadigital.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.agendadigital.Interfaces.Comunicador;
import com.agendadigital.R;

import java.util.ArrayList;

public class FragmentListaAlumnos extends Fragment {

    private Comunicador comunicador;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_lista_alumnos, container, false);

        ListView lvListaAlumnosBoletin = root.findViewById(R.id.lvListaAlumnosBoletin);
        ArrayList<String> elementos = new ArrayList<>();
        final ArrayList<Integer> codigos = new ArrayList<>();
        elementos.add("Mercado Cespedes Roberto");
        codigos.add(1);
        elementos.add("Meneses Ribera Luis");
        codigos.add(2);
        elementos.add("Meneses Ribera Sofia");
        codigos.add(3);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, elementos);
        lvListaAlumnosBoletin.setAdapter(adapter);
        lvListaAlumnosBoletin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                comunicador.enviarDatos(Integer.toString(codigos.get(position)),R.id.boletinFragment);
                Navigation.findNavController(view).navigate(R.id.boletinFragment);
            }
        });

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            Activity activity = (Activity) context;
            comunicador = (Comunicador) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}