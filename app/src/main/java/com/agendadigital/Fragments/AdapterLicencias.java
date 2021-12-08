package com.agendadigital.Fragments;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import android.widget.TextView;
import com.agendadigital.R;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Estudiante;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.Menus;
import com.itextpdf.text.pdf.codec.Base64;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;


public class AdapterLicencias extends ArrayAdapter {
    private Context context;
    private ArrayList<Estudiante> datos;
    private ArrayList<View> views = new ArrayList<>();

    public AdapterLicencias(Context context, ArrayList datos) {
        super(context, R.layout.item_lista_alumno, datos);
        // Guardamos los parámetros en variables de clase.
        this.context = context;
        this.datos = datos;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String foto = datos.get(position).getFoto();

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_alumno,
                null,false);
        views.add(item);

        TextView nombre = item.findViewById(R.id.tvNombre);
        nombre.setText(datos.get(position).getNombre());

        if (Globals.menu == Menus.AGENDA) {
            NotificationBadge badge = item.findViewById(R.id.badge);
            if (Integer.parseInt(datos.get(position).getCantidadMensajes()) > 0) {
                badge.setNumber(Integer.parseInt(datos.get(position).getCantidadMensajes()));
            }

        }




        try {
            Bitmap img;
            if (!foto.isEmpty()){

            String base64 = foto.split(",")[1];
            byte[] decode;

            decode = Base64.decode(base64);
            img = BitmapFactory.decodeByteArray(decode,0,decode.length);

            ImageView imagen = item.findViewById(R.id.imgAlumno);
            imagen.setImageBitmap(img);
            }

        } catch(IllegalArgumentException iae) {

        }
        return item;
    }
    public void showBadge(String codEst){

        int position = getPosition(codEst);
        NotificationBadge badge = views.get(position).findViewById(R.id.badge);
        badge.setNumber(0);
        if (position != -1) {
            AdminSQLite adm = new AdminSQLite(getContext(), "agenda", null, 1);
            int cantidad = adm.notificacionesPendientes(codEst, Globals.user.getCodigo());
            datos.get(position).setcantidadMensajes(Integer.toString(cantidad));
            if (Integer.parseInt(datos.get(position).getCantidadMensajes()) > 0) {
                badge.setNumber(Integer.parseInt(datos.get(position).getCantidadMensajes()));
            }
        }
    }
    private int getPosition(String codest){
        for (int i = 0 ; i < datos.size() ;  i++){
            if (datos.get(i).getCodigo().equals(codest)){
                return i;
            }
        }
        return -1;
    }




}
