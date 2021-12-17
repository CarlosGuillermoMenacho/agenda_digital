package com.agendadigital.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agendadigital.R;
import com.agendadigital.clases.AdapterPublicidad;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Publicidad;

import java.util.ArrayList;

/*import com.agendadigital.clases.AdapterNotificaciones;
import com.agendadigital.clases.AdapterPublicidad;*/

public class FragmentViewPagerPublicidad extends Fragment {

    private AdminSQLite adm;
    RecyclerView recyclerViewPublicidades;

    private int codigoEmpresa;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

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
        ArrayList<Publicidad> publicidad = new ArrayList<>();
        Cursor cursor = adm.getPublicidad(Integer.toString(codigoEmpresa));
        if (cursor.moveToFirst()) {
            do {
                publicidad.add(new Publicidad(cursor.getString(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3)
                        ,cursor.getString(4)));

            } while (cursor.moveToNext());
            AdapterPublicidad data = new AdapterPublicidad(getContext() , publicidad);
            recyclerViewPublicidades.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewPublicidades.setAdapter(data);
        }
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


