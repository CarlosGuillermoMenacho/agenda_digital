package com.agendadigital.clases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agendadigital.R;
import com.agendadigital.ViewImage;
import com.itextpdf.text.pdf.codec.Base64;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AdapterNotificaciones extends RecyclerView.Adapter<AdapterNotificaciones.ViewHolderNotificaciones> {


    private ArrayList<Notificacion> data;
    AdminSQLite adm;
    private String[] coloreMaterias;
    private Context context;

    public AdapterNotificaciones(ArrayList<Notificacion> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderNotificaciones onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificaciones_image,
                null,false);
        adm = new AdminSQLite(parent.getContext(), "agenda", null,1);
        coloreMaterias = parent.getContext().getResources().getStringArray(R.array.colores);
        return new ViewHolderNotificaciones(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNotificaciones holder, final int position) {


        String nombreMateria = adm.getNombreMateria(Globals.estudiante.getCodigo(), data.get(position).getEmisor());
        if (nombreMateria.isEmpty()){nombreMateria = data.get(position).getEmisor(); }
        holder.TV_Emisor.setText(nombreMateria);
        holder.TV_Emisor.setTextColor(Color.parseColor("#e65100"));


        holder.TV_NombreEmisor.setText(data.get(position).getNombreEmisor());
        holder.TV_Mensaje_Hora.setText(data.get(position).getHora());
        holder.IMG_Notifiacion.setImageBitmap(null);
        if (data.get(position).getTipo() == NoticationType.IMAGEN) {
            String ImgNotificacion = data.get(position).getMensaje();
            holder.TV_Mensaje.setVisibility(View.GONE);
            holder.IMG_Notifiacion.setVisibility(View.VISIBLE);
           // holder.IMG_Notifiacion.setImageBitmap(decodeBase64(ImgNotificacion));
            String path = Environment.getExternalStorageDirectory().toString()+"/saved_images/"+ImgNotificacion;
            holder.IMG_Notifiacion.setImageBitmap(BitmapFactory.decodeFile(path));
        }
        if (data.get(position).getTipo() == NoticationType.TEXT) {
            holder.IMG_Notifiacion.setVisibility(View.GONE);
            holder.TV_Mensaje.setVisibility(View.VISIBLE);
            holder.TV_Mensaje.setText(data.get(position).getMensaje());
        }
        holder.IMG_Notifiacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewImage.class);
                intent.putExtra("imagen",data.get(position).getMensaje());
                context.startActivity(intent);
            }
        });




    }
    public void clear() {
        int size = this.data.size();
        this.data.clear();
        notifyItemRangeRemoved(0, size);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolderNotificaciones extends RecyclerView.ViewHolder {
        TextView TV_Emisor,
                TV_NombreEmisor,
                TV_Mensaje_Hora,
                TV_Mensaje;
        ImageView IMG_Notifiacion;
        public ViewHolderNotificaciones(@NonNull View itemView) {
            super(itemView);

            TV_Emisor = itemView.findViewById(R.id.TV_Emisor);
            TV_NombreEmisor = itemView.findViewById(R.id.TV_NombreEmisor);
            TV_Mensaje_Hora = itemView.findViewById(R.id.TV_Mensaje_Hora);
            IMG_Notifiacion = itemView.findViewById(R.id.img_Notificacion);
            TV_Mensaje = itemView.findViewById(R.id.TV_MensajeNotificacion);
        }
    }
    public void add(Notificacion mensaje){
        data.add(mensaje);
        notifyDataSetChanged();
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    private Bitmap converter64(String imgPub) {
        Bitmap img = null;
        try {
            if (imgPub != null && !imgPub.isEmpty()) {

                String base64 = imgPub.split(",")[1];
                byte[] decode;

                decode = Base64.decode(base64);
                img = BitmapFactory.decodeByteArray(decode, 0, decode.length);

            }
        } catch (IllegalArgumentException iae) {
            img = null;
        }
        return img;
    }


}
