package com.agendadigital.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.agendadigital.Interfaces.Comunicador;
import com.agendadigital.R;

public class FragmentFormAfiliacion extends Fragment{
    private Comunicador comunicador;

    public FragmentFormAfiliacion() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View vista = inflater.inflate(R.layout.fragment_form_afiliacion, container, false);
        Spinner spinner = vista.findViewById(R.id.spSelector);
        String[] opciones = new String[]{"Tutor/Padre","Profesor","Alumno"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,opciones);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onitemSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return vista;
    }

    private void onitemSelected(int position) {
        switch (position){
            case 0:
                comunicador.cambiarForm(R.id.fragmentFormTutor);
                break;
            case 1:
                comunicador.cambiarForm(R.id.fragmentFormProfesor);
                break;
            case 2:
                comunicador.cambiarForm(R.id.fragmentFormAlumno);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            Activity activity = (Activity) context;
            comunicador = (Comunicador) activity;
        }
    }
}