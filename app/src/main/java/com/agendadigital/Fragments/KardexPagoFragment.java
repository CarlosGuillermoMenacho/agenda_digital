package com.agendadigital.Fragments;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.agendadigital.R;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.MySingleton;

import com.agendadigital.clases.TableDynamic_pagos;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class KardexPagoFragment extends Fragment {

    private String codigoAlumno;
    AlertDialog.Builder builder;
    private TableLayout tableLayout;
    private String id2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        codigoAlumno = Globals.estudiante.getCodigo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista2=inflater.inflate(R.layout.fragment_kardexpago, container, false);

        final TableDynamic_pagos tableDynamic = new TableDynamic_pagos(tableLayout, getContext());
        final TextView tvcurso=vista2.findViewById(R.id.tvCurso);
        final TextView tvalumno=vista2.findViewById(R.id.tvAlumno);
        tableLayout=vista2.findViewById(R.id.pagos);

        tvalumno.setText(codigoAlumno);
        id2=tvalumno.getText().toString();
        builder=new AlertDialog.Builder(getContext());
        if (!id2.isEmpty()) {
            AdminSQLite dbs = new AdminSQLite(getContext(), "agenda", null, 1);
            SQLiteDatabase sdq = dbs.getReadableDatabase();
            Toast.makeText(getContext(), "Espere mientras se descargan los datos", Toast.LENGTH_LONG).show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+Globals.estudiante.getIp() + "/agendadigital/get_kardex_pago.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        builder.setTitle("Server Response:");
                        JSONObject jsonObject = new JSONObject(response);
                        final String status = jsonObject.getString("status");
                        final String message = jsonObject.getString("message");
                        builder.setMessage("Response: " + message);
                        if (status.equals("200")) {
                            String paterno="";
                            String materno="";
                            String nombres="";
                            String curso="";

                            AdminSQLite admin = new AdminSQLite(getContext(), "agenda", null, 1);
                            SQLiteDatabase BaseDeDato = admin.getWritableDatabase();

                            JSONObject datosArrayClientes = jsonObject.getJSONObject("alumno");

                            paterno = datosArrayClientes.getString("paterno");
                            materno= datosArrayClientes.getString("materno");
                            nombres= datosArrayClientes.getString("nombres");
                            curso=datosArrayClientes.getString("curso");
                            tvalumno.setText(nombres+" "+paterno+" "+materno);
                            tvcurso.setText(curso);

                            JSONArray datosArrayCategor_ = jsonObject.getJSONArray("pagos");
                            BaseDeDato.execSQL("delete from kar_alu");
                            for (int i = 0; i < datosArrayCategor_.length(); i++) {
                                ContentValues RegistrosNotas = new ContentValues();
                                JSONObject jsonObject1 = datosArrayCategor_.getJSONObject(i);
                                RegistrosNotas.put("codigo", jsonObject1.getInt("codigo"));
                                RegistrosNotas.put("detalle", jsonObject1.getString("detalle"));
                                RegistrosNotas.put("fecha", jsonObject1.getString("fecha"));
                                RegistrosNotas.put("recnum", jsonObject1.getString("recnum"));
                                RegistrosNotas.put("haber", jsonObject1.getString("haber"));
                                RegistrosNotas.put("acreedor", jsonObject1.getString("acreedor"));
                                BaseDeDato.insert("kar_alu", null, RegistrosNotas);
                            }

                            BaseDeDato.close();
                            Toast.makeText(getContext(), "GUARDANDO", Toast.LENGTH_SHORT).show();

                            tableDynamic.cargarPagos(tableLayout, getContext());
                        }
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        //AlertDialog alertDialog = builder.create();
                        // alertDialog.show();
                    } catch (JSONException e) {
                        builder.setTitle("Server Response:");
                        builder.setMessage("Response: HUBO UN PROBLEMA MIENTRAS SE CARGABAN LOS DATOS VUELVE A INTENTARLO OTRA VEZ...");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //   Intent intent = new Intent(Ver_tuconsumo.this,Tu_consumo.class);
                                //   startActivity(intent);  // inicia el nuevo layout - pantalla en este caso VerRegistro
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        Log.e("Ver_tuconsumo", e.toString());
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "ERROR EN LA RED", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", id2);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getContext()).addToRequest(stringRequest);


        }else{
            Toast.makeText(getContext(),"Faltan Datos...",Toast.LENGTH_SHORT).show();
        }
        return vista2;
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