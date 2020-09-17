package com.agendadigital.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.agendadigital.R;
import com.agendadigital.clases.Globals;


public class BoletinFragment2 extends Fragment {

    public BoletinFragment2() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            Toast.makeText(getActivity(), Globals.estudiante.getNombre(),Toast.LENGTH_LONG).show();
        return inflater.inflate(R.layout.fragment_boletin, container, false);
    }
}