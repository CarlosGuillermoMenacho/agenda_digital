package com.agendadigital.Fragments;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agendadigital.R;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.clases.TableDynamic;
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
//import androidx.appcompat.app.AppCompatActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BoletinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoletinFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String codigoAlumno;
    AlertDialog.Builder builder;
    private TableLayout tableLayout;
    private String id2;

    public BoletinFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BoletinFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BoletinFragment newInstance(String param1, String param2) {
        BoletinFragment fragment = new BoletinFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            
        }
        codigoAlumno = Globals.estudiante.getCodigo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista=inflater.inflate(R.layout.fragment_boletin, container, false);

        final TableDynamic tableDynamic = new TableDynamic(tableLayout, getContext());
        final TextView tvcurso=vista.findViewById(R.id.tvCurso);
        final TextView tvalumno=vista.findViewById(R.id.tvAlumno);
        tableLayout=vista.findViewById(R.id.table);
        tvalumno.setText(codigoAlumno);
        id2=tvalumno.getText().toString();
        builder=new AlertDialog.Builder(getContext());
        if (!id2.isEmpty()) {
            AdminSQLite dbs = new AdminSQLite(getContext(), "dbReader", null, 1);
            SQLiteDatabase sdq = dbs.getReadableDatabase();
            Toast.makeText(getContext(), "Espere mientras se descargan los datos", Toast.LENGTH_LONG).show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.url + "/get_notas_3ro.php", new Response.Listener<String>() {
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
                            AdminSQLite admin = new AdminSQLite(getContext(), "dbReader", null, 1);
                            SQLiteDatabase BaseDeDato = admin.getWritableDatabase();

                            JSONObject datosArrayClientes = jsonObject.getJSONObject("alumno");

                            paterno = datosArrayClientes.getString("paterno");
                            materno= datosArrayClientes.getString("materno");
                            nombres= datosArrayClientes.getString("nombres");
                            curso=datosArrayClientes.getString("curso");
                            tvalumno.setText(nombres+" "+paterno+" "+materno);
                            tvcurso.setText(curso);

                            JSONArray datosArrayCategor_ = jsonObject.getJSONArray("notas");
                            BaseDeDato.execSQL("delete from notas");
                            for (int i = 0; i < datosArrayCategor_.length(); i++) {
                                ContentValues RegistrosNotas = new ContentValues();
                                JSONObject jsonObject1 = datosArrayCategor_.getJSONObject(i);
                                RegistrosNotas.put("codigo", jsonObject1.getInt("codigo"));
                                RegistrosNotas.put("cod_mat", jsonObject1.getString("cod_mat"));
                                RegistrosNotas.put("descri", jsonObject1.getString("descri"));
                                RegistrosNotas.put("nota1", jsonObject1.getString("nota1"));
                                RegistrosNotas.put("nota2", jsonObject1.getString("nota2"));
                                RegistrosNotas.put("nota3", jsonObject1.getString("nota3"));
                                BaseDeDato.insert("notas", null, RegistrosNotas);
                            }

                            BaseDeDato.close();
                            Toast.makeText(getContext(), "GUARDANDO", Toast.LENGTH_SHORT).show();

                            tableDynamic.cargarNotas(tableLayout, getContext());
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

        return vista;
    }
}