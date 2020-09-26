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
        db.execSQL("create table alu_tut(tutor int,alu int)");

        db.execSQL("create table notificaciones(codigo int,cod_est int,mensaje varchar,emisor int,cod_tutor int,fecha varchar, hora varchar, visto int)");
        db.execSQL("create table materias(cod_est varchar,cod_materia varchar,nomb_materia varchar)");

        db.execSQL("create table publicidad(cod_publi int,cod_materia varchar,nomb_materia varchar)");


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

        db.execSQL("create table alumno(codigo int,nombre varchar,curso varchar,cod_cur int,colegio varchar," +
                "ip varchar,cod_col int,foto varchar, activo int, esUser int)");
        db.execSQL("create table tutor(codigo int,nombre varchar, foto varchar, cedula varchar, telefono varchar," + " activo int)");
        db.execSQL("create table profesor(codigo varchar,nombre varchar, foto varchar,"+ "activo int)");


        /* Publicidades table */
        db.execSQL("create table empresa_pub(cod_pai int, cod_ciu int, cod_rub int, cod_emp int, nombre varchar," +
                                             "descrip varchar, url varchar, estado int)");
        db.execSQL("create table emp_inicio( cod_emp int, cod_ini int, img varchar, estado int)");
        db.execSQL("create table emp_ptos_ubic(cod_emp int, cod_pto int, ubica varchar, estado int)");
        db.execSQL("create table emp_pub(cod_emp int, cod_pub int, img varchar, estado int)");
        db.execSQL("create table emp_rubro( cod_rub int, descrip varchar, estado int)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

  /*  public Cursor getNotificacion(String codigo){
        return getReadableDatabase().rawQuery("select * from notificaction where codigo="+codigo,null);
    }*/

    public Cursor getImgEmpInicio (String cod_emp) {
        return getReadableDatabase().rawQuery("select img from emp_inicio where cod_emp="+ cod_emp+" and estado = 1",
                                            null);
    }

    public Cursor getInfoEmp (String cod_emp) {
        return getReadableDatabase().rawQuery("select emp.nombre, emp.url, epu.ubica, ep.img  from empresa_pub emp, emp_ptos_ubic epu, emp_pub ep  " +
                                                "where emp.cod_emp="+cod_emp+" and epu.cod_emp = "+cod_emp+" and ep.cod_empu = "+cod_emp+"",null);
    }


    public void saveAlumno(String codigo , String nombre , String curso , String codCurso, String colegio,
                           String ip, String cod_col, String foto, int esUser  ){
        getWritableDatabase().execSQL("insert into alumno values('"+codigo+"','"+nombre+"','"+curso+"','"
                                        +codCurso+"','"+colegio+"','"+ip+"','"+cod_col+"','"+foto+"','',"+esUser+")");
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
    public Cursor alumnos(){//devuelve en un cursor todos los profesores habilitados
        return getReadableDatabase().rawQuery("select * from alumno where esUser = 1" ,null);
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
        getWritableDatabase().execSQL("insert into tutor values('"+valores.get(0)+"','"+valores.get(1)+"','"+valores.get(2)+"','"+valores.get(3)+"','"+valores.get(4)+"',0)");
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
        Cursor alumnos = alumnos();
        ArrayList<User> ususarios = new ArrayList<>();
        if (tutores.moveToFirst()){
            do {
                ususarios.add(new User(tutores.getString(0),tutores.getString(1),
                        tutores.getString(2),"tutor"));
            }while (tutores.moveToNext());
        }
        if (profesores.moveToFirst())
        {
            do {
                ususarios.add(new User(profesores.getString(0),profesores.getString(1),
                         profesores.getString(2),"profesor"));
            }while (profesores.moveToNext());
        }
        if (alumnos.moveToFirst())
        {
            do {
                ususarios.add(new User(alumnos.getString(0),alumnos.getString(1),
                        alumnos.getString(7) ,"alumno"));
            }while (profesores.moveToNext());
        }


        return  ususarios;
    }



    public Cursor estudiantes(String codigoTutor){
        return getReadableDatabase().rawQuery("select a.codigo, a.nombre, a.curso, a.cod_cur, a.colegio, a.ip, a.cod_col, a.foto " +
                                            "from alumno a,alu_tut t where t.alu = a.codigo and t.tutor="+codigoTutor,null);
    }
    public Cursor estudiante(String codEstudiante){
        return getReadableDatabase().rawQuery("select * from alumno where codigo='"+codEstudiante+"'",null);
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
            return new User(cursor.getString(0),cursor.getString(1), cursor.getString(2), "tutor");
        }
        @SuppressLint("Recycle") Cursor cursor1 = getReadableDatabase().rawQuery("select * from profesor where activo = 1",null);
        if (cursor1.moveToFirst()&&cursor1.getCount()==1){
            return new User(cursor1.getString(0),cursor1.getString(1),cursor1.getString(2) ,"profesor");
        }
        @SuppressLint("Recycle") Cursor cursor2 = getReadableDatabase().rawQuery("select * from alumno where activo = 1",null);
        if (cursor1.moveToFirst()&&cursor2.getCount()==1){
            return new User(cursor2.getString(0),cursor2.getString(1), cursor2.getString(7),"profesor");
        }


        return new User();
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

    public Cursor getMaterias(String alu){
        return getReadableDatabase().rawQuery("select * from materias where cod_est='"+alu+"'",null);
    }

    public void saveNotificacion(String codigo,String cod_est,String menasje, String emisor,String fecha,String hora,String cod_tutor){
        getWritableDatabase().execSQL("insert into notificaciones values("+codigo+","+cod_est+",'"+menasje+"',"+emisor+"," +
                "                           "+cod_tutor+",'"+fecha+"','"+hora+"',0)");
    }

    public void saveMaterias(String Alu,String codMat,String nombMat){
        getWritableDatabase().execSQL("insert into materias values('"+Alu+"','"+codMat+"','"+nombMat+"')");
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