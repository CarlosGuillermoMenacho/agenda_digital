package com.agendadigital.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.agendadigital.R;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.ConstantsGlobals;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.clases.User;
import com.agendadigital.clases.Utils;
import com.agendadigital.services.Service;
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

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FragmentFormTutor extends Fragment{
    private Button btnCancelar, btnHabilitar;
    private EditText etCedula,etTelefono;
    private String cedula, telefono;



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
                closeKeyboard();
                Navigation.findNavController(v).navigate(R.id.nav_home);
            }
        });
        btnHabilitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                habilitar(v);
            }
        });
    }

    public void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()){
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void habilitar(final View v) {
        if(validar()){
            if (!existe()){
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Validando...");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(false);
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsGlobals.url+ "/habilitar.php?op=tutor", new Response.Listener<String>() {
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

                                JSONObject datosArray = jsonObject.getJSONObject("tutor");

                                String nombre = datosArray.getString("nombre");
                                String codigo = datosArray.getString("codigo");
                                String fotot = datosArray.getString("foto");
                                ArrayList<String> valores = new ArrayList<>();
                                valores.add(codigo);
                                valores.add(nombre);
                                valores.add(fotot);//Agreagar aqui la foto desde el JSON
                                valores.add(cedula);
                                valores.add(telefono);
                                adminSQLite.saveTutor(valores);

                                Globals.user = new User(codigo, nombre, fotot, "tutor");

                                JSONArray datosArrayfact_pend = jsonObject.getJSONArray("alumnos");
                                //BaseDeDato.execSQL("delete from alumno");
                                for (int i = 0; i < datosArrayfact_pend.length(); i++) {
                                    ContentValues Registros1 = new ContentValues();
                                    ContentValues Registros2 = new ContentValues();
                                    JSONObject jsonObjectClientes = datosArrayfact_pend.getJSONObject(i);
                                    Registros1.put("codigo", jsonObjectClientes.getInt("codigo"));
                                    //codi=" "+jsonObjectClientes.getInt("codigo");
                                    Registros1.put("nombre", jsonObjectClientes.getString("nombre"));
                                    Registros1.put("curso", jsonObjectClientes.getString("curso"));
                                    Registros1.put("cod_cur", jsonObjectClientes.getInt("cod_cur"));
                                    Registros1.put("cod_par", jsonObjectClientes.getString("cod_par"));
                                    Registros1.put("colegio", jsonObjectClientes.getString("colegio"));
                                    Registros1.put("ip", jsonObjectClientes.getString("ip"));
                                    Registros1.put("cod_col", jsonObjectClientes.getInt("cod_col"));
                                    Registros1.put("foto", jsonObjectClientes.getString("foto"));
                                    Registros1.put("horario", "");
                                    if (!existealu(jsonObjectClientes.getInt("codigo"))) { // verifico si existe el alumno, para no grabarlo mas de una vez
                                        BaseDeDato.insert("alumno", null, Registros1);
                                    }
                                    Registros2.put("tutor", datosArray.getString("codigo"));
                                    Registros2.put("alu", jsonObjectClientes.getInt("codigo"));
                                    BaseDeDato.insert("alu_tut", null, Registros2);
                                }

                                builder.setMessage("Se habilitó exitosamente...");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requireActivity().stopService(new Intent(getContext(), Service.class));
                                        Navigation.findNavController(v).navigate(R.id.nav_home);
                                    }
                                });
                            } else {
                                builder.setMessage("El usuario NO existe...");
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
    private boolean existealu(int cod_alu) {
        AdminSQLite adm = new AdminSQLite(getContext(),"agenda",null,1);
        Cursor cursor = adm.verif_alu(cod_alu);
        return cursor.moveToFirst();
    }
    private void hacerCast(View vista) {
       btnCancelar  = vista.findViewById(R.id.btnCancelarformtutor);
       btnHabilitar = vista.findViewById(R.id.btnHabilitarformtutor);
       etCedula = vista.findViewById(R.id.etCedulaformtutor);
       etTelefono = vista.findViewById(R.id.etTelefonoformtutor);
    }
}