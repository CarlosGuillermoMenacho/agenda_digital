package com.agendadigital.clases;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agendadigital.R;
import com.itextpdf.text.pdf.codec.Base64;

import java.util.ArrayList;

public class AdapterNotificacionesDir extends RecyclerView.Adapter<AdapterNotificacionesDir.ViewHolderNotificacionesMaterias> {

    private ArrayList<NotificacionDir> data;

    public AdapterNotificacionesDir(ArrayList<NotificacionDir> data) {
        this.data = data;
    }


    @NonNull
    @Override
    public ViewHolderNotificacionesMaterias onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificaciones_director_msg,
                null,false);
        return new ViewHolderNotificacionesMaterias(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNotificacionesDir.ViewHolderNotificacionesMaterias holder,
                                 int position) {


        holder.TV_NombreEmisor.setText(data.get(position).getFecha());
        holder.TV_Mensaje_Hora.setText(data.get(position).getHora());

        if (data.get(position).getTipo() == NoticationType.IMAGEN) {
            String ImgNotificacion = data.get(position).getMensaje();
            holder.IMG_Notifiacion.setImageBitmap(converter64(ImgNotificacion));
        }
        if (data.get(position).getTipo() == NoticationType.TEXT) {
            holder.TV_Mensaje.setText(data.get(position).getMensaje());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolderNotificacionesMaterias extends RecyclerView.ViewHolder {


        TextView TV_NombreEmisor,
                TV_Mensaje_Hora,
                    TV_Mensaje;
        ImageView IMG_Notifiacion;

        public ViewHolderNotificacionesMaterias(@NonNull View itemView) {
            super(itemView);

            TV_NombreEmisor = itemView.findViewById(R.id.TV_Fecha);
            TV_Mensaje_Hora = itemView.findViewById(R.id.TV_Mensaje_HoraMDir);
            IMG_Notifiacion = itemView.findViewById(R.id.img_NotificacionMDir);
            TV_Mensaje = itemView.findViewById(R.id.TV_MensajeEmisorDir);
        }
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
    public void clear() {
        int size = this.data.size();
        this.data.clear();
        notifyItemRangeRemoved(0, size);
    }
    public void add(NotificacionDir mensaje){
        data.add(mensaje);
        notifyDataSetChanged();
    }
}
