package com.agendadigital.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.agendadigital.R;
import com.agendadigital.clases.AdaptadorViewPager;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Estudiante;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.User;
import com.google.android.material.tabs.TabLayout;

public class FragmentTabDinamico extends Fragment {
    private ViewPager viewPager;
    private AdaptadorViewPager adaptadorViewPager;
    private AdminSQLite adm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        adm = new AdminSQLite(getContext(),"agenda",null,1);
        return inflater.inflate(R.layout.fragment_tab_dinamico, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.viewPagerAvisos);
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutAvisos);
        adaptadorViewPager = new AdaptadorViewPager(requireActivity().getSupportFragmentManager());
        tabLayout.setupWithViewPager(viewPager);
        cargarTabs();
       // contadorNotificaciones(tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                adm.testEstados("1","1","1");
                tab.removeBadge();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
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
                bundle.putInt("codigoMateria", cursor.getInt(1) );
                fragmentNotificationContainer.setArguments(bundle);
                adaptadorViewPager.agregarFragmento( fragmentNotificationContainer ,cursor.getString(2));
            }while (cursor.moveToNext());
        }
        viewPager.setAdapter(adaptadorViewPager);
    }

    private void contadorNotificaciones(TabLayout tabLayout) {
        int cursorContadorNotificaciones = adm.getCountNotificacion("1", "1", "1");
        tabLayout.getTabAt(1).getOrCreateBadge().setNumber(cursorContadorNotificaciones);
    }
}