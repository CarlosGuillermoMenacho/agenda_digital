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
import androidx.viewpager.widget.ViewPager;

import com.agendadigital.R;
import com.agendadigital.clases.AdaptadorViewPager;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Globals;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class FragmentTabDinamico extends Fragment {
    private ViewPager viewPager;
    private AdaptadorViewPager adaptadorViewPager;
    private AdminSQLite adm;
    TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adm = new AdminSQLite(getContext(),"agenda",null,1);
        Globals.tabsActivos = new ArrayList<>();

        adm.testEstados(Globals.user.getCodigo(),Globals.estudiante.getCodigo());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_dinamico, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.viewPagerAvisos);
        tabLayout = view.findViewById(R.id.tabLayoutAvisos);
        adaptadorViewPager = new AdaptadorViewPager(requireActivity().getSupportFragmentManager());
        tabLayout.setupWithViewPager(viewPager);

        cargarTabs();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    public void cargarTabs() {

        adaptadorViewPager = new AdaptadorViewPager(getChildFragmentManager());

        adaptadorViewPager.agregarFragmento(new FragmentNotificationContainer(),"Notificaciones");
        Cursor cursor = adm.getMaterias(Globals.estudiante.getCodigo());
        if (cursor.moveToFirst()){
            do {
                FragmentNotificationContainer fragmentNotificationContainer = new FragmentNotificationContainer();
                Bundle bundle = new Bundle();
                bundle.putString("codigoMateria", cursor.getString(1));
                fragmentNotificationContainer.setArguments(bundle);
                adaptadorViewPager.agregarFragmento( fragmentNotificationContainer ,cursor.getString(2));
            }while (cursor.moveToNext());
        }
        viewPager.setAdapter(adaptadorViewPager);
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