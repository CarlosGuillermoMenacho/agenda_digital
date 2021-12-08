package com.agendadigital.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.agendadigital.R;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.Menus;

public class CalendarioFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Globals.menu = Menus.CALENDARIO_WEBVIEW;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendario, container, false);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem dark = menu.findItem(R.id.action_darkTheme);
        MenuItem light = menu.findItem(R.id.action_lightTheme);

        if ( dark != null) {
            dark.setVisible(false);
        }
        if ( light != null) {
            light.setVisible(false);
        }
    }

}