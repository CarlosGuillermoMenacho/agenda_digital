package com.agendadigital.clases;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agendadigital.R;

import java.util.ArrayList;

public class AdapterNotificaciones extends RecyclerView.Adapter<AdapterNotificaciones.ViewHolderNotificaciones> {
    private ArrayList<String> itemFacturas;

    public AdapterNotificaciones(ArrayList<String> itemFacturas) {
        this.itemFacturas = itemFacturas;
    }


    @NonNull
    @Override
    public ViewHolderNotificaciones onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notificactio_message,
                null,false);
        return new ViewHolderNotificaciones(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNotificaciones holder, int position) {
        holder.mensaje.setText(itemFacturas.get(position));

    }

    @Override
    public int getItemCount() {
        return itemFacturas.size();
    }

    public static class ViewHolderNotificaciones extends RecyclerView.ViewHolder {
        TextView mensaje;
        public ViewHolderNotificaciones(@NonNull View itemView) {
            super(itemView);
            mensaje = itemView.findViewById(R.id.notificacion_message);

        }

    }
}
