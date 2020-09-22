package com.agendadigital.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FragmentFormLicencia extends Fragment {
    private Button btnCancelar, btnHabilitar,btnDesde,btnHasta;
    private EditText etCedula,etTelefono;
    private TextView inicio,fin,hoy,obs;
    private String fini, ffin, dobs;
    private int a_anio,a_mes,a_dia,anio,mes,dia,aniof,mesf,diaf;
    static final int Date_ID =0;
    Calendar c = Calendar.getInstance();
    DatePickerDialog dpd;
    String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    Date f_hoy = new SimpleDateFormat("dd-MM-yyyy").parse(date);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    String h_actual = simpleDateFormat.format(new Date());
    public FragmentFormLicencia() throws ParseException {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_form_licencia, container, false);
        a_anio=c.get(Calendar.YEAR);
        a_mes=c.get(Calendar.MONTH);
        a_dia=c.get(Calendar.DAY_OF_MONTH);
        anio=c.get(Calendar.YEAR);
        mes=c.get(Calendar.MONTH);
        dia=c.get(Calendar.DAY_OF_MONTH);
        aniof=c.get(Calendar.YEAR);
        mesf=c.get(Calendar.MONTH);
        diaf=c.get(Calendar.DAY_OF_MONTH);
        hacerCast(vista);
        mostrarfechaAct();
        oncliks();


        return vista;
    }

    public void mostrarfechaAct(){
        hoy.setText("Fecha actual: "+date);
    }

    private void oncliks() {
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.fragmentLicencias);
            }
        });
        btnHabilitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                h_actual = simpleDateFormat.format(new Date());
                habilitar(v);
            }
        });
        btnDesde.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dpd= new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int nanio, int nmes, int ndia) {
                        anio=nanio;
                        mes=nmes+1;
                        dia=ndia;
                        aniof=nanio;
                        mesf=nmes+1;
                        diaf=ndia;
                        if (anio>=a_anio) {
                            if (mes>=a_mes) {
                                if(dia>=a_dia) {
                                    inicio.setText(dia + "/" + mes + "/" + anio);
                                    fin.setText(dia + "/" + mes + "/" + anio);
                                }else{
                                    Toast.makeText(getContext(), "día NO VALIDO", Toast.LENGTH_SHORT).show();
                                    inicio.setText("");
                                    fin.setText("");
                                    mes=mes-1;
                                    mesf=mesf-1;
                                }
                            }else{
                                Toast.makeText(getContext(), "mes NO VALIDO", Toast.LENGTH_SHORT).show();
                                inicio.setText("");
                                fin.setText("");
                                mes=mes-1;
                                mesf=mesf-1;
                            }
                        }else{
                            Toast.makeText(getContext(), "año NO VALIDO", Toast.LENGTH_SHORT).show();
                            inicio.setText("");
                            fin.setText("");
                            mes=mes-1;
                            mesf=mesf-1;
                        }
                    }
                },anio,mes,dia);
                dpd.show();
            }
        });
        btnHasta.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dpd= new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int nanio, int nmes, int ndia) {
                        aniof=nanio;
                        mesf=nmes+1;
                        diaf=ndia;
                        if (aniof>=anio) {
                            if (mesf>=mes) {
                                if(diaf>=dia) {
                                    fin.setText(diaf + "/" + mesf + "/" + aniof);
                                }else{
                                    Toast.makeText(getContext(), "día NO VALIDO", Toast.LENGTH_SHORT).show();
                                    fin.setText("");
                                    mesf=mesf-1;
                                }
                            }else{
                                Toast.makeText(getContext(), "mes NO VALIDO", Toast.LENGTH_SHORT).show();
                                fin.setText("");
                                mesf=mesf-1;
                            }
                        }else{
                            Toast.makeText(getContext(), "año NO VALIDO", Toast.LENGTH_SHORT).show();
                            fin.setText("");
                            mesf=mesf-1;
                        }

                    }
                },aniof,mesf-1,diaf);
                dpd.show();
            }
        });

    }

    private boolean validarDatos() {
        fini = inicio.getText().toString();
        ffin = fin.getText().toString();
        dobs = obs.getText().toString();
        return !fini.isEmpty() && !ffin.isEmpty() && !dobs.isEmpty();
    }
    private boolean existeLic() { //verifica si existe una licencia vigente ** solo puede haber una sola licencia vigente
        AdminSQLite adm = new AdminSQLite(getContext(),"agenda",null,1);
        Cursor cursor = adm.licencia(Integer.parseInt(Globals.estudiante.getCodigo()),f_hoy);
        return cursor.moveToFirst();
    }

    private void hacerCast(View vista) {
       btnCancelar  = vista.findViewById(R.id.btnCancelarformtutor);
        btnHabilitar = vista.findViewById(R.id.btnHabilitarformtutor);
        btnDesde = vista.findViewById(R.id.btndesde);
        btnHasta = vista.findViewById(R.id.btnhasta);
        etCedula = vista.findViewById(R.id.etCedulaformtutor);
        etTelefono = vista.findViewById(R.id.etTelefonoformtutor);
        inicio = vista.findViewById(R.id.tvinicio);
        fin=vista.findViewById(R.id.tvfin);
        hoy=vista.findViewById(R.id.tvfecha);
        obs = vista.findViewById(R.id.etObs);
     }

    private void habilitar(final View v) {
        if(validarDatos()){
            if (!existeLic()){
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Grabando...");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(false);
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.url + "/grab_licencia.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("ok")) {

                                Integer id = jsonObject.getInt("id");

                                AdminSQLite adminSQLite = new AdminSQLite(getContext(), "agenda", null, 1);

                                adminSQLite.saveLicencia(id,Integer.parseInt(Globals.estudiante.getCodigo()),Integer.parseInt(Globals.user.getCodigo()),date,h_actual,fini,ffin,dobs);

                                builder.setMessage("Grabado con exito.");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Navigation.findNavController(v).navigate(R.id.fragmentLicencias);
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
                        params.put("codigo", Globals.estudiante.getCodigo());
                        params.put("cod_tut", Globals.user.getCodigo());
                        params.put("f_sol", date);
                        params.put("hora", h_actual);
                        params.put("f_ini", fini);
                        params.put("f_fin", ffin);
                        params.put("obs", dobs);
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                MySingleton.getInstance(getContext()).addToRequest(stringRequest);
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("El alumno ya tiene una licencia vigente!");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Navigation.findNavController(v).navigate(R.id.fragmentLicencias);
                    }
                });
                builder.show();
            }
        }else{
            Toast.makeText(getContext(),"Faltan datos: Inicio, Fin y Motivo de la Licencia deben contener datos.",Toast.LENGTH_SHORT).show();
        }
    }
}