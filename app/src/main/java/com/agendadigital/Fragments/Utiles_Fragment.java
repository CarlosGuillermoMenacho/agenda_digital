package com.agendadigital.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.agendadigital.R;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.MySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Utiles_Fragment extends Fragment {

    private ListView lvListaAlumnosBoletin;
    private AdminSQLite adm;
    private int anio;

    Calendar c = Calendar.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_utiles, container, false);
        lvListaAlumnosBoletin = root.findViewById(R.id.lvListaAlumnosBoletin_uti);
        adm = new AdminSQLite(getContext(),"agenda",null, 1);
        TextView titulo = root.findViewById(R.id.text_gallery);
        titulo.setText(Globals.estudiante.getNombre());
        anio=c.get(Calendar.YEAR);

        llenarLista_utiles();

        return root;
    }
    private void llenarLista_utiles() {
        Cursor cursor = adm.lista_utiles(Globals.estudiante.getCodigo(),anio);
        ArrayList<String> nombres = new ArrayList<>();
        if (cursor.moveToFirst()){ // existe la lista localmente
            do {
                nombres.add("Materia: "+cursor.getString(3));
                nombres.add(cursor.getString(4));
            }while (cursor.moveToNext());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, nombres);
            lvListaAlumnosBoletin.setAdapter(adapter);
        }else{  // no existe en el celular, hay que descargarla
            String d_ip = Globals.estudiante.getIp();
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Obteniendo Lista de útiles...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+d_ip + "/agendadigital/get_lista_utiles.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("ok")) {
                            AdminSQLite adminSQLite = new AdminSQLite(getContext(), "agenda", null, 1);
                            SQLiteDatabase BaseDeDato = adminSQLite.getWritableDatabase();

                            JSONArray datosArray = jsonObject.getJSONArray("utiles");
                            for (int i = 0; i < datosArray.length(); i++) {
                                ContentValues Registros1 = new ContentValues();
                                JSONObject jsonObjectClientes = datosArray.getJSONObject(i);
                                Registros1.put("codigo", Globals.estudiante.getCodigo());
                                Registros1.put("gestion", anio);
                                Registros1.put("cod_mat", jsonObjectClientes.getString("cod_mat"));
                                Registros1.put("materia", jsonObjectClientes.getString("materia"));
                                Registros1.put("descrip", jsonObjectClientes.getString("descrip"));
                                BaseDeDato.insert("utiles", null, Registros1);

                            }
                            Cursor cursor = adm.lista_utiles(Globals.estudiante.getCodigo(),anio);
                            ArrayList<String> nombres = new ArrayList<>();
                            if (cursor.moveToFirst()){ // existe la lista localmente
                                do {
                                    nombres.add("Materia: "+cursor.getString(3));
                                    nombres.add(cursor.getString(4));
                                }while (cursor.moveToNext());

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, nombres);
                                lvListaAlumnosBoletin.setAdapter(adapter);
                            }

                        } else {
                            builder.setMessage("NO existe lista de útiles");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Error al procesar los datos...", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),"Error en la red...",Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("cod_cur", Globals.estudiante.getCod_cur());
                    params.put("cod_par", Globals.estudiante.getCod_par());
                    params.put("gestion", anio+"");
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getContext()).addToRequest(stringRequest);
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