package com.agendadigital.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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

public class FragmentFormAdm extends Fragment {
    private Button btnCancelar;
    private Button btnHabilitar;
    private EditText codigo;
    private EditText clave;
    private String cod_personal,clave_personal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View vista = inflater.inflate(R.layout.fragment_form_adm, container, false);
       hacerCast(vista);
       oncliks();
        return vista;
    }

    private void oncliks() {
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_home);
                closeKeyboard();
            }
        });
        btnHabilitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habilitar(v);
                closeKeyboard();
            }
        });
    }

    private void closeKeyboard() {
        View view = this.getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void habilitar(final View v) {
        if (validarDatos()){
            if (!existe()) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Validando...");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(false);
                progressDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsGlobals.url + "/habilitar.php?op=adm", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("ok")) {
                                AdminSQLite adminSQLite = new AdminSQLite(getContext(), "agenda", null, 1);

                                JSONObject adm = jsonObject.getJSONObject("adm");
                                String codigo = adm.getString("codigo");
                                String nombre = adm.getString("nombre");
                                String foto = adm.getString("foto");

                                ArrayList<String> valores = new ArrayList<>();
                                valores.add(codigo);
                                valores.add(nombre);
                                valores.add(foto);
                                adminSQLite.saveAdm(valores);
                                JSONArray colegios = jsonObject.getJSONArray("colegios");
                                for (int i = 0 ; i < colegios.length(); i++){
                                    JSONObject colegio = colegios.getJSONObject(i);
                                    String codAdm = colegio.getString("cod_adm");
                                    String nombreCol = colegio.getString("nombre");
                                    String turno = colegio.getString("turno");
                                    String codCol = colegio.getString("cod_col");
                                    String ip = colegio.getString("ip");
                                    String estado = colegio.getString("estado");
                                    adminSQLite.saveColAdm(codAdm,nombreCol,turno,codCol,ip,estado);
                                }


                                builder.setMessage("Se habilitó exitosamente...");
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
                        params.put("codigo", cod_personal);
                        params.put("clave", clave_personal);
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
        Cursor cursor = adm.profesor(cod_personal);
        return cursor.moveToFirst();
    }

    private boolean validarDatos() {
        cod_personal = codigo.getText().toString();
        clave_personal = clave.getText().toString();
        return !cod_personal.isEmpty() && cod_personal.length() < 4 && !clave_personal.isEmpty();
    }

    private void hacerCast(View vista) {
        btnCancelar = vista.findViewById(R.id.btnCancelarPersonal);
        btnHabilitar = vista.findViewById(R.id.btnHabilitarPersonal);
        codigo = vista.findViewById(R.id.ET_codigoPersonal);
        clave = vista.findViewById(R.id.ET_clavePersonal);
    }
}