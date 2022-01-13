 package com.agendadigital.clases;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.itextpdf.text.pdf.codec.Base64;

import java.io.Serializable;

 public class User {
    private String codigo;
    private String nombre;
    private UserType tipo;
    private String foto;

    public User(){
        this.codigo = null;
        this.nombre = null;
        this.tipo = null;
        this.foto = null;
    }
    public User(String codigo, String nombre, String foto ,UserType tipo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public UserType getTipo() {
        return tipo;
    }
    public void setTipo(UserType tipo) {
        this.tipo = tipo;
    }

    public String getFoto() { return foto; }


    public Bitmap getFotoConverter(String foto){
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
    public void setFoto(String foto) {
        this.foto = foto;
    }

    public enum UserType {
        Tutor(1),
        Student(2),
        Teacher(3),
        Director(4),
        Staff(5);

        private final int value;

        UserType(int value) {
            this.value =value;
        }

        public int getValue() {
            return value;
        }
        public static UserType setValue(int value) throws Exception {
            switch (value){
                case 1:
                    return Tutor;
                case 2:
                    return Student;
                case 3:
                    return Teacher;
                case 4:
                    return Director;
                case 5:
                    return Staff;
                default:
                    throw new Exception("UserType inválido.");
            }
        }
    }
}
