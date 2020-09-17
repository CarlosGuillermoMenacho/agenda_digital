package com.agendadigital.Fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.agendadigital.R;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.Estudiante;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.clases.Notificacion;
import com.agendadigital.clases.Notificaciones;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentListaAlumnos extends Fragment {

    private ListView lvListaAlumnosBoletin;
    private AdminSQLite adm;
    private ArrayList<Estudiante> estudiantes;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_lista_alumnos, container, false);
        adm = new AdminSQLite(getContext(),"agenda",null, 1);
        enlaces(root);
        llenarLista();
        requestEstudiantes();
        oncliks();
        return root;
    }


    private void requestEstudiantes() {
        if (Globals.user.getCodigo()!=null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.url + "/estudiante.php?op=alu_tutor", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("ok")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("estudiantes");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONArray fila = jsonArray.getJSONArray(i);
                                Cursor cursor = adm.estudiante(fila.getString(0));
                                if (!cursor.moveToFirst()) {
                                    adm.tutor_alu(Globals.user.getCodigo(), fila.getString(0));
                                    adm.saveAlumno(fila.getString(0), fila.getString(1), fila.getString(2), fila.getString(3));
                                }
                            }
                            llenarLista();
                        } else {
                            Toast.makeText(getContext(), jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
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
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("codigo", Globals.user.getCodigo());
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getContext()).addToRequest(stringRequest);
        }
    }

    private void llenarLista() {
        Cursor cursor = adm.estudiantes(Globals.user.getCodigo());
        estudiantes = new ArrayList<>();
        ArrayList<String> nombres = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                estudiantes.add(new Estudiante(cursor.getString(0),cursor.getString(1)));
                nombres.add(cursor.getString(1));
            }while (cursor.moveToNext());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, nombres);
            lvListaAlumnosBoletin.setAdapter(adapter);
        }
    }

    private void oncliks() {
        lvListaAlumnosBoletin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Globals.estudiante = estudiantes.get(position);
                Cursor cursor = adm.getNotificaciones(Globals.estudiante.getCodigo(),Globals.user.getCodigo());
                if (cursor.moveToFirst()){
                    Globals.notificaciones = new Notificaciones();
                    do {
                        Globals.notificaciones.add(new Notificacion(cursor.getInt(0),cursor.getInt(1),cursor.getInt(3),cursor.getInt(7),cursor.getInt(4),cursor.getString(2),cursor.getString(5),cursor.getString(6)));
                    }while (cursor.moveToNext());
                }
                cursor.close();

                switch (Globals.menu){
                    case 0:
                        Toast.makeText(getContext(),"Ir a Licencia",Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Navigation.findNavController(view).navigate(R.id.boletinFragment);
                        break;
                    case 2:
                        Toast.makeText(getContext(),"Ir a Administrar cuenta",Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Navigation.findNavController(view).navigate(R.id.kardexPagoFragment);
                        break;
                    case 4:
                        Navigation.findNavController(view).navigate(R.id.fragmentTabDinamico);
                        break;
                }
            }
        });
    }

    private void enlaces(View root) {
        lvListaAlumnosBoletin = root.findViewById(R.id.lvListaAlumnosBoletin);
    }
}