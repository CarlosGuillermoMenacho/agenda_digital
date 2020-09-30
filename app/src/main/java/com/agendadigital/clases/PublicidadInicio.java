package com.agendadigital.clases;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.itextpdf.text.pdf.codec.Base64;

public class PublicidadInicio {

    private String cod_emp;
    private String cod_ini;
    private String imgPublicidad;
    private int visibilidad;

    public PublicidadInicio() {
    }

    public PublicidadInicio(String codigoPublicidad, String codigoInicio, String img, int visibilidad) {

        this.cod_emp = codigoPublicidad;
        this.cod_ini = codigoInicio;
        this.imgPublicidad = img;
        this.visibilidad= visibilidad;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }


    public int getVisibilidad() {
        return visibilidad;
    }

    public String getImgPublicidad() {
        return imgPublicidad;
    }

    public String getCodigoPublicidad () {
        return cod_ini;}

    public Bitmap getFotoPublicidad () {

        Bitmap img = null;
        try {
            if (imgPublicidad != null && !imgPublicidad.isEmpty() ){

                String base64 = imgPublicidad.split(",")[1];
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
