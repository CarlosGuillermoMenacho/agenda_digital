package com.agendadigital.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agendadigital.R;
import com.agendadigital.clases.AdapterNotificaciones;
import com.agendadigital.clases.Globals;

import java.util.ArrayList;

public class FragmentNotificationContainer extends Fragment {
    private View vista;
    private int codMate;
    public FragmentNotificationContainer(int codMate) {
        // Required empty public constructor
        this.codMate = codMate;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_notification_container, container, false);
        cargarRecycler();
        return vista;
    }
    public void cargarRecycler(){
        RecyclerView rvNotificacionesContainer = vista.findViewById(R.id.rvNotificationContainer);
        ArrayList<String> notificaciones = new ArrayList<>();
        if (codMate==0){
            for (int i = 0 ; i < Globals.notificaciones.size(); i++){
                notificaciones.add(Globals.notificaciones.getNotificacion(i).getMensaje());
            }
            AdapterNotificaciones adapterNotificaciones = new AdapterNotificaciones(notificaciones);
            rvNotificacionesContainer.setLayoutManager(new LinearLayoutManager(getContext()));
            rvNotificacionesContainer.setAdapter(adapterNotificaciones);
        }else{
            for (int i = 0 ; i < Globals.notificaciones.size(); i++){
                if (Globals.notificaciones.getNotificacion(i).getEmisor()==codMate){
                    notificaciones.add(Globals.notificaciones.getNotificacion(i).getMensaje());
                }
            }
            AdapterNotificaciones adapterNotificaciones = new AdapterNotificaciones(notificaciones);
            rvNotificacionesContainer.setLayoutManager(new LinearLayoutManager(getContext()));
            rvNotificacionesContainer.setAdapter(adapterNotificaciones);
        }
    }
}