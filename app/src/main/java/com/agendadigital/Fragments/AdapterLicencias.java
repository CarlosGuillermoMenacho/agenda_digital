package com.agendadigital.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

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

        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.item_lista_alumno, null);

        Bitmap img;
        String base64 = foto.split(",")[1];
        byte[] decode = Base64.decode(base64);
        img = BitmapFactory.decodeByteArray(decode,0,decode.length);


        ImageView imagen = item.findViewById(R.id.imgAlumno);
        imagen.setImageBitmap(img);

        TextView nombre = item.findViewById(R.id.tvNombre);
        nombre.setText(datos.get(position).getNombre());

        return item;
    }
}
