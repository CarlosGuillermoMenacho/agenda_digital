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
import com.agendadigital.clases.MySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentFormAlumno extends Fragment {

    public FragmentFormAlumno() { }
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    private Button btnCancelar, btnHabilitar;
    private EditText codigo;
    private EditText clave;
    private String cod_alu, clave_alu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_form_alumno, container, false);
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
        if (validarDatos()){
            if (!existe()) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Validando...");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(false);
                progressDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.url + "/habilitar.php?op=alu", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("ok")) {
                                String nombre = jsonObject.getString("nombre");

                                AdminSQLite adminSQLite = new AdminSQLite(getContext(), "agenda", null, 1);
                                ArrayList<String> valores = new ArrayList<>();
                                valores.add(cod_alu);
                                valores.add(nombre);
                                adminSQLite.saveProfesor(valores);

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
                            Toast.makeText(getContext(),"Error al procesar los datos...",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Error en la red...",Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("codigo", cod_alu);
                        params.put("clave", clave_alu);
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

    private boolean existe() {
        AdminSQLite adm = new AdminSQLite(getContext(),"agenda",null,1);
        Cursor cursor = adm.estudiante(cod_alu);
        return cursor.moveToFirst();
    }

    private boolean validarDatos() {
        cod_alu = codigo.getText().toString();
        clave_alu = clave.getText().toString();
        return !cod_alu.isEmpty() && cod_alu.length() < 4 && !clave_alu.isEmpty();
    }

    private void hacerCast(View vista) {
        btnCancelar  = vista.findViewById(R.id.btnCancelarformalumno);
        btnHabilitar = vista.findViewById(R.id.btnHabilitarformalumno);
        codigo = vista.findViewById(R.id.etcodigoformalumno);
        clave = vista.findViewById(R.id.etclaveformalumno);
    }

}