package com.agendadigital.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.agendadigital.R;
/*import com.agendadigital.clases.AdapterNotificaciones;
import com.agendadigital.clases.AdapterPublicidad;*/
import com.agendadigital.clases.AdapterPublicidad;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.clases.Publicidad;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class FragmentViewPagerPublicidad extends Fragment {

    private AdminSQLite adm;
    RecyclerView recyclerViewPublicidades;

    private ArrayList<Publicidad> publicidad;
    private int codigoEmpresa;

    public FragmentViewPagerPublicidad() {

    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adm = new AdminSQLite(getContext(), "agenda", null, 1);
        if (getArguments() != null ) {
            codigoEmpresa = getArguments().getInt("codigoEmpresa");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_pager_publicidad, container, false);
        recyclerViewPublicidades = view.findViewById(R.id.RV_Publicidades);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarPublicidad();
    }

    public void cargarPublicidad(){
        publicidad = new ArrayList<>();
        Cursor cursor = adm.getPublicidad(Integer.toString(codigoEmpresa));
        if (cursor.moveToFirst()) {
            do {
                publicidad.add(new Publicidad(cursor.getString(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3)
                        ,cursor.getString(4)));

            } while (cursor.moveToNext());
            AdapterPublicidad data = new AdapterPublicidad(getContext() ,publicidad);
            recyclerViewPublicidades.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewPublicidades.setAdapter(data);
        }
    }
}


