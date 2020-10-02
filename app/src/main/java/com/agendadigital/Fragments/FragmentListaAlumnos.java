package com.agendadigital.Fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.agendadigital.R;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.Estudiante;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.clases.Notificacion;
import com.agendadigital.clases.Notificaciones;
import com.agendadigital.clases.PublicidadInicio;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.itextpdf.text.pdf.codec.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentListaAlumnos extends Fragment {

    private ListView lvListaAlumnosBoletin;
    private AdminSQLite adm;
    private ArrayList<Estudiante> estudiantes;

    private ArrayList<PublicidadInicio> imgPublicidad;
    private ImageView imgPublicidadInicio;

    private int positonPud = 0;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_lista_alumnos, container, false);
        adm = new AdminSQLite(getContext(),"agenda",null, 1);
        enlaces(root);
        llenarLista();
        requestEstudiantes();
        requestImgPublicidad();
        oncliks();
        return root;
    }


    private void requestEstudiantes() {

        if (Globals.user.getCodigo()!=null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.url
                                          + "/estudiante.php?op=alu_tutor", new Response.Listener<String>() {
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
                        } else {
                            Toast.makeText(getContext(), jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Error en la red...", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("codigo", Globals.user.getCodigo());
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getContext()).addToRequest(stringRequest);
        }
    }

    private void requestImgPublicidad() {

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.url
                    + "/publicidad_inicio.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        JSONArray jsonArray = new JSONArray(response);
                        for (int i =0; i < jsonArray.length(); i++){
                            JSONArray fila = jsonArray.getJSONArray(i);
                            String codPud = fila.getString(0);
                            String imgPud = fila.getString(1);
                            Cursor cursor = adm.getImgEmpInicio(codPud);
                            if (!cursor.moveToFirst()) {

                                adm.saveInicioPublicidad(codPud,imgPud);

                            }
                        }
                        llenarImgPublicidad();

                        } catch (JSONException e) {
                        Toast.makeText(getContext(), "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Error en la red...", Toast.LENGTH_SHORT).show();
                }
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getContext()).addToRequest(stringRequest);

    }


    private void llenarLista() {

        Cursor cursor = adm.estudiantes(Globals.user.getCodigo());
        estudiantes = new ArrayList<>();
        ArrayList<String> nombres = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                estudiantes.add(new Estudiante(cursor.getString(0),cursor.getString(1),
                        cursor.getString(7),cursor.getString(2)));


            }while (cursor.moveToNext());

            AdapterLicencias adapter = new AdapterLicencias(getContext(), estudiantes);
            lvListaAlumnosBoletin.setAdapter(adapter);
        }
    }

    private void llenarImgPublicidad() {

        Cursor cursor = adm.getImgEmpInicio();
        imgPublicidad = new ArrayList<>();
        ArrayList<String> imgPub  = new ArrayList<>();

        if (cursor.moveToFirst()){
            do {
                if (cursor.getInt(3 ) == 1) {
                    positonPud = cursor.getInt(1);
                }
                imgPublicidad.add(new PublicidadInicio( cursor.getString(0), cursor.getString(1),
                                   cursor.getString(2), cursor.getInt(3) ));

                imgPub.add(cursor.getString(2));

            }while (cursor.moveToNext());



            if (positonPud == 0 ) {

                if (imgPublicidad.size()>1){
                    adm.setVisibilitedPub(imgPublicidad.get(1).getCodigoPublicidad());
                }
                String imgPosition = imgPublicidad.get(0).getImgPublicidad();
                imgPublicidadInicio.setImageBitmap(converter64(imgPosition));
            } else {
                for (int i = 0; i < imgPublicidad.size(); i ++) {
                    if (imgPublicidad.get(i).getVisibilidad() == 1) {

                        int nextImg =  ((i+1) % imgPublicidad.size()) + 1;
                        adm.setVisibilitedPub(imgPublicidad.get(nextImg - 1).getCodigoPublicidad());


                        imgPublicidadInicio.setImageBitmap(converter64(imgPublicidad.get(i).getImgPublicidad()));

                    }
                }
            }

        }
    }

    private Bitmap converter64(String imgPub) {
        Bitmap img = null;
        try {
            if (imgPub != null && !imgPub.isEmpty() ){

                String base64 = imgPub.split(",")[1];
                byte[] decode;

                decode = Base64.decode(base64);
                img = BitmapFactory.decodeByteArray(decode,0,decode.length);

                }
        } catch(IllegalArgumentException iae) {
            img = null;
        }
        return img;
    }


    private void oncliks() {
        lvListaAlumnosBoletin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Globals.estudiante = estudiantes.get(position);
                Cursor cursor = adm.getNotificaciones(Globals.estudiante.getCodigo(),Globals.user.getCodigo());
                if (cursor.moveToFirst()){
                    Globals.notificaciones = new Notificaciones();
                    do {
                        Globals.notificaciones.add(new Notificacion(cursor.getInt(0)
                                ,cursor.getInt(1),cursor.getInt(3),
                                cursor.getInt(7),cursor.getInt(4),
                                cursor.getString(2),cursor.getString(5),cursor.getString(6)));
                    }while (cursor.moveToNext());
                }
                cursor.close();

                switch (Globals.menu){
                    case 0:
                        Toast.makeText(getContext(),"Ir a Licencia",Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Navigation.findNavController(view).navigate(R.id.boletinFragment);
                        break;
                    case 2:
                        Toast.makeText(getContext(),"Ir a Administrar cuenta",Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Navigation.findNavController(view).navigate(R.id.kardexPagoFragment);
                        break;
                    case 4:
                        Navigation.findNavController(view).navigate(R.id.fragmentTabDinamico);
                        break;
                }
            }
        });
    }

    private void enlaces(View root) {
        lvListaAlumnosBoletin = root.findViewById(R.id.lvListaAlumnosBoletin);
        imgPublicidadInicio = root.findViewById(R.id.publicidadInicio);

    }
}