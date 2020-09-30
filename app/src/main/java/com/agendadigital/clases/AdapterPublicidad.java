package com.agendadigital.clases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agendadigital.R;
import com.itextpdf.text.pdf.codec.Base64;

import java.util.ArrayList;

public class AdapterPublicidad extends RecyclerView.Adapter<AdapterPublicidad.ViewHolderPublicidad> {

    Context context;
    ArrayList<Publicidad> dataPublicidad;

    public AdapterPublicidad(Context context, ArrayList<Publicidad> dataPublicidad) {
        this.context = context;
        this.dataPublicidad = dataPublicidad;
    }

    @NonNull
    @Override
    public ViewHolderPublicidad onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_publicidad, parent,false);
        ViewHolderPublicidad holderPublicidad = new ViewHolderPublicidad(view);
        return holderPublicidad;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPublicidad holder, final int position) {

        holder.btnSitioWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = dataPublicidad.get(position).getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });

        holder.btnPuntoVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = dataPublicidad.get(position).getUbicacion();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });

        String imgPublicidad = dataPublicidad.get(position).getImg();
        holder.imageViewPublicidad.setImageBitmap(converter64(imgPublicidad));

    }

    @Override
    public int getItemCount() {
        return dataPublicidad.size();
    }

    public class ViewHolderPublicidad extends RecyclerView.ViewHolder {

        Button btnSitioWeb, btnPuntoVentas;
        ImageView imageViewPublicidad;

        public ViewHolderPublicidad(@NonNull View itemView) {
            super(itemView);
            btnPuntoVentas = itemView.findViewById(R.id.btnPuntosVentas);
            btnSitioWeb = itemView.findViewById(R.id.btnSitioWeb);
            imageViewPublicidad = itemView.findViewById(R.id.imgPublicidadEmpresa);

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



}
