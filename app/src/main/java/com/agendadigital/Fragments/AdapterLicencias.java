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
import com.agendadigital.clases.Estudiante;
import com.itextpdf.text.pdf.codec.Base64;

import java.util.ArrayList;


public class AdapterLicencias extends ArrayAdapter {
    private Context context;
    private ArrayList<Estudiante> datos;


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

        TextView nombre = item.findViewById(R.id.tvNombre);
        nombre.setText(datos.get(position).getNombre());




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
}
