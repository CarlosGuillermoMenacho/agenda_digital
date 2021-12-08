package com.agendadigital.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.itextpdf.text.pdf.codec.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Horario_Fragment extends Fragment {

    private static final String BUNDLE_STATE = "ImageViewState";
    private SubsamplingScaleImageView ivhor;
    Bitmap horabm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_horarios, container, false);
        ImageViewState imageViewState = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_STATE)) {
            imageViewState = (ImageViewState) savedInstanceState.getSerializable(BUNDLE_STATE);
        }
        ivhor=root.findViewById(R.id.ivHor);


        TextView titulo = root.findViewById(R.id.text_gallery);
        titulo.setText(Globals.estudiante.getNombre());

        String horar3 = Globals.estudiante.getHor();
        if (!horar3.isEmpty()) {  // si no es vacio que muestre
            String base64 = horar3.split(",")[1];
            byte[] decode = Base64.decode(base64);
            horabm = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            ivhor.setImage(ImageSource.bitmap(horabm));
            ivhor.setOrientation(SubsamplingScaleImageView.ORIENTATION_270);
            ivhor.setMaxScale(SubsamplingScaleImageView.SCALE_TYPE_START);

        }else{  // si esta vacio que vaya y lo busque en el servidor
            String d_ip = Globals.estudiante.getIp();
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Obteniendo Horario Escolar...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+d_ip + "/agendadigital/get_horario.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("ok")) {
                            AdminSQLite adminSQLite = new AdminSQLite(getContext(), "agenda", null, 1);

                            JSONObject datosArray = jsonObject.getJSONObject("horario");

                            String imagen2 = datosArray.getString("imagen");

                            adminSQLite.actHorario(Integer.parseInt(Globals.estudiante.getCodigo()),imagen2);

                            Globals.estudiante.setHor(imagen2);
                            String base64 = imagen2.split(",")[1];
                            byte[] decode = Base64.decode(base64);
                            horabm = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                            ivhor.setImage(ImageSource.bitmap(horabm));

                        } else {
                            builder.setMessage("El horario NO existe...");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        }
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
                    params.put("cod_cur", Globals.estudiante.getCod_cur());
                    params.put("cod_par", Globals.estudiante.getCod_par());
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getContext()).addToRequest(stringRequest);
        }
        return root;
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        View rootView = getView();
        if (rootView != null) {
            SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)rootView.findViewById(R.id.ivHor);
            ImageViewState state = imageView.getState();
            if (state != null) {
                outState.putSerializable(BUNDLE_STATE, imageView.getState());
            }
        }
    }
}