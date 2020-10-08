package com.agendadigital.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.itextpdf.text.pdf.codec.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FragmentIngreso extends Fragment {
    private Button btnCancelar;
    private Button btnHabilitar;
    private Button btnBuscar;
    private EditText codigo;
    private String cod_prof,clave_prof,cod_cur,cod_par,dia_s,hora_ing,hora_sal,d_ip;
    Bitmap fotobm;
    private ImageView ivfoto;
    private TextView alumno,atraso,horario,hoy;
    private Spinner tipo,agenda;
    private int anio,mes,dia,tipo_hor,agenda_hoy;

    Calendar c = Calendar.getInstance();
    DatePickerDialog dpd;
    String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    String date2 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    //Date f_hoy = new SimpleDateFormat("dd-MM-yyyy").parse(date);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    String h_actual = simpleDateFormat.format(new Date());



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View vista = inflater.inflate(R.layout.fragment_ingreso, container, false);
       hacerCast(vista);
        mostrarfechaAct();

        oncliks();
        return vista;
    }

    private void oncliks() {
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_home);
            }
        });
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anio=c.get(Calendar.YEAR);
                mes=c.get(Calendar.MONTH);
                dia=c.get(Calendar.DAY_OF_MONTH);
                dia_s=diaSemana(dia,mes,anio);
                Buscar(v);
            }
        });
        btnHabilitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Grabar(v);
            }
        });
    }
    public void mostrarfechaAct(){
        hoy.setText("Fecha actual: "+date);
    }


    private void Grabar(final View v) {
        if (validarDatos()){
            d_ip= Globals.colegio.getIp();

            h_actual = simpleDateFormat.format(new Date());

            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Validando...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST,"http://"+d_ip + "/agendadigital/grab_ingreso_alu.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        if (status.equals("ok")) {

                            builder.setMessage("Grabado con exito!");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   // Navigation.findNavController(v).navigate(R.id.nav_home);
                                    envMensaje(cod_prof,"...", Globals.user.getCodigo());
                                }
                            });
                        } else {
                            builder.setMessage("NO GRABO el ingreso");
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
                    params.put("codigo", cod_prof);
                    params.put("fecha", date2);
                    params.put("hora", h_actual);
                    params.put("tipo", "I");
                    params.put("hora_ing", hora_ing);
                    params.put("hora_sal", hora_sal);
                    params.put("usr", Globals.user.getCodigo());
                    params.put("agenda", agenda_hoy+"");
                    params.put("nube", "1");
                    return params;

                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getContext()).addToRequest(stringRequest);


        }else{
            Toast.makeText(getContext(),"Faltan datos...",Toast.LENGTH_SHORT).show();
        }
    }

    private void envMensaje(final String codigo, String msg,final String cod_emi) {
        if (agenda_hoy == 1) {
            msg = "Hora de entrada: " + h_actual + " SI trajo agenda. Hoy sale a: " + hora_sal;
        }else{
            msg = "Hora de entrada: " + h_actual + " NO TRAJO AGENDA. Hoy sale a: " + hora_sal;
        }

        d_ip= Globals.colegio.getIp();
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("codEst",cod_prof);
            jsonObject.put("codEmit", Globals.user.getCodigo());
            jsonObject.put("msg",msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+d_ip+"/agenda/mensaje", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonObject==null? null : jsonObject.toString().getBytes();
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(stringRequest);
    }


    String diaSemana (int dia, int mes, int ano)
    {
        String letraD="";
        /*Calendar c = Calendar.getInstance();
        c.set(ano, mes, dia, 0, 0, 0);
        nD=c.get(Calendar.DAY_OF_WEEK);*/
        TimeZone timezone = TimeZone.getDefault();
        Calendar calendar = new GregorianCalendar(timezone);
        calendar.set(ano, mes, dia);
        int nD=calendar.get(Calendar.DAY_OF_WEEK);
        Log.i("result","diaSemana: "+nD+" dia:"+dia+" mes:"+mes+ "año:" +ano);
        switch (nD){
            case 1: letraD = "D";
                break;
            case 2: letraD = "L";
                break;
            case 3: letraD = "M";
                break;
            case 4: letraD = "X";
                break;
            case 5: letraD = "J";
                break;
            case 6: letraD = "V";
                break;
            case 7: letraD = "S";
                break;
        }

        return letraD;
    }
    private void Buscar(final View v) {
        if (validarDatos()){
            d_ip= Globals.colegio.getIp();
            h_actual = simpleDateFormat.format(new Date());

            final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Validando...");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(false);
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST,"http://"+d_ip + "/agendadigital/get_dat_alumno.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("ok")) {
                                String nombre2 = jsonObject.getString("nombre");
                                String foto = jsonObject.getString("foto");
                                cod_cur=jsonObject.getString("cod_cur");
                                cod_par=jsonObject.getString("cod_par");
                                hora_ing=jsonObject.getString("hora_ing");
                                hora_sal=jsonObject.getString("hora_sal");

                                alumno.setText(jsonObject.getString("nombre"));
                                horario.setText("Hora: "+h_actual+" Entrada: "+hora_ing+" Salida: "+hora_sal);
                                String base64 = foto.split(",")[1];
                                byte[] decode = Base64.decode(base64);
                                fotobm = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                                ivfoto.setImageBitmap(fotobm);
                            } else {
                                builder.setMessage("El usuario no existe...");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
                            }
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
                        params.put("codigo", cod_prof);
                        params.put("dia_s", dia_s);
                        params.put("tipo", tipo_hor+"");
                        return params;
                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                MySingleton.getInstance(getContext()).addToRequest(stringRequest);


        }else{
            Toast.makeText(getContext(),"Faltan datos...",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean existe() {
        AdminSQLite adm = new AdminSQLite(getContext(),"agenda",null,1);
        Cursor cursor = adm.profesor(cod_prof);
        return cursor.moveToFirst();
    }

    private boolean validarDatos() {
        cod_prof = codigo.getText().toString();
        return !cod_prof.isEmpty();
    }

    private void hacerCast(View vista) {
        btnCancelar = vista.findViewById(R.id.btnCancelarformprofesor);
        btnHabilitar = vista.findViewById(R.id.btnHabilitarformprofesor);
        btnBuscar = vista.findViewById(R.id.btnBuscarA);
        codigo = vista.findViewById(R.id.etcodigoformprofesor);
        alumno = vista.findViewById(R.id.tvAlumno);
        horario = vista.findViewById(R.id.tvHorario);
        atraso = vista.findViewById(R.id.tvatraso);
        ivfoto = vista.findViewById(R.id.ivFoto);
        hoy=vista.findViewById(R.id.tvfecha);
        tipo = vista.findViewById(R.id.spSelector1);
        String[] opciones = new String[]{"NORMAL","INVIERNO"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner,opciones);
        tipo.setAdapter(adapter);
        tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onitemSelected(position,view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        agenda = vista.findViewById(R.id.spSelector2);
        String[] opciones2 = new String[]{"SI","NO"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getContext(), R.layout.item_spinner,opciones2);
        agenda.setAdapter(adapter2);
        agenda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onitemSelected2(position,view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void onitemSelected2(int position,View view) {
        switch (position){
            case 0:
                agenda_hoy=1;
                break;
            case 1:
                agenda_hoy=2;
                break;
        }
    }
    private void onitemSelected(int position,View view) {
        switch (position){
            case 0:
                tipo_hor=1;
                break;
            case 1:
                tipo_hor=2;
                break;
        }
    }

}