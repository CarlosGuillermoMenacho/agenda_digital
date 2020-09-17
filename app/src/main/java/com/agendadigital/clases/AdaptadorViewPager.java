package com.agendadigital.clases;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorViewPager extends FragmentPagerAdapter {
    private final List<Fragment> listaDeFragmentos = new ArrayList<>();
    private final List<String> listaDeTitulosDeFragmentos = new ArrayList<>();

    public AdaptadorViewPager(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return listaDeFragmentos.get(position);
    }

    @Override
    public int getCount() {
        return listaDeFragmentos.size();
    }

    public void agregarFragmento(Fragment fragment, String title) {
        listaDeFragmentos.add(fragment);
        listaDeTitulosDeFragmentos.add(title);
    }

    // Si es el título de la última pestaña, regresamos null, lo
    // cual regresará el icono únicamente
    @Override
    public CharSequence getPageTitle(int position) {
        return listaDeTitulosDeFragmentos.get(position);
    }
}
