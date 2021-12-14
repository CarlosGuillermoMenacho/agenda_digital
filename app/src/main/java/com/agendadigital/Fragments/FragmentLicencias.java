package com.agendadigital.Fragments;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.agendadigital.R;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.Licencia;
import com.agendadigital.clases.Licencias;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.clases.Usuarios;
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

import static com.agendadigital.R.id;
import static com.agendadigital.R.layout;

public class FragmentLicencias extends Fragment {
    private ArrayList<String[]> codigos;
    private Button btnNew;
    private Button btnDelete;
    private Button btnListo;
    private TextView title;
    private ListView lista;
    private boolean delete = false;
    private AdminSQLite adm;
    private Usuarios usuarios;
    private Licencias licencias;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(layout.fragment_licencias,container,false);
        adm = new AdminSQLite(getContext(),"agenda",null, 1 );
        buscarLicencias(vista,Globals.user.getCodigo(),Globals.estudiante.getCodigo());
        enlaces(vista);
        title.setText(Globals.estudiante.getNombre());
        llenarListas();
        onclick();
        return vista;
    }
    private void onclick() {
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.fragmentFormLicencia);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"SetTextI18n"})
            @Override
            public void onClick(View v) {
                /*btnListo.setVisibility(View.VISIBLE);*/
                btnDelete.setVisibility(View.GONE);
                btnNew.setVisibility(View.GONE);
                title.setText("Anular Licencia");
                delete = true;
            }
        });
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                if (delete){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("¿Desea anular esta licencia?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (codigos.get(position)[2].equals("ACTIVO")) {  // el estado de la licencia es Activo
                                if (codigos.get(position)[3].equals(Globals.user.getCodigo())) { // verifica que sea el usuario tutor
                                    anularItem(position);
                                    llenarListas();
                                }else{
                                    AlertDialog.Builder tutor_req = new AlertDialog.Builder(getContext());
                                    tutor_req.setMessage("Solo anula el Tutor que pidió la licencia");
                                    tutor_req.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            llenarListas();
                                        }
                                    });
                                    tutor_req.show();
                                }
                            }else{
                                AlertDialog.Builder verif = new AlertDialog.Builder(getContext());
                                verif.setMessage("NO se puede anular porque no es una licencia ACTIVA!");
                                verif.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        llenarListas();
                                    }
                                });
                                verif.show();
                            }
                        }
                    });
                    builder.setNegativeButton("No",null);
                    builder.show();
                    btnDelete.setVisibility(View.VISIBLE);
                    btnNew.setVisibility(View.VISIBLE);
                    delete = false;
                    title.setText(Globals.estudiante.getNombre());
                }
            }
        });
        btnListo.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"SetTextI18n", "ResourceAsColor"})
            @Override
            public void onClick(View v) {
                title.setText("Cambiar Cuenta");
                btnDelete.setVisibility(View.VISIBLE);
                btnNew.setVisibility(View.VISIBLE);
                btnListo.setVisibility(View.GONE);
                delete = false;
            }
        });
    }

    private void anularItem(final int position) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Anulando la Licencia...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.url + "/grab_anu_licencia.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")) {
                        builder.setMessage("ANULADO con exito.");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Navigation.findNavController(v).navigate(R.id.fragmentLicencias);
                                adm.anularLicencia(Integer.parseInt(codigos.get(position)[1]));  // anula en la base de datos del celular
                                llenarListas();
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
                params.put("codigo", codigos.get(position)[1]);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(stringRequest);

    }

    private void deleteItem(int position) {

        if (codigos.get(position)[1].equals("tutor")){
            adm.deleteTutor(codigos.get(position)[0]);
        }

    }

    private void enlaces(View vista) {
        btnNew = vista.findViewById(id.btnNewUser);
        btnDelete = vista.findViewById(id.btnDeleteUser);
        btnListo = vista.findViewById(id.btnListoafiliacion);
        title = vista.findViewById(id.text_home);
        lista = vista.findViewById(id.lvAfiliados);
    }

    private void llenarListas() {
        licencias = new Licencias(getContext());
        codigos = new ArrayList<>();
        ArrayList<String> nombres = new ArrayList<>();

        ArrayList<Licencia> arraUser = new ArrayList<>(licencias.getLicencias());

        for (int i = 0 ; i < arraUser.size(); i++){
            codigos.add(new String[]{arraUser.get(i).getCodigo(),arraUser.get(i).getId(),arraUser.get(i).getEstado(),arraUser.get(i).getCod_tut()});
            nombres.add("Inicio:"+arraUser.get(i).getF_ini()+" Fin:"+arraUser.get(i).getF_fin()+" Motivo:"+arraUser.get(i).getObse()+" Solicitado por:"+arraUser.get(i).getCod_tut()+":"+arraUser.get(i).getNombre()+" Fecha y hora:"+arraUser.get(i).getF_sol()+" / "+arraUser.get(i).getH_sol()+" Estado: "+arraUser.get(i).getEstado());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, nombres);
        lista.setAdapter(adapter);
    }
    private void buscarLicencias(final View v,final String cod_tut, final String cod_alu){
        StringRequest DominioLocal = new StringRequest(Request.Method.POST, "http://"+Globals.estudiante.getIp() + "/agendadigital/get_licencias.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")) {
                        AdminSQLite adminSQLite = new AdminSQLite(getContext(), "agenda", null, 1);
                        SQLiteDatabase BaseDeDato = adminSQLite.getWritableDatabase();

                        JSONArray datosArrayLicencias = jsonObject.getJSONArray("licencias");
                        for (int i = 0; i < datosArrayLicencias.length(); i++) {
                            ContentValues Registros1 = new ContentValues();
                            JSONObject jsonObjectClientes = datosArrayLicencias.getJSONObject(i);
                            Registros1.put("id", jsonObjectClientes.getInt("id"));
                            Registros1.put("codigo", jsonObjectClientes.getInt("codigo"));
                            Registros1.put("cod_tut", jsonObjectClientes.getInt("cod_tut"));
                            Registros1.put("f_sol", jsonObjectClientes.getString("f_solicitud"));
                            Registros1.put("h_sol", jsonObjectClientes.getString("h_solicitud"));
                            Registros1.put("f_ini", jsonObjectClientes.getString("f_ini"));
                            Registros1.put("f_fin", jsonObjectClientes.getString("f_fin"));
                            Registros1.put("obs", jsonObjectClientes.getString("obs"));
                            Registros1.put("estado", jsonObjectClientes.getInt("estado"));
                            if (!existeLicencia(jsonObjectClientes.getInt("id"))) { // verifico si existe el alumno, para no grabarlo mas de una vez
                                BaseDeDato.insert("licencias", null, Registros1);
                            }

                        }

                        llenarListas();
                    } else {
                        builder.setMessage("El usuario NO existe...");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error al procesar los datos...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error en la red...",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cod_tut", cod_tut);
                params.put("cod_alu", cod_alu);
                return params;
            }
        };
        DominioLocal.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(DominioLocal);
    }
    private boolean existeLicencia(int cod_lice) {
        AdminSQLite adm = new AdminSQLite(getContext(),"agenda",null,1);
        Cursor cursor = adm.verif_Lic(cod_lice);
        return cursor.moveToFirst();
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
