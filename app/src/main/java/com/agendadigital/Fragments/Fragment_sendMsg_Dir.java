package com.agendadigital.Fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agendadigital.R;
import com.agendadigital.clases.AdapterNotificacionesDir;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Alumno;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.clases.NoticationType;
import com.agendadigital.clases.NotificacionDir;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Fragment_sendMsg_Dir extends Fragment {
    private TextView nombreCurso;
    private Spinner selectAlumno;
    private RecyclerView rvMsg;
    private EditText etMsg;
    private Button btnSendMsg;
    private ArrayList<Alumno> lista;
    private AdminSQLite adm;
    private AdapterNotificacionesDir adapterNotificacionesDir;
    public Fragment_sendMsg_Dir() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_msg__dir, container, false);
        adm = new AdminSQLite(getContext(),"agenda",null,1);
        hacerCast(view);
        nombreCurso.setText(Globals.curso.getNombre());
        obtenerListaAlumnos();
        cargarRecycler();
        onCliks();
        return view;
    }

    private void cargarRecycler() {
        Cursor cursor = adm.getNotificacionesDirCurso(Globals.user.getCodigo(),Globals.colegio.getCodigo(),
                Globals.curso.getCod_curso(),Globals.curso.getCod_par());
        ArrayList<NotificacionDir> notificaciones = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                notificaciones.add(new NotificacionDir(cursor.getString(3),cursor.getString(4),
                        cursor.getString(5),cursor.getInt(6)));
            }while (cursor.moveToNext());
        }
        adapterNotificacionesDir = new AdapterNotificacionesDir(notificaciones);
        rvMsg.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMsg.setAdapter(adapterNotificacionesDir);
        rvMsg.scrollToPosition(notificaciones.size()-1);
    }

    private void obtenerListaAlumnos() {
        Cursor cursor = adm.getAlumnosDir(Globals.user.getCodigo(),Globals.colegio.getCodigo(),Globals.curso.getCod_curso(),
                Globals.curso.getCod_par());
        ArrayList<String> nombres = new ArrayList<>();
        lista = new ArrayList<>();
        nombres.add("Enviar mensaje a todos");
        if (cursor.moveToFirst()){
            do {
                lista.add(new Alumno(cursor.getString(2),cursor.getString(3),
                        cursor.getString(4),cursor.getString(5)));
                nombres.add("Enviar mensaje a: "+cursor.getString(5));
            }while (cursor.moveToNext());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, nombres);
        selectAlumno.setAdapter(adapter);
    }
    private void onCliks(){
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etMsg.getText().toString().isEmpty()){
                    enviarMSG();
                }
            }
        });
        selectAlumno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<NotificacionDir> notificacionDirs = new ArrayList<>();
                if (position>0){
                    Cursor cursor = adm.getNotificacionesDirAlumno(Globals.user.getCodigo(),lista.get(position-1).getCod_alu());
                    if (cursor.moveToFirst()){
                        do {
                            notificacionDirs.add(new NotificacionDir(cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getInt(6)));
                        }while (cursor.moveToNext());
                    }
                }else{
                    Cursor cursor = adm.getNotificacionesDirCurso(Globals.user.getCodigo(),Globals.colegio.getCodigo(),Globals.curso.getCod_curso(),Globals.curso.getCod_par());
                    if (cursor.moveToFirst()){
                        do {
                            notificacionDirs.add(new NotificacionDir(cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getInt(8)));
                        }while (cursor.moveToNext());
                    }
                }

                adapterNotificacionesDir = new AdapterNotificacionesDir(notificacionDirs);
                rvMsg.setAdapter(adapterNotificacionesDir);
                rvMsg.scrollToPosition(notificacionDirs.size()-1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void enviarMSG() {
        if (!etMsg.getText().toString().trim().isEmpty()){
            final JSONObject jsonObject = new JSONObject();
            String url = "";
            try {
                if (selectAlumno.getSelectedItemPosition()>0){
                    jsonObject.put("codEst",lista.get(selectAlumno.getSelectedItemPosition()-1).getCod_alu());
                    url = "http://"+Globals.colegio.getIp()+"/agenda/mensajeDirAlumno";
                }else{
                    jsonObject.put("codCur",Globals.curso.getCod_curso());
                    jsonObject.put("codPar",Globals.curso.getCod_par());
                    url = "http://"+Globals.colegio.getIp()+"/agenda/mensajeDirCurso";
                }
                jsonObject.put("nombre",Globals.user.getNombre());
                jsonObject.put("codEmit", "director");
                jsonObject.put("msg",etMsg.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("ok")){
                        @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                        String hora = simpleDateFormat.format(new Date());
                        if (selectAlumno.getSelectedItemPosition()>0){

                            adm.saveNotificacionDirAlumno(Globals.user.getCodigo(),lista.get(selectAlumno.getSelectedItemPosition()-1).getCod_alu()
                                    ,etMsg.getText().toString(), date,hora, NoticationType.TEXT);
                        }else{
                            adm.saveNotificacionDirCurso(Globals.user.getCodigo(),Globals.colegio.getCodigo(),Globals.curso.getCod_curso()
                                    ,Globals.curso.getCod_par(),etMsg.getText().toString(),date,hora,NoticationType.TEXT);
                        }
                        adapterNotificacionesDir.add(new NotificacionDir(etMsg.getText().toString(),date,hora,NoticationType.TEXT));
                        rvMsg.scrollToPosition(adapterNotificacionesDir.getItemCount()-1);
                        etMsg.setText("");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    return jsonObject.toString().getBytes();
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getContext()).addToRequest(stringRequest);
        }
    }

    private void hacerCast(View view) {
        nombreCurso = view.findViewById(R.id.tvNameCurso);
        selectAlumno = view.findViewById(R.id.spSelectAlumno);
        rvMsg = view.findViewById(R.id.rvContentMsg);
        etMsg = view.findViewById(R.id.etMsgToSend);
        btnSendMsg = view.findViewById(R.id.btnSendMsg);
    }
}