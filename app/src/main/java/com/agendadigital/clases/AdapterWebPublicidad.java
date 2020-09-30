/*package com.agendadigital.clases;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agendadigital.R;

import java.util.ArrayList;

public class AdapterWebPublicidad  extends RecyclerView.Adapter<AdapterWebPublicidad.ViewHolderWeb> {

    private ArrayList<String> sitioWeb;

    static class ViewHolder extends RecyclerView.ViewHolder {


        ViewHolder(@NonNull View itemView) {
            super(itemView);


        }
    }


    public AdapterWebPublicidad(ArrayList<String> sitioWeb) {
        this.sitioWeb = sitioWeb;
    }

    @NonNull
    @Override
    public ViewHolderWeb onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publicidad,
                    null, false);
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderWeb holder, int position) {

        holder.sitioWebBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {


    }

    @Override
    public int getItemCount() {
        return sitioWeb.size();
    }

    public class ViewHolderWeb extends RecyclerView.ViewHolder {
        Button sitioWebBtn;
        public ViewHolderWeb(@NonNull View itemView) {
            super(itemView);
            sitioWebBtn = itemView.findViewById(R.id.btnSitioWeb);
        }

    }
}*/
