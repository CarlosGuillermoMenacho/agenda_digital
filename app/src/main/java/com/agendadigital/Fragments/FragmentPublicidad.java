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
import android.widget.Toast;

import com.agendadigital.R;
import com.agendadigital.clases.AdaptadorViewPager;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.MySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;

public class FragmentPublicidad extends Fragment {

    private ViewPager viewPagerPublicidad;
    private AdaptadorViewPager adaptadorViewPagerPublicidad;
    private AdminSQLite adm;
    private TabLayout tabLayoutPublicidad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adm = new AdminSQLite(getContext(), "agenda", null, 1);
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

        requestImgPublicidad();

        viewPagerPublicidad = view.findViewById(R.id.viewPagerPublicidad);
        tabLayoutPublicidad = view.findViewById(R.id.tabLayoutPublicidad);

        adaptadorViewPagerPublicidad = new AdaptadorViewPager(requireActivity().getSupportFragmentManager());


        TabsPublicidad();
        tabLayoutPublicidad.setupWithViewPager(viewPagerPublicidad);
        tabLayoutPublicidad.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }


    private void requestImgPublicidad() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.url
                + "/publicidad.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i =0; i < jsonArray.length(); i++){
                        JSONArray fila = jsonArray.getJSONArray(i);
                        String urlPublicidad = fila.getString(0);
                        String nombrePublicidad = fila.getString(1);
                        String imgPublicidad = fila.getString(2);
                        String ubicacionPublicidad = fila.getString(3);
                        String codigoPublicidad = fila.getString(4);

                        Cursor cursor = adm.getPublicidad(codigoPublicidad);

                        if (!cursor.moveToFirst()) {
                            adm.savePublicidad(urlPublicidad, nombrePublicidad ,imgPublicidad,
                                    ubicacionPublicidad,codigoPublicidad);
                        }
                        TabsPublicidad();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error en la red...", Toast.LENGTH_SHORT).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(stringRequest);



    }
    private void TabsPublicidad() {

        adaptadorViewPagerPublicidad = new AdaptadorViewPager(getChildFragmentManager());
        Cursor cursor = adm.getPublicidad();

        if (cursor.moveToFirst()){
            do {
                String letra = cursor.getString(1);

                adaptadorViewPagerPublicidad.agregarFragmento(new FragmentViewPagerPublicidad(cursor.getInt(4)),
                                                                  cursor.getString(1) );
            } while (cursor.moveToNext());
        }

        viewPagerPublicidad.setAdapter(adaptadorViewPagerPublicidad);
    }
}
