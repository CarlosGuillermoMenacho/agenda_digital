package com.agendadigital.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

public class FragmentFormTutor extends Fragment {
    private Button btnCancelar, btnHabilitar;
    private EditText etCedula,etTelefono;
    private String cedula, telefono;
    public FragmentFormTutor() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_form_tutor, container, false);
        hacerCast(vista);
        oncliks();


        return vista;
    }

    private void oncliks() {
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_home);
            }
        });
        btnHabilitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habilitar(v);
            }
        });
    }

    private void habilitar(final View v) {
        if(validar()){
            if (!existe()){
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Validando...");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(false);
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.url + "/habilitar.php?op=tutor", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            AdminSQLite adminSQLite = new AdminSQLite(getContext(), "agenda", null, 1);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("ok")) {
                                JSONObject tutor = jsonObject.getJSONObject("tutor");

                                String nombre = tutor.getString("nombre");
                                String codigo = tutor.getString("codigo");
                                JSONArray alumnos = jsonObject.getJSONArray("alumnos");


                                for (int i = 0; i< alumnos.length(); i++ ){
                                    JSONObject alumno = alumnos.getJSONObject(i);
                                    String codigoAlu = alumno.getString("codigo");
                                    String nombreAlumno = alumno.getString("nombre");
                                    String curso = alumno.getString("curso");
                                    String cod_curso = alumno.getString("cod_cur");
                                    String colegio = alumno.getString("colegio");
                                    String ip = alumno.getString("ip");
                                    String cod_col = alumno.getString("cod_col");
                                    String fotoAlu = alumno.getString("foto");

                                    adminSQLite.saveAlumno(codigoAlu, nombreAlumno, curso, cod_curso, colegio,
                                                            ip, cod_col, fotoAlu,0);
                                    adminSQLite.tutor_alu(codigo,codigoAlu);
                                }

                                ArrayList<String> valores = new ArrayList<>();
                                valores.add(codigo);
                                valores.add(nombre);
                                valores.add("");
                                valores.add(cedula);
                                valores.add(telefono);

                                adminSQLite.saveTutor(valores);

                                builder.setMessage("Se ha habilitado exitosamente...");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Navigation.findNavController(v).navigate(R.id.nav_home);
                                    }
                                });
                            } else {
                                builder.setMessage("El usuario no existe...");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                            }
                            builder.show();
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
                        params.put("cedula", cedula);
                        params.put("telefono", telefono);
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                MySingleton.getInstance(getContext()).addToRequest(stringRequest);
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("El usuario ya se encuentra habilitado en este dispositivo...");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Navigation.findNavController(v).navigate(R.id.nav_home);
                    }
                });
                builder.show();
            }
        }else{
            Toast.makeText(getContext(),"Faltan datos...",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validar() {
        cedula = etCedula.getText().toString();
        telefono = etTelefono.getText().toString();
        return !cedula.isEmpty() && !telefono.isEmpty();
    }
    private boolean existe() {
        AdminSQLite adm = new AdminSQLite(getContext(),"agenda",null,1);
        Cursor cursor = adm.tutor(cedula,telefono);
        return cursor.moveToFirst();
    }

    private void hacerCast(View vista) {
       btnCancelar  = vista.findViewById(R.id.btnCancelarformtutor);
       btnHabilitar = vista.findViewById(R.id.btnHabilitarformtutor);
       etCedula = vista.findViewById(R.id.etCedulaformtutor);
       etTelefono = vista.findViewById(R.id.etTelefonoformtutor);
    }
}



















/*

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
                        adm.saveAlumno(fila.getString(0), fila.getString(1),
                                fila.getString(2), fila.getString(3),fila.getString(4),
                                fila.getString(5), fila.getString(6),fila.getString(7),
                                0);
                    }
                }
                llenarLista();
            }
*/


















