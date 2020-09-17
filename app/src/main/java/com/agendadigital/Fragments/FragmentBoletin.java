package com.agendadigital.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agendadigital.R;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.Menus;

public class FragmentBoletin extends Fragment {





    public FragmentBoletin() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Globals.menu = Menus.BOLETINES;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_boletin2, container, false);
    }
}