package com.agendadigital.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
import com.agendadigital.core.services.login.UserDto;
import com.agendadigital.core.shared.infrastructure.Firebase;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FragmentFormProfesor extends Fragment {

    private final String TAG = "FragmentFormProfesor";
    private Button btnCancelar;
    private Button btnHabilitar;
    private EditText codigo;
    private EditText clave;
    private String cod_prof,clave_prof;
    private Firebase firebase;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View vista = inflater.inflate(R.layout.fragment_form_profesor, container, false);
       hacerCast(vista);
       oncliks();
       firebase = Firebase.getInstance();
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
        if (validarDatos()){
            if (!existe()) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Validando...");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(false);
                progressDialog.show();
                UserDto.LoginUserRequest loginUserRequest = new UserDto.LoginUserRequest(cod_prof, User.UserType.Tutor.getValue(), firebase.getToken());
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("token", new JSONObject(loginUserRequest.toString()));

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,ConstantsGlobals.urlChatServer + "/token", jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject tokenResponse = response.getJSONObject("UserToken");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "onResponse: " + response);
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        }
                    });
                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);
                }catch (Exception e) {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
                StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsGlobals.url + "/habilitar.php?op=profesor", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("ok")) {

                                ArrayList<String[]> lista_col=new ArrayList<>();

                                AdminSQLite adminSQLite = new AdminSQLite(getContext(), "agenda", null, 1);
                                SQLiteDatabase BaseDeDato = adminSQLite.getWritableDatabase();

                                JSONObject datosArray = jsonObject.getJSONObject("profesor");

                                String nombre = datosArray.getString("nombre");
                                String codigo = datosArray.getString("codigo");
                                String foto = datosArray.getString("foto");
                                ArrayList<String> valores = new ArrayList<>();
                                valores.add(codigo);
                                valores.add(nombre);
                                valores.add(foto);
                                adminSQLite.saveProfesor(valores);

                                Globals.user = new User(codigo,nombre,foto, User.UserType.Teacher);

                                JSONArray datosArrayfact_pend = jsonObject.getJSONArray("colegios");
                                //BaseDeDato.execSQL("delete from alumno");
                                for (int i = 0; i < datosArrayfact_pend.length(); i++) {
                                    ContentValues Registros1 = new ContentValues();
                                    ContentValues Registros2 = new ContentValues();
                                    JSONObject jsonObjectClientes = datosArrayfact_pend.getJSONObject(i);
                                    Registros1.put("cod_col", jsonObjectClientes.getInt("cod_col"));
                                    Registros1.put("nombre", jsonObjectClientes.getString("nombre"));
                                    Registros1.put("ip", jsonObjectClientes.getString("ip"));
                                    Registros1.put("turno", jsonObjectClientes.getString("turno"));
                                    if (!existeCol(jsonObjectClientes.getInt("cod_col"))) { // verifico si existe el alumno, para no grabarlo mas de una vez
                                        BaseDeDato.insert("colegios", null, Registros1);
                                    }
                                    lista_col.add(new String[]{jsonObjectClientes.getInt("cod_col")+"",jsonObjectClientes.getString("ip")});
                                    Registros2.put("cod_pro", jsonObjectClientes.getString("cod_pro"));
                                    Registros2.put("cod_col", jsonObjectClientes.getInt("cod_col"));
                                    Registros2.put("estado", 1);
                                    BaseDeDato.insert("prof_col", null, Registros2);
                                }

                                obt_cursos_listas(codigo,lista_col);



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
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String,String> header = new HashMap<>();
                        header.put("codigo",cod_prof);
                        header.put("clave",clave_prof);
                        return header;
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
        Cursor cursor = adm.profesor(cod_prof);
        return cursor.moveToFirst();
    }
    private boolean existeCol(int cod_col) {
        AdminSQLite adm = new AdminSQLite(getContext(),"agenda",null,1);
        Cursor cursor = adm.verif_col(cod_col);
        return cursor.moveToFirst();
    }
    private boolean validarDatos() {
        cod_prof = codigo.getText().toString();
        clave_prof = clave.getText().toString();
        return !cod_prof.isEmpty() && cod_prof.length() < 4 && !clave_prof.isEmpty();
    }

    private void hacerCast(View vista) {
        btnCancelar = vista.findViewById(R.id.btnCancelarProfesor);
        btnHabilitar = vista.findViewById(R.id.btnHabilitarProfesor);
        codigo = vista.findViewById(R.id.ET_codigoProfesor);
        clave = vista.findViewById(R.id.ET_claveProfesor);
    }
    private void obt_cursos_listas(final String codi_profe, final ArrayList<String[]> lista_cole){
        for (int j = 0; j < lista_cole.size(); j++) {
            String d_ip= lista_cole.get(j)[1];
            final String codCol = lista_cole.get(j)[0];
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+d_ip+ "/agendadigital/obt_cur_mat.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("ok")) {
                            JSONArray cursos = jsonObject.getJSONArray("cursos");
                            AdminSQLite adm = new AdminSQLite(getContext(), "agenda", null, 1);
                            ArrayList<String[]> codigoCursos = new ArrayList<>();
                            for (int i = 0; i < cursos.length(); i++){
                                JSONArray fila = cursos.getJSONArray(i);
                                String codCur = fila.getString(0);
                                String paralelo = fila.getString(1);
                                String nombre = fila.getString(2);
                                codigoCursos.add(new String[]{codCur,paralelo});

                                adm.saveCursoProf(codi_profe,codCol,codCur,paralelo,nombre);

                            }

                            JSONArray materias = jsonObject.getJSONArray("materias");
                            for (int i = 0; i < materias.length(); i++){
                                JSONArray fila = materias.getJSONArray(i);
                                String codmat = fila.getString(0);
                                String nombre = fila.getString(1);
                                adm.saveMateriasProf(codi_profe,codCol,codmat,nombre);
                            }

                            JSONArray listas = jsonObject.getJSONArray("listas");
                            for (int i = 0; i < listas.length(); i++){
                                JSONArray fila = listas.getJSONArray(i);
                                for (int j = 0; j < fila.length(); j++){
                                    JSONArray alumno = fila.getJSONArray(j);
                                    String codAlu = alumno.getString(0);
                                    String nombre = alumno.getString(1);
                                    adm.saveListaAlumno(codi_profe,codCol,codigoCursos.get(i)[0],codigoCursos.get(i)[1],codAlu,nombre);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("codigo", codi_profe);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getContext()).addToRequest(stringRequest);
        }
    }

}