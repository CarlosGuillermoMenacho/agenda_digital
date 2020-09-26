package com.agendadigital.Fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agendadigital.R;
import com.agendadigital.clases.AdaptadorViewPager;
import com.agendadigital.clases.AdminSQLite;
import com.google.android.material.tabs.TabLayout;

public class FragmentPublicidad extends Fragment {

    private ViewPager viewPagerPublicidad;
    private AdaptadorViewPager adaptadorViewPagerPublicidad;
    private AdminSQLite adm;
    private TabLayout tabLayoutPublicidad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adm = new AdminSQLite(getContext(), "publicidad", null, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_publicidad, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPagerPublicidad = view.findViewById(R.id.viewPagerPublicidad);
        tabLayoutPublicidad = view.findViewById(R.id.tabLayoutPublicidad);
        adaptadorViewPagerPublicidad = new AdaptadorViewPager(requireActivity().getSupportFragmentManager());
        tabLayoutPublicidad.setupWithViewPager(viewPagerPublicidad);
        TabsPublicidad();
        tabLayoutPublicidad.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void TabsPublicidad() {

        adaptadorViewPagerPublicidad = new AdaptadorViewPager(getChildFragmentManager());
        adaptadorViewPagerPublicidad.agregarFragmento(new FragmentViewPagerPublicidad(1),"Publicidad");
        Cursor cursor = adm.getEmpresas();
        if (cursor.moveToFirst()){
            do {
                adaptadorViewPagerPublicidad.agregarFragmento(new FragmentViewPagerPublicidad(10)
                                                              ,cursor.getString(2));
            } while (cursor.moveToNext());
        }
        viewPagerPublicidad.setAdapter(adaptadorViewPagerPublicidad);

    }
}