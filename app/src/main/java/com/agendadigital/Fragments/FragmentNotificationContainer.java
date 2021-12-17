package com.agendadigital.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agendadigital.R;
import com.agendadigital.clases.AdapterNotificaciones;
import com.agendadigital.clases.AdapterNotificacionesMaterias;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.Notificacion;
import com.agendadigital.clases.Notificaciones;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentNotificationContainer extends Fragment {
    private View vista;
    private String codMateria = "X";
    private AdapterNotificaciones adapterNotificaciones;
    private AdapterNotificacionesMaterias adapterNotificacionesMaterias;
    RecyclerView rvNotificacionesContainer;
    AdminSQLite adm;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Notificacion notificacion1 = new Notificacion();
            try {
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(intent.getStringExtra("mensaje")));
                notificacion1 = new Notificacion(jsonObject.getInt("id"), jsonObject.getInt("cod_est"),
                        jsonObject.getString("emisor"), 0, jsonObject.getInt("cod_tutor"), jsonObject.getString("mensaje"),
                        jsonObject.getString("fecha"),
                        jsonObject.getString("hora"), jsonObject.getInt("tipo"), jsonObject.getString("nombreemisor"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (codMateria.equals("X")&&Globals.estudiante.getCodigo().equals(Integer.toString(notificacion1.getCod_est()))) {
                adapterNotificaciones.add(notificacion1);
                adm.testEstados(Globals.user.getCodigo(),Globals.estudiante.getCodigo());
                rvNotificacionesContainer.scrollToPosition(adapterNotificaciones.getItemCount() - 1);
            } else if (Globals.estudiante.getCodigo().equals(Integer.toString(notificacion1.getCod_est()))){
                adapterNotificacionesMaterias.add(notificacion1);
                rvNotificacionesContainer.scrollToPosition(adapterNotificacionesMaterias.getItemCount() - 1);
            }

            Cursor cursor = adm.getNotificaciones(Globals.estudiante.getCodigo(),Globals.user.getCodigo());
            if (cursor.moveToFirst()){
                Globals.notificaciones = new Notificaciones();
                do {
                    Globals.notificaciones.add(new Notificacion(cursor.getInt(0)
                            ,cursor.getInt(1),cursor.getString(3),
                            cursor.getInt(7),cursor.getInt(4),
                            cursor.getString(2),cursor.getString(5),cursor.getString(6),
                            cursor.getInt(8),cursor.getString(9)));
                }while (cursor.moveToNext());
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adm = new AdminSQLite(requireActivity().getApplicationContext(),"agenda",null,1);
        if (getArguments() != null ) {
            codMateria = getArguments().getString("codigoMateria");
            Globals.tabsActivos.add(codMateria+Globals.estudiante.getCodigo());
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_notification_container, container, false);
        cargarRecycler();
        rvNotificacionesContainer = vista.findViewById(R.id.rvNotificationContainer);
        if (codMateria.equals("X")) {
            rvNotificacionesContainer.scrollToPosition(adapterNotificaciones.getItemCount() - 1);
        } else {
            if (Globals.notificaciones != null) {
                rvNotificacionesContainer.scrollToPosition(adapterNotificacionesMaterias.getItemCount() - 1);
            }
        }
        return vista;
    }
    public void cargarRecycler(){
        if (Globals.tabsActivos.isEmpty()){
            Globals.tabsActivos.add("X"+Globals.estudiante.getCodigo());
        }
        rvNotificacionesContainer = vista.findViewById(R.id.rvNotificationContainer);
        ArrayList<Notificacion> notificaciones = new ArrayList<>();
        if (codMateria.equals("X")){
            adapterNotificaciones = new AdapterNotificaciones(new ArrayList<Notificacion>(),getContext());
        }else{
            adapterNotificacionesMaterias = new AdapterNotificacionesMaterias(new ArrayList<Notificacion>(),getContext());
        }
        if (codMateria.equals("X") && Globals.notificaciones != null){
            for (int i = 0 ; i < Globals.notificaciones.size(); i++){
                notificaciones.add(Globals.notificaciones.getNotificacion(i));
            }
            adapterNotificaciones = new AdapterNotificaciones(notificaciones,getContext());
            rvNotificacionesContainer.setLayoutManager(new LinearLayoutManager(getContext()));
            rvNotificacionesContainer.setAdapter(adapterNotificaciones);
        } else if(Globals.notificaciones!=null){
            for (int i = 0 ; i < Globals.notificaciones.size(); i++){
                if (Globals.notificaciones.getNotificacion(i).getEmisor().equals(codMateria)){
                    notificaciones.add(Globals.notificaciones.getNotificacion(i));
                }
            }
            adapterNotificacionesMaterias = new AdapterNotificacionesMaterias(notificaciones,getContext());
            rvNotificacionesContainer.setLayoutManager(new LinearLayoutManager(getContext()));
            rvNotificacionesContainer.setAdapter(adapterNotificacionesMaterias);
        }
        else{
            adapterNotificaciones = new AdapterNotificaciones(notificaciones,getContext());
            adapterNotificaciones.clear();
            rvNotificacionesContainer.setLayoutManager(new LinearLayoutManager(getContext()));
            rvNotificacionesContainer.setAdapter(adapterNotificaciones);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(codMateria + Globals.estudiante.getCodigo());
        requireActivity().registerReceiver(receiver, filter);
    }
    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(receiver);
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