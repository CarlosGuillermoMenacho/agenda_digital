package com.agendadigital.clases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AdminSQLite extends SQLiteOpenHelper {
    public AdminSQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table kar_alu(codigo int,detalle varchar,fecha date ,recnum int,haber float,acreedor float)");
        db.execSQL("create table notas(codigo int,cod_mat varchar,descri varchar ,nota1 varchar,nota2 varchar,nota3 varchar)");
        db.execSQL("create table tutor(codigo varchar,nombre varchar, cedula varchar, telefono varchar, activo int)");
        db.execSQL("create table alu_tut(tutor int,alu int)");
        db.execSQL("create table alumno(codigo int,nombre,curso varchar,cod_cur int)");
        db.execSQL("create table profesor(codigo varchar,nombre varchar, activo int)");
        db.execSQL("create table notificaciones(codigo int,cod_est int,mensaje varchar,emisor int,cod_tutor int,fecha varchar, hora varchar, visto int)");
        db.execSQL("create table materias(cod_est varchar,cod_materia varchar,nomb_materia varchar)");


        db.execSQL("insert into materias values('241','1','Matemáticas')");
        db.execSQL("insert into materias values('241','2','Física')");
        db.execSQL("insert into materias values('241','3','Lenguaje')");
        db.execSQL("insert into materias values('241','4','Química')");
        db.execSQL("insert into materias values('241','5','Biología')");
        db.execSQL("insert into materias values('241','6','Música')");
        db.execSQL("insert into materias values('241','7','Geografía')");
        db.execSQL("insert into materias values('241','8','Filosofía')");
        db.execSQL("insert into materias values('241','9','Sociales')");
        db.execSQL("insert into materias values('241','10','Educación Física')");

        db.execSQL("insert into notificaciones values(1,241,'Mensaje numero 1 de matematicas',1,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(2,241,'Mensaje numero 2 de matematicas',1,22,'2020-05-03','08:30',0)");
        db.execSQL("insert into notificaciones values(3,241,'Mensaje numero 3 de matematicas',1,22,'2020-05-04','08:30',0)");
        db.execSQL("insert into notificaciones values(4,241,'Mensaje numero 4 de matematicas',1,22,'2020-05-05','08:30',0)");
        db.execSQL("insert into notificaciones values(5,241,'Mensaje numero 5 de matematicas',1,22,'2020-05-06','08:30',0)");

        db.execSQL("insert into notificaciones values(6,241,'Mensaje numero 1 de fisica',2,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(7,241,'Mensaje numero 2 de fisica',2,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(8,241,'Mensaje numero 3 de fisica',2,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(9,241,'Mensaje numero 4 de fisica',2,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(10,241,'Mensaje numero 5 de fisica',2,22,'2020-05-02','08:30',0)");

        db.execSQL("insert into notificaciones values(11,241,'Mensaje numero 1 de lenguaje',3,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(12,241,'Mensaje numero 2 de lenguaje',3,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(13,241,'Mensaje numero 3 de lenguaje',3,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(14,241,'Mensaje numero 4 de lenguaje',3,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(15,241,'Mensaje numero 5 de lenguaje',3,22,'2020-05-02','08:30',0)");

        db.execSQL("insert into notificaciones values(16,241,'Mensaje numero 1 de quimica',4,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(17,241,'Mensaje numero 2 de quimica',4,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(18,241,'Mensaje numero 3 de quimica',4,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(19,241,'Mensaje numero 4 de quimica',4,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(20,241,'Mensaje numero 5 de quimica',4,22,'2020-05-02','08:30',0)");

        db.execSQL("insert into notificaciones values(21,241,'Mensaje numero 1 de Biologia',5,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(22,241,'Mensaje numero 2 de Biologia',5,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(23,241,'Mensaje numero 3 de Biologia',5,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(24,241,'Mensaje numero 4 de Biologia',5,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(25,241,'Mensaje numero 5 de Biologia',5,22,'2020-05-02','08:30',0)");

        db.execSQL("insert into notificaciones values(26,241,'Mensaje numero 1 de musica',6,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(27,241,'Mensaje numero 2 de musica',6,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(28,241,'Mensaje numero 3 de musica',6,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(29,241,'Mensaje numero 4 de musica',6,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(30,241,'Mensaje numero 5 de musica',6,22,'2020-05-02','08:30',0)");

        db.execSQL("insert into notificaciones values(31,241,'Mensaje numero 1 de geografia',7,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(32,241,'Mensaje numero 2 de geografia',7,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(33,241,'Mensaje numero 3 de geografia',7,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(34,241,'Mensaje numero 4 de geografia',7,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(35,241,'Mensaje numero 5 de geografia',7,22,'2020-05-02','08:30',0)");

        db.execSQL("insert into notificaciones values(36,241,'Mensaje numero 1 de filosofia',8,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(37,241,'Mensaje numero 2 de filosofia',8,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(38,241,'Mensaje numero 3 de filosofia',8,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(39,241,'Mensaje numero 4 de filosofia',8,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(40,241,'Mensaje numero 5 de filosofia',8,22,'2020-05-02','08:30',0)");

        db.execSQL("insert into notificaciones values(41,241,'Mensaje numero 1 de sociales',9,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(42,241,'Mensaje numero 2 de sociales',9,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(43,241,'Mensaje numero 3 de sociales',9,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(44,241,'Mensaje numero 4 de sociales',9,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(45,241,'Mensaje numero 5 de sociales',9,22,'2020-05-02','08:30',0)");

        db.execSQL("insert into notificaciones values(46,241,'Mensaje numero 1 de educacion fisica',10,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(47,241,'Mensaje numero 2 de educacion fisica',10,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(48,241,'Mensaje numero 3 de educacion fisica',10,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(49,241,'Mensaje numero 4 de educacion fisica',10,22,'2020-05-02','08:30',0)");
        db.execSQL("insert into notificaciones values(50,241,'Mensaje numero 5 de educacion fisica',10,22,'2020-05-02','08:30',0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
    public Cursor tutor(String cedula,String telefono){
        return getReadableDatabase().rawQuery("select * from tutor where cedula='"+cedula+"' and telefono='"+telefono+"'",null);
    }
    public void deleteEstudiante(String codigo){
        getWritableDatabase().execSQL("delete from alumno where codigo = "+codigo);
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
        return getReadableDatabase().rawQuery("select a.codigo,a.nombre from alumno a,alu_tut t where t.alu = a.codigo and t.tutor="+codigoTutor,null);
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
    public void saveNotificacion(String codigo,String cod_est,String menasje, String emisor,String fecha,String hora,String cod_tutor){
        getWritableDatabase().execSQL("insert into notificaciones values("+codigo+","+cod_est+",'"+menasje+"',"+emisor+","+cod_tutor+",'"+fecha+"','"+hora+"',0)");
    }
    public Cursor getNotificacion(String codigo){
        return getReadableDatabase().rawQuery("select * from notificaction where codigo="+codigo,null);
    }
    public Cursor getNotificaciones(String cod_est,String cod_tutor){
        return getReadableDatabase().rawQuery("select * from notificaciones where cod_est="+cod_est+" and cod_tutor="+cod_tutor,null);
    }
    public Cursor getNotificaciones(String cod_est,String cod_tutor,String emisor){
        return getReadableDatabase().rawQuery("select * from notificaciones where cod_est="+cod_est+" and emisor="+emisor+" and cod_tutor="+cod_tutor,null);
    }
    public void saveMaterias(String Alu,String codMat,String nombMat){
        getWritableDatabase().execSQL("insert into materias values('"+Alu+"','"+codMat+"','"+nombMat+"')");
    }
    public Cursor getMaterias(String alu){
        return getReadableDatabase().rawQuery("select * from materias where cod_est='"+alu+"'",null);
    }

    public int getCountNotificacion (String cod_emisor, String cod_tutor, String cod_estudiante) {

        return getReadableDatabase().rawQuery("select * from notificaciones " +
                        "where cod_est="+cod_estudiante + "  and  emisor= "+ cod_emisor +" and "+
                        "cod_tutor="+cod_tutor ,
                null).getCount();

    }
    public int testEstados (String cod_emisor, String cod_tutor, String cod_estudiante) {
        return getReadableDatabase().rawQuery("update notificaciones set visto = 1 " +
                        "where cod_est="+cod_estudiante + "  and  emisor= "+ cod_emisor +" and "+
                        "cod_tutor="+cod_tutor ,
                null).getCount();
    }


}
