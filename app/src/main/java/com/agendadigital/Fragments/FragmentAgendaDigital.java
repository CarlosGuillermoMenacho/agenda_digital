package com.agendadigital.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agendadigital.R;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.Menus;
import com.nex3z.notificationbadge.NotificationBadge;

public class FragmentAgendaDigital extends Fragment {

    NotificationBadge mBadge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Globals.menu = Menus.AGENDA;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Globals.tabsActivos.clear();
        View view = inflater.inflate(R.layout.fragment_agenda_digital, container, false);
        return view;
    }
}