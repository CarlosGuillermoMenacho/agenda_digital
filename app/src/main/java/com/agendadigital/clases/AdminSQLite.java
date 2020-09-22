package com.agendadigital.clases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

public class AdminSQLite extends SQLiteOpenHelper {
    public AdminSQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table kar_alu(codigo int,detalle varchar,fecha date ,recnum int,haber float,acreedor float)");
        db.execSQL("create table notas(codigo int,cod_mat varchar,descri varchar ,nota1 varchar,nota2 varchar,nota3 varchar)");
        db.execSQL("create table tutor(codigo int,nombre varchar, cedula varchar, telefono varchar, activo int)");
        db.execSQL("create table alu_tut(tutor int,alu int)");
        db.execSQL("create table alumno(codigo int,nombre,curso varchar,cod_cur int,colegio varchar,ip varchar,cod_col int,foto varchar)");
        db.execSQL("create table profesor(codigo varchar,nombre varchar, activo int)");
        db.execSQL("create table licencias(id int,codigo int,cod_tut int, f_sol varchar,h_sol varchar,f_ini varchar,f_fin varchar,obs varchar,estado int)");
        db.execSQL("create table estados(id_est int,descrip varchar)");

        db.execSQL("insert into estados values(0,'PASIVO')");
        db.execSQL("insert into estados values(1,'ACTIVO')");
        db.execSQL("insert into estados values(2,'ANULADO')");
        db.execSQL("insert into estados values(3,'EJECUTADO')");
     /*   db.execSQL("insert into licencias values(1,1,1,'2020-05-01','08:01','2020-05-01','2020-05-01','prueba',0)");
        db.execSQL("insert into licencias values(2,2,1,'2020-05-01','08:02','2020-05-01','2020-05-01','prueba',0)");
        db.execSQL("insert into licencias values(3,3,3,'2020-05-01','08:03','2020-05-01','2020-05-01','prueba',0)");
        db.execSQL("insert into licencias values(4,1,1,'2020-05-01','08:04','2020-05-01','2020-05-01','prueba',0)");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor licencias(int cod_alu){//devuelve en un cursor todos los profesores habilitados
        return getReadableDatabase().rawQuery("select l.id,l.codigo,l.cod_tut,l.f_sol,l.h_sol,l.f_ini,l.f_fin,l.obs,t.nombre,e.descrip as estado from licencias l,tutor t,estados e where l.codigo="+cod_alu+" and l.cod_tut=t.codigo and l.estado=e.id_est",null);
    }
    public Cursor tutores(){//devuelve en un cursor todos los tutores habilitados
        return getReadableDatabase().rawQuery("select * from tutor",null);
    }
    public Cursor profesores(){//devuelve en un cursor todos los profesores habilitados
        return getReadableDatabase().rawQuery("select * from profesor",null);
    }
    public Cursor profesor(String codigo){//devuelve los datos de un profesor dado un codigo
        return getReadableDatabase().rawQuery("select * from profesor where codigo='"+codigo+"'",null);
    }
    public void saveProfesor(ArrayList<String> valores){//guarda un profesor habilitado
        getWritableDatabase().execSQL("insert into profesor values('" + valores.get(0) + "','" + valores.get(1) + "',0)");
    }
    public Cursor getAlus(String tutor){
        return getReadableDatabase().rawQuery("select * from alu_tut where tutor = "+tutor,null);
    }
    public Cursor getTutors(String alu){
        return getReadableDatabase().rawQuery("select * from alu_tut where alu="+alu,null);
    }
    public void deleteProfesor(String codigo){
        getWritableDatabase().execSQL("delete from profesor where codigo='"+codigo+"'");
    }
    public void saveTutor(ArrayList<String> valores){
        getWritableDatabase().execSQL("insert into tutor values('"+valores.get(0)+"','"+valores.get(1)+"','"+valores.get(2)+"','"+valores.get(3)+"',0)");
    }
    public void saveLicencia(int id,int codigo,int cod_tut,String f_sol,String hora,String f_ini,String f_fin,String obs){
        getWritableDatabase().execSQL("insert into licencias values("+id+","+codigo+","+cod_tut+",'"+f_sol+"','"+hora+"','"+f_ini+"','"+f_fin+"','"+obs+"',1)");
    }
    public Cursor licencia(int cod_alu,Date fecha){
        return getReadableDatabase().rawQuery("select * from licencias where estado=1 and codigo='"+cod_alu+"'",null);
    }
    public Cursor verif_Lic(int codi_lice){
        return getReadableDatabase().rawQuery("select * from licencias where id="+codi_lice,null);
    }
    public Cursor verif_alu(int codi_alum){
        return getReadableDatabase().rawQuery("select * from alumno where codigo="+codi_alum,null);
    }
    public Cursor tutor(String cedula,String telefono){
        return getReadableDatabase().rawQuery("select * from tutor where cedula='"+cedula+"' and telefono='"+telefono+"'",null);
    }
    public void deleteEstudiante(String codigo){
        getWritableDatabase().execSQL("delete from alumno where codigo = "+codigo);
    }
    public void anularLicencia(int codigo){
        getWritableDatabase().execSQL("update licencias set estado=2 where id="+codigo);
    }
    public void deleteTutor(String codigo){
        Cursor cursor = getAlus(codigo);
        if (cursor.moveToFirst()){
            do {
                Cursor cursor1 = getTutors(cursor.getString(1));
                if (cursor1.getCount()==1){
                    deleteEstudiante(cursor.getString(1));
                }
            }while (cursor.moveToNext());
        }
        getWritableDatabase().execSQL("delete from tutor where codigo="+codigo);
        getWritableDatabase().execSQL("delete from alu_tut where tutor = "+codigo);
    }

    public ArrayList<Licencia> obt_licencias(){
        Cursor licenc = licencias(Integer.parseInt(Globals.estudiante.getCodigo()));
        ArrayList<Licencia> ususarios = new ArrayList<>();
        if (licenc.moveToFirst()) {
            do {
                ususarios.add(new Licencia(licenc.getString(0), licenc.getString(1), licenc.getString(2), licenc.getString(3), licenc.getString(4), licenc.getString(5), licenc.getString(6), licenc.getString(7), licenc.getString(8), licenc.getString(9)));
            } while (licenc.moveToNext());
        }
        return  ususarios;
    }

    public ArrayList<User> users(){
        Cursor tutores = tutores();
        Cursor profesores = profesores();
        ArrayList<User> ususarios = new ArrayList<>();
        if (tutores.moveToFirst()){
            do {
                ususarios.add(new User(tutores.getString(0),tutores.getString(1),"tutor"));
            }while (tutores.moveToNext());
        }
        if (profesores.moveToFirst())
        {
            do {
                ususarios.add(new User(profesores.getString(0),profesores.getString(1),"profesor"));
            }while (profesores.moveToNext());
        }
        return  ususarios;
    }
    public Cursor estudiantes(String codigoTutor){
        return getReadableDatabase().rawQuery("select a.codigo,a.nombre,a.colegio from alumno a,alu_tut t where t.alu = a.codigo and t.tutor="+codigoTutor,null);
    }
    public Cursor estudiante(String codEstudiante){
        return getReadableDatabase().rawQuery("select * from alumno where codigo='"+codEstudiante+"'",null);
    }
    public void saveAlumno(String codigo , String nombre , String curso , String codCurso){
        getWritableDatabase().execSQL("insert into alumno values('"+codigo+"','"+nombre+"','"+curso+"','"+codCurso+"')");
    }
    public void tutor_alu(String tutor, String alumno){
        getWritableDatabase().execSQL("insert into alu_tut values('"+tutor+"','"+alumno+"')");
    }
    public void userActivo(String codigo, String tipo){
        if (tipo.equals("tutor")){
            getWritableDatabase().execSQL("update tutor set activo = 0");
            getWritableDatabase().execSQL("update tutor set activo = 1 where codigo = "+codigo);
        }
        if (tipo.equals("profesor")){
            getWritableDatabase().execSQL("update profesor set activo = 0");
            getWritableDatabase().execSQL("update profesor set activo = 1 where codigo = "+codigo);
        }
    }
    public User getUserActivo(){
        @SuppressLint("Recycle") Cursor cursor = getReadableDatabase().rawQuery("select * from tutor where activo = 1",null);
        if (cursor.moveToFirst()&&cursor.getCount()==1){
            return new User(cursor.getString(0),cursor.getString(1),"tutor");
        }
        @SuppressLint("Recycle") Cursor cursor1 = getReadableDatabase().rawQuery("select * from profesor where activo = 1",null);
        if (cursor1.moveToFirst()&&cursor1.getCount()==1){
            return new User(cursor1.getString(0),cursor1.getString(1),"profesor");
        }
        return new User();
    }
}