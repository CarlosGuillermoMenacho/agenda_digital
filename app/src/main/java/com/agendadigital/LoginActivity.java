package com.agendadigital;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Colegio;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.ConstantsGlobals;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.clases.User;
import com.agendadigital.clases.Usuarios;
import com.agendadigital.core.services.login.UserDto;
import com.agendadigital.core.shared.infrastructure.Firebase;
import com.agendadigital.core.shared.infrastructure.utils.DirectoryManager;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";
    private EditText ed_codigo, ed_clave;
    private Button btn_iniciar_sesion,
                   btn_cancelar_sesion,
                   btn_tutor,
                   btn_docente,
                   btn_alumno,
                   btn_administracion;
    private TextView tv_title;
    private User.UserType userType;
    private AdminSQLite adm;
    private Firebase firebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btn_iniciar_sesion = findViewById(R.id.btn_inicio_sesion);
        btn_cancelar_sesion = findViewById(R.id.button_cancelar_sesion);
        btn_tutor = findViewById(R.id.button_tutor);
        btn_docente = findViewById(R.id.button_docente);
        btn_alumno = findViewById(R.id.button_alumno);
        btn_administracion = findViewById(R.id.button_adm);
        ed_codigo = findViewById(R.id.editText_codigo);
        ed_clave = findViewById(R.id.editText_clave);
        tv_title = findViewById(R.id.textView2);
        firebase = Firebase.getInstance();
        btn_iniciar_sesion.setOnClickListener(v -> {
           String clave = ed_clave.getText().toString();
           String codigo = ed_codigo.getText().toString();

           if(clave.isEmpty()||codigo.isEmpty()){
               Toast.makeText(getApplicationContext(),"Debe llenar los campos requeridos",Toast.LENGTH_SHORT).show();
           }else{
                iniciar_sesion();
           }
        });

        btn_tutor.setOnClickListener(v -> {
            userType = User.UserType.Tutor;
            cleanForm();
            visivility_btn_sesion(View.GONE);
            showLoginForm(userType);
        });
        btn_docente.setOnClickListener(v -> {
            userType = User.UserType.Teacher;
            cleanForm();
            visivility_btn_sesion(View.GONE);
            showLoginForm(userType);
        });
        btn_alumno.setOnClickListener(v -> {
            userType = User.UserType.Student;
            cleanForm();
            visivility_btn_sesion(View.GONE);
            showLoginForm(userType);
        });
        btn_administracion.setOnClickListener(v -> {
            userType = User.UserType.Staff;
            cleanForm();
            visivility_btn_sesion(View.GONE);
            showLoginForm(userType);
        });
        btn_cancelar_sesion.setOnClickListener(v -> {
            tv_title.setText(R.string.iniciar_sesi_n_como);
            visivility_btn_sesion(View.VISIBLE);
            visivility_form(View.GONE);
        });
    }

    private void iniciar_sesion() {
        switch (userType){
            case Tutor:
                habilitar_tutor();
                break;
            case Student:
                habilitar_alumno();
                break;
            case Staff:
                habilitar_administrador();
                break;
            case Teacher:
                habilitar_docente();
                break;
            case Director:
                habilitar_director();
                break;
        }
    }

    private boolean existe_director() {
        AdminSQLite adm = new AdminSQLite(getApplicationContext(),"agenda",null,1);
        Cursor cursor = adm.director(ed_codigo.getText().toString());
        return !cursor.moveToFirst();
    }

    private void habilitar_director() {
        if (existe_director()) {
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Validando...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsGlobals.url + "/habilitar.php?op=director", response -> {
                progressDialog.dismiss();
                try {
                    ArrayList<Colegio> listacolegios = new ArrayList<>();
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")) {
                        AdminSQLite adminSQLite = new AdminSQLite(getApplicationContext(), "agenda", null, 1);
                        SQLiteDatabase BaseDeDato = adminSQLite.getWritableDatabase();

                        JSONObject datosArray = jsonObject.getJSONObject("director");

                        String nombre = datosArray.getString("nombre");
                        String codigo = datosArray.getString("codigo");
                        String foto = datosArray.getString("foto");
                        ArrayList<String> valores = new ArrayList<>();
                        valores.add(codigo);
                        valores.add(nombre);
                        valores.add(foto);
                        adminSQLite.saveDirector(valores);

                        Globals.user = new User(codigo,nombre,foto, User.UserType.Director);

                        JSONArray datosArrayfact_pend = jsonObject.getJSONArray("colegios");
                        //BaseDeDato.execSQL("delete from alumno");
                        for (int i = 0; i < datosArrayfact_pend.length(); i++) {
                            ContentValues Registros1 = new ContentValues();
                            ContentValues Registros2 = new ContentValues();
                            JSONObject jsonObjectClientes = datosArrayfact_pend.getJSONObject(i);
                            Registros1.put("cod_col", jsonObjectClientes.getInt("cod_col"));
                            listacolegios.add(new Colegio(jsonObjectClientes.getString("cod_col"),"",jsonObjectClientes.getString("ip"),""));
                            Registros1.put("nombre", jsonObjectClientes.getString("nombre"));
                            Registros1.put("ip", jsonObjectClientes.getString("ip"));
                            Registros1.put("turno", jsonObjectClientes.getString("turno"));
                            if (existeCol(jsonObjectClientes.getInt("cod_col"))) { // verifico si existe el alumno, para no grabarlo mas de una vez
                                BaseDeDato.insert("colegios", null, Registros1);
                            }
                            Registros2.put("cod_dir", jsonObjectClientes.getInt("cod_dir"));
                            Registros2.put("cod_col", jsonObjectClientes.getInt("cod_col"));
                            Registros2.put("estado", 1);
                            BaseDeDato.insert("dir_col", null, Registros2);
                        }
                        obtenerCursos(listacolegios);
                        builder.setMessage("Se habilitó exitosamente...");
                        builder.setPositiveButton("Aceptar", (dialog, which) -> {
                            redirectToMainActivity();
                        });
                    } else {
                        builder.setMessage("El usuario no existe...");
                        builder.setPositiveButton("Aceptar", (dialog, which) -> {

                        });
                    }
                    builder.show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Error al procesar los datos...",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }, error -> {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error en la red...",Toast.LENGTH_SHORT).show();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("codigo", ed_codigo.getText().toString());
                    params.put("clave", ed_clave.getText().toString());
                    params.put("token", firebase.getToken());
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getApplicationContext()).addToRequest(stringRequest);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("El usuario ya se encuentra habilitado en este dispositivo...");
            builder.setPositiveButton("Aceptar", (dialog, which) -> {

            });
            builder.show();
        }
    }

    private void obtenerCursos(ArrayList<Colegio> listacolegios) {
        for (int i = 0; i < listacolegios.size(); i++){
            final String codCol = listacolegios.get(i).getCodigo();
            String ip = listacolegios.get(i).getIp();
            final AdminSQLite adm1 = new AdminSQLite(getApplicationContext(),"agenda",null,1);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://"+ip + "/agendadigital/getCursosDir.php", response -> {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")){
                        JSONArray cursos = jsonObject.getJSONArray("cursos");
                        for (int i1 = 0; i1 < cursos.length(); i1++){
                            JSONArray fila = cursos.getJSONArray(i1);
                            String codcur = fila.getString(0);
                            String codPar = fila.getString(1);
                            String nombre = fila.getString(2);
                            adm1.savecursosDir(Globals.user.getCodigo(),codCol,codcur,codPar,nombre);
                        }
                        JSONArray listas = jsonObject.getJSONArray("listas");
                        for (int i1 = 0; i1 < listas.length(); i1++){
                            JSONArray lista = listas.getJSONArray(i1);
                            for (int j = 0; j < lista.length(); j++){
                                JSONArray fila = lista.getJSONArray(j);
                                if (fila.length()>0) {
                                    String codCur = fila.getString(0);
                                    String codPar = fila.getString(1);
                                    String codest = fila.getString(2);
                                    String nombre = fila.getString(3);
                                    adm1.saveListaAlumnoDir(Globals.user.getCodigo(), codCol, codCur, codPar, codest, nombre);
                                }
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getApplicationContext()).addToRequest(stringRequest);
        }
    }

    private boolean existe_docente() {
        AdminSQLite adm = new AdminSQLite(getApplicationContext(),"agenda",null,1);
        Cursor cursor = adm.profesor(ed_codigo.getText().toString());
        return !cursor.moveToFirst();
    }

    private void habilitar_docente() {
        if (existe_docente()) {
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Validando...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsGlobals.url + "/habilitar.php?op=profesor", response -> {
                progressDialog.dismiss();
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")) {

                        ArrayList<String[]> lista_col=new ArrayList<>();

                        AdminSQLite adminSQLite = new AdminSQLite(getApplicationContext(), "agenda", null, 1);
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
                            if (existeCol(jsonObjectClientes.getInt("cod_col"))) { // verifico si existe el alumno, para no grabarlo mas de una vez
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
                        builder.setPositiveButton("Aceptar", (dialog, which) -> {
                             redirectToMainActivity();
                        });
                    } else {
                        builder.setMessage("El usuario no existe...");
                        builder.setPositiveButton("Aceptar", (dialog, which) -> {

                        });
                    }
                    builder.show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Error al procesar los datos...",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }, error -> {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error en la red...",Toast.LENGTH_SHORT).show();
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> header = new HashMap<>();
                    header.put("codigo",ed_codigo.getText().toString());
                    header.put("clave",ed_clave.getText().toString());
                    header.put("token", firebase.getToken());
                    return header;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getApplicationContext()).addToRequest(stringRequest);

        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("El usuario ya se encuentra habilitado en este dispositivo...");
            builder.setPositiveButton("Aceptar", (dialog, which) -> {

            });
            builder.show();
        }
    }

    private boolean existeCol(int cod_col) {
        AdminSQLite adm = new AdminSQLite(getApplicationContext(),"agenda",null,1);
        Cursor cursor = adm.verif_col(cod_col);
        return !cursor.moveToFirst();
    }

    private void obt_cursos_listas(final String codi_profe, final ArrayList<String[]> lista_cole){
        for (int j = 0; j < lista_cole.size(); j++) {
            String d_ip= lista_cole.get(j)[1];
            final String codCol = lista_cole.get(j)[0];
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+d_ip+ "/agendadigital/obt_cur_mat.php", response -> {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")) {
                        JSONArray cursos = jsonObject.getJSONArray("cursos");
                        AdminSQLite adm = new AdminSQLite(getApplicationContext(), "agenda", null, 1);
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
                            for (int j1 = 0; j1 < fila.length(); j1++){
                                JSONArray alumno = fila.getJSONArray(j1);
                                String codAlu = alumno.getString(0);
                                String nombre = alumno.getString(1);
                                adm.saveListaAlumno(codi_profe,codCol,codigoCursos.get(i)[0],codigoCursos.get(i)[1],codAlu,nombre);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {

            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("codigo", codi_profe);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getApplicationContext()).addToRequest(stringRequest);
        }
    }

    private boolean existe_administrador() {
        AdminSQLite adm = new AdminSQLite(getApplicationContext(),"agenda",null,1);
        Cursor cursor = adm.profesor(ed_codigo.getText().toString());
        return !cursor.moveToFirst();
    }

    private void habilitar_administrador() {
        if (existe_administrador()) {
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Validando...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsGlobals.url + "/habilitar.php?op=adm", response -> {
                progressDialog.dismiss();
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")) {
                        AdminSQLite adminSQLite = new AdminSQLite(getApplicationContext(), "agenda", null, 1);

                        JSONObject adm1 = jsonObject.getJSONObject("adm");
                        String codigo = adm1.getString("codigo");
                        String nombre = adm1.getString("nombre");
                        String foto = adm1.getString("foto");

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
                        builder.setPositiveButton("Aceptar", (dialog, which) -> {
                            redirectToMainActivity();
                        });
                    } else {
                        builder.setMessage("El usuario no existe...");
                        builder.setPositiveButton("Aceptar", (dialog, which) -> {

                        });
                    }
                    builder.show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Error al procesar los datos...",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }, error -> {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error en la red...",Toast.LENGTH_SHORT).show();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("codigo", ed_codigo.getText().toString());
                    params.put("clave", ed_clave.getText().toString());
                    params.put("token", firebase.getToken());
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getApplicationContext()).addToRequest(stringRequest);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("El usuario ya se encuentra habilitado en este dispositivo...");
            builder.setPositiveButton("Aceptar", (dialog, which) -> {

            });
            builder.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adm = new AdminSQLite(getApplicationContext(),"agenda",null, 1 );
        Usuarios usuarios = new Usuarios(getApplicationContext());
        if(usuarios.getUsuarios().size()>0){
            adm.userActivo(usuarios.getUsuarios().get(0).getCodigo(), usuarios.getUsuarios().get(0).getTipo());
            Intent filter = new Intent("restarSockets");
            getApplication().sendBroadcast(filter);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("restartService","1");
            Toast.makeText(getApplicationContext(),"Iniciando...",Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }



    private void habilitar_alumno() {
        if(existe_Alumn()){
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Validando...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsGlobals.url + "/habilitar.php?op=est", response -> {
                progressDialog.dismiss();
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")) {

                        AdminSQLite adminSQLite = new AdminSQLite(getApplicationContext(), "agenda", null, 1);



                        String codigo = jsonObject.getString("codigo");
                        String nombre = jsonObject.getString("nombre");
                        String curso = jsonObject.getString("curso");
                        String cod_curso = jsonObject.getString("cod_cur");
                        String cod_par = jsonObject.getString("cod_par");
                        String colegio = jsonObject.getString("colegio");
                        String ip = jsonObject.getString("ip");
                        String cod_col = jsonObject.getString("cod_col");
                        String foto = jsonObject.getString("foto");
                        String nivel = jsonObject.getString("nivel");

                        ArrayList<String> valores = new ArrayList<>();
                        valores.add(codigo);
                        valores.add(nombre);
                        valores.add(curso);
                        valores.add(cod_curso);
                        valores.add(cod_par);
                        valores.add(colegio);
                        valores.add(ip);
                        valores.add(cod_col);
                        valores.add(foto);
                        valores.add(nivel);


                        adminSQLite.saveEstudiante(valores);
                        Globals.user = new User(codigo,nombre,foto, User.UserType.Student);

                        builder.setMessage("Se habilitó exitosamente...");
                        builder.setPositiveButton("Aceptar", (dialog, which) -> {

                        });
                    } else {
                        builder.setMessage("El usuario no existe...");
                        builder.setPositiveButton("Aceptar", (dialog, which) -> {
                            redirectToMainActivity();


                        });
                    }
                    builder.show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Error al procesar los datos...",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }, error -> {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error en la red...",Toast.LENGTH_SHORT).show();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("codigo", ed_codigo.getText().toString());
                    params.put("clave", ed_clave.getText().toString());
                    params.put("token", firebase.getToken());
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getApplicationContext()).addToRequest(stringRequest);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("El usuario ya se encuentra habilitado en este dispositivo...");
            builder.setPositiveButton("Aceptar", (dialog, which) -> {
                Usuarios u = new Usuarios(getApplicationContext());
                adm.userActivo(u.getUsuarios().get(0).getCodigo(),u.getUsuarios().get(0).getTipo());
                Intent filter = new Intent("restarSockets");
                getApplication().sendBroadcast(filter);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("restartService","1");
                startActivity(intent);
            });
            builder.show();
        }
    }

    private boolean existe_Alumn() {
        AdminSQLite adm = new AdminSQLite(getApplicationContext(),"agenda",null,1);
        Cursor cursor = adm.estudiante(ed_codigo.getText().toString());
        return !cursor.moveToFirst();
    }

    private boolean habilitar_tutor() {
        if (existe()) {
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Validando...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsGlobals.url+ "/habilitar.php?op=tutor", response -> {
                progressDialog.dismiss();
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")) {
                        AdminSQLite adminSQLite = new AdminSQLite(getApplicationContext(), "agenda", null, 1);
                        SQLiteDatabase BaseDeDato = adminSQLite.getWritableDatabase();

                        JSONObject datosArray = jsonObject.getJSONObject("tutor");

                        String nombre = datosArray.getString("nombre");
                        String codigo = datosArray.getString("codigo");
                        String fotot = datosArray.getString("foto");
                        ArrayList<String> valores = new ArrayList<>();
                        valores.add(codigo);
                        valores.add(nombre);
                        valores.add(fotot);//Agreagar aqui la foto desde el JSON
                        valores.add(ed_codigo.getText().toString());
                        valores.add(ed_clave.getText().toString());
                        adminSQLite.saveTutor(valores);

                        Globals.user = new User(codigo, nombre, fotot, User.UserType.Tutor);

                        JSONArray datosArrayfact_pend = jsonObject.getJSONArray("alumnos");
                        //BaseDeDato.execSQL("delete from alumno");

                        ArrayList<String[]> codAlumnos = new ArrayList<>();
                        for (int i = 0; i < datosArrayfact_pend.length(); i++) {
                            ContentValues Registros1 = new ContentValues();
                            ContentValues Registros2 = new ContentValues();
                            JSONObject jsonObjectClientes = datosArrayfact_pend.getJSONObject(i);
                            Registros1.put("codigo", jsonObjectClientes.getInt("codigo"));
                            codAlumnos.add(new String[]{jsonObjectClientes.getString("codigo"), jsonObjectClientes.getString("ip")});
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

                        obtenerMaterias(codAlumnos,codigo);
                        builder.setMessage("Se habilitó exitosamente...");
                        builder.setPositiveButton("Aceptar", (dialog, which) -> {
                        redirectToMainActivity();
                        });
                    } else {
                        builder.setMessage("El usuario NO existe...");
                        builder.setPositiveButton("Aceptar", (dialog, which) -> {

                        });
                    }
                    builder.show();

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error al procesar los datos...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }, error -> {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error en la red...",Toast.LENGTH_SHORT).show();
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("cedula", ed_codigo.getText().toString());
                    params.put("telefono", ed_clave.getText().toString());
                    params.put("token", firebase.getToken());
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getApplicationContext()).addToRequest(stringRequest);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("El usuario ya se encuentra habilitado en este dispositivo...");
            builder.setPositiveButton("Aceptar", (dialog, which) -> {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);

            });
            builder.show();
            return true;
        }
        return true;
    }

    private boolean existealu(int cod_alu) {
        AdminSQLite adm = new AdminSQLite(getApplicationContext(),"agenda",null,1);
        Cursor cursor = adm.verif_alu(cod_alu);
        return cursor.moveToFirst();
    }

    private boolean existe() {
        AdminSQLite adm = new AdminSQLite(getApplicationContext(),"agenda",null,1);
        Cursor cursor = adm.tutor(ed_codigo.getText().toString(),ed_clave.getText().toString());
        return !cursor.moveToFirst();
    }

    private void cleanForm() {
        ed_clave.setText("");
        ed_codigo.setText("");
    }

    private void visivility_btn_sesion(int view) {
        btn_tutor.setVisibility(view);
        btn_docente.setVisibility(view);
        btn_alumno.setVisibility(view);
        btn_administracion.setVisibility(view);
    }

    private void obtenerMaterias(ArrayList<String[]> codAlumnos, final String codigo) {
        for (int i = 0; i < codAlumnos.size(); i++) {
            final String codAlu = codAlumnos.get(i)[0];
            String url = codAlumnos.get(i)[1];
            final AdminSQLite adm = new AdminSQLite(getApplicationContext(), "agenda", null,1);
            adm.saveMaterias(codAlu,"director","Director");
            adm.saveMaterias(codAlu,"administracion","Administración");
            adm.saveMaterias(codAlu,"caja","Caja");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+url+ "/agendadigital/getmaterias.php", response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")){
                        JSONArray materias = jsonObject.getJSONArray("materias");

                        for (int i1 = 0; i1 < materias.length(); i1++){
                            JSONArray fila = materias.getJSONArray(i1);
                            String codmat = fila.getString(0);
                            String nombre = fila.getString(1);
                            adm.saveMaterias(codAlu,codmat,nombre);
                        }
                        JSONArray mensajes = jsonObject.getJSONArray("mensajes");
                        for (int i1 = 0; i1 < mensajes.length(); i1++){
                            JSONArray fila = mensajes.getJSONArray(i1);
                            String id = fila.getString(0);
                            String codest = fila.getString(1);
                            String msg = fila.getString(2);
                            String emisor = fila.getString(3);
                            String codtut = fila.getString(4);
                            String fecha = fila.getString(5);
                            String hora = fila.getString(6);
                            String estado = fila.getString(7);
                            String tipo = fila.getString(8);
                            String nombre = fila.getString(9);
                            adm.savemsgAnterior(id,codest,msg,emisor,codtut,fecha,hora,estado,tipo,nombre);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {

            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("codigo", codAlu);
                    params.put("codtut", codigo);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getApplicationContext()).addToRequest(stringRequest);
        }

    }

    private void showLoginForm(User.UserType userType) {
        switch (userType) {
            case Tutor:
                tv_title.setText(R.string.tutor);
                ed_codigo.setHint(R.string.cedula);
                ed_codigo.setInputType(InputType.TYPE_CLASS_NUMBER);
                ed_clave.setHint(R.string.celular);
                ed_clave.setInputType(InputType.TYPE_CLASS_NUMBER);
                visivility_form(View.VISIBLE);
                break;
            case Teacher:
                tv_title.setText(R.string.docente);
                ed_codigo.setHint(R.string.cod_doc);
                ed_codigo.setInputType(InputType.TYPE_CLASS_TEXT);
                ed_clave.setHint(R.string.clave_a);
                ed_clave.setInputType(InputType.TYPE_CLASS_NUMBER);
                visivility_form(View.VISIBLE);
                break;
            case Student:
                tv_title.setText(R.string.alumno);
                ed_codigo.setHint(R.string.usuario);
                ed_codigo.setInputType(InputType.TYPE_CLASS_TEXT);
                ed_clave.setHint(R.string.clave_a);
                ed_clave.setInputType(InputType.TYPE_CLASS_NUMBER);
                visivility_form(View.VISIBLE);
                break;
            case Director:
            case Staff:
                tv_title.setText(R.string.administrador);
                ed_codigo.setHint(R.string.codigo_a);
                ed_codigo.setInputType(InputType.TYPE_CLASS_NUMBER);
                ed_clave.setHint(R.string.clave_a);
                ed_clave.setInputType(InputType.TYPE_CLASS_NUMBER);
                visivility_form(View.VISIBLE);
                break;
        }
    }

    private void redirectToMainActivity(){
        UserDto.LoginUserRequest loginUserRequest = new UserDto.LoginUserRequest(Globals.user.getCodigo(), Globals.user.getTipo().getValue(), firebase.getToken());
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
            MySingleton.getInstance(getApplicationContext()).addToRequest(jsonObjectRequest);
        }catch (Exception e) {
            Log.d(TAG, "Error: " + e.getMessage());
        }
        Usuarios u = new Usuarios(getApplicationContext());
        adm.userActivo(u.getUsuarios().get(0).getCodigo(),u.getUsuarios().get(0).getTipo());
        Intent filter = new Intent("restarSockets");
        getApplication().sendBroadcast(filter);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("restartService","1");
        startActivity(intent);
    }

    private void visivility_form(int visible) {
        ed_clave.setVisibility(visible);
        ed_codigo.setVisibility(visible);
        btn_cancelar_sesion.setVisibility(visible);
        btn_iniciar_sesion.setVisibility(visible);
    }
}