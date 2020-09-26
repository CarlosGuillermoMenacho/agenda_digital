package com.agendadigital.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agendadigital.R;

public class FragmentViewPagerPublicidad extends Fragment {

    private int codigoEmpresa;

    public FragmentViewPagerPublicidad( int codigoEmpresa ) {

        this.codigoEmpresa = codigoEmpresa;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_view_pager_publicidad, container, false);
    }






}