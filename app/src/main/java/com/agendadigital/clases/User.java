package com.agendadigital.clases;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.agendadigital.R;
import com.itextpdf.text.pdf.codec.Base64;

public class User {
    private String codigo;
    private String nombre;
    private String tipo;
    private String foto;

    public User(){
        this.codigo = null;
        this.nombre = null;
        this.tipo = null;
        this.foto = null;
    }
    public User(String codigo, String nombre, String foto ,String tipo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }


    public String getCodigo() {
        return codigo;
    }

    public String getTipo() {
        return tipo;
    }



    public Bitmap getFoto(){
        Bitmap img = null;
        try {

            if (foto != null && !foto.isEmpty() ){

                String base64 = foto.split(",")[1];
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
