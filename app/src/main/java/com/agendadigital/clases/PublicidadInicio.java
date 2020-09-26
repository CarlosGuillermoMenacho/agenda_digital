package com.agendadigital.clases;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.itextpdf.text.pdf.codec.Base64;

public class PublicidadInicio {

    private String codigoPublicidad;
    private String publicidadFoto;

    public PublicidadInicio( String codigoPublicidad, String publicidadFoto ) {

        this.codigoPublicidad = codigoPublicidad;
        this.publicidadFoto = publicidadFoto;

    }

    public String getCodigoPublicidad () { return codigoPublicidad;}

    public Bitmap getFotoPublicidad () {

        Bitmap img = null;
        try {
            if (publicidadFoto != null && !publicidadFoto.isEmpty() ){

                String base64 = publicidadFoto.split(",")[1];
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
