package com.agendadigital.clases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.agendadigital.core.modules.contacts.domain.ContactBase;
import com.agendadigital.core.modules.contacts.domain.ContactCourseBase;
import com.agendadigital.core.modules.contacts.domain.ContactTypeBase;
import com.agendadigital.core.modules.messages.domain.MessageBase;
import com.agendadigital.core.modules.messages.domain.MultimediaBase;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

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

        db.execSQL("create table notificaciones(codigo int,cod_est int,mensaje varchar,emisor varchar,cod_tutor int," +
                                    "fecha varchar, hora varchar, visto int, tipo varchar, nombreEmisor varchar)");

        db.execSQL("create table notificacionesDirCurso(id integer primary key autoincrement,codDir varchar,codCol int,cod_curso int,cod_par int,mensaje varchar,fecha varchar,hora varchar,tipo int)");
        db.execSQL("create table notificacionesDirAlumno(id integer primary key autoincrement,codDir varchar,codEst varchar,mensaje varchar,fecha varchar,hora varchar,tipo int)");
        db.execSQL("create table materias(cod_est varchar,cod_materia varchar,nomb_materia varchar)");
        db.execSQL("create table cursos(cod_pro varchar,cod_col int,cod_curso int,cod_par int,nombre varchar)");
        db.execSQL("create table materiasProf(cod_pro varchar,cod_col int,cod_mat varchar,nombre varchar)");
        db.execSQL("create table listaAlumnos(cod_pro varchar,cod_col int,cod_curso int,cod_par int,codalu int,nombre varchar)");

        db.execSQL("create table utiles(codigo int,gestion int,cod_mat varchar,materia varchar, descrip varchar)");
        db.execSQL("create table publicidad(cod_publi int,cod_materia varchar,nomb_materia varchar)");

        db.execSQL("create table cursosDir(codDir int,codCol int,codCur int,codPar int,nombre varchar)");
        db.execSQL("create table AlumnosDir(codDir int,codCol int,codCur int,codPar int,codEst int,nombre varchar)");

        db.execSQL("create table licencias(id int,codigo int,cod_tut int, f_sol varchar,h_sol varchar,f_ini varchar,f_fin varchar,obs varchar,estado int)");
        db.execSQL("create table estados(id_est int,descrip varchar)");

        db.execSQL("insert into estados values(0,'PASIVO')");
        db.execSQL("insert into estados values(1,'ACTIVO')");
        db.execSQL("insert into estados values(2,'ANULADO')");
        db.execSQL("insert into estados values(3,'EJECUTADO')");

        /*db.execSQL("insert into licencias values(1,1,1,'2020-05-01','08:01','2020-05-01','2020-05-01','prueba',0)");
        db.execSQL("insert into licencias values(2,2,1,'2020-05-01','08:02','2020-05-01','2020-05-01','prueba',0)");
        db.execSQL("insert into licencias values(3,3,3,'2020-05-01','08:03','2020-05-01','2020-05-01','prueba',0)");
        db.execSQL("insert into licencias values(4,1,1,'2020-05-01','08:04','2020-05-01','2020-05-01','prueba',0)");*/


        db.execSQL("create table alumno(codigo int,nombre varchar,curso varchar,cod_par int,cod_cur int,colegio varchar," +
                "ip varchar,cod_col int,foto varchar, activo int, esUser int,horario varchar)");



        db.execSQL("create table tutor(codigo int,nombre varchar, foto varchar,activo int, cedula varchar, telefono varchar)");

        db.execSQL("create table profesor(codigo varchar,nombre varchar, foto varchar,activo int)");

        db.execSQL("create table estudiante(codigo int,nombre varchar,foto varchar, cod_cur int,cod_par int," +
                                            "colegio varchar,ip varchar,cod_col int, curso varchar, nivel varchar, activo int)");

        db.execSQL("create table director(cod_dir int,nombre varchar, foto varchar, activo int)");
        db.execSQL("create table dir_col(cod_dir int,cod_col int,estado int)");
        db.execSQL("create table prof_col(cod_pro int,cod_col int,estado int)");
        db.execSQL("create table colegios(cod_col int,nombre varchar,turno varchar,ip varchar)");

        db.execSQL("create table personal(codigo int,nombre varchar, foto varchar,activo int,cargo varchar)");

        db.execSQL("create table ult_usr(id int,codigo int,nombre varchar,tipo varchar)");

        db.execSQL("insert into ult_usr values(1,0,'User','')");

        db.execSQL("create table colegio_adm(codAdm int, nombre varchar,turno varchar,codCol int, ip varchar, estado int)");

        /* Publicidades table */
        db.execSQL("create table empresa_pub(cod_pai int, cod_ciu int, cod_rub int, cod_emp int, nombre varchar," +
                                             "descrip varchar, url varchar, estado int)");
        db.execSQL("create table emp_publicidad( url varchar, nombre varchar, img varchar, ubica varchar, cod_emp int )");

        db.execSQL("create table emp_inicio( cod_emp int, cod_ini int, img varchar, visible int)");

        db.execSQL("create table emp_ptos_ubic(cod_emp int, cod_pto int, ubica varchar, estado int)");
        db.execSQL("create table emp_pub(cod_emp int, cod_pub int, img varchar, estado int)");
        db.execSQL("create table emp_rubro( cod_rub int, descrip varchar, estado int)");
        db.execSQL(ContactBase.SQL_CREATE_TABLE);
        db.execSQL(MessageBase.SQL_CREATE_TABLE);
        db.execSQL(MultimediaBase.SQL_CREATE_TABLE);
        db.execSQL(ContactTypeBase.SQL_CREATE_TABLE);
        db.execSQL(ContactCourseBase.SQL_CREATE_TABLE);
        /*db.execSQL("insert into materias values('1','1','Matemáticas')");
        db.execSQL("insert into materias values('1','2','Física')");
        db.execSQL("insert into materias values('1','3','Lenguaje')");
        db.execSQL("insert into materias values('1','4','Química')");
        db.execSQL("insert into materias values('1','5','Biología')");
        db.execSQL("insert into materias values('1','6','Música')");
        db.execSQL("insert into materias values('1','7','Geografía')");
        db.execSQL("insert into materias values('1','8','Filosofía')");
        db.execSQL("insert into materias values('1','9','Sociales')");
        db.execSQL("insert into materias values('1','10','Educación Física')");

        db.execSQL("insert into materias values('2','1','Matemáticas')");
        db.execSQL("insert into materias values('2','2','Física')");
        db.execSQL("insert into materias values('2','3','Lenguaje')");
        db.execSQL("insert into materias values('2','4','Química')");
        db.execSQL("insert into materias values('2','5','Biología')");
        db.execSQL("insert into materias values('2','6','Música')");
        db.execSQL("insert into materias values('2','7','Geografía')");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { db.execSQL(MessageBase.SQL_DROP_TABLE); }

  /*  public Cursor getNotificacion(String codigo){
        return getReadableDatabase().rawQuery("select * from notificaction where codigo="+codigo,null);
    }*/

    public Cursor getPublicidad () {
        return getReadableDatabase().rawQuery("select * from emp_publicidad",
                null);
    }
    public Cursor getPublicidad (String cod_publicidad) {
        return getReadableDatabase().rawQuery("select * from emp_publicidad where cod_emp="+ cod_publicidad,
                null);
    }
    public void savePublicidad (String url, String nombre, String img, String ubicacion, String codigoPublicidad) {
        getWritableDatabase().execSQL("insert into emp_publicidad values('"+url+"','"+nombre+"' ,'"+img+"'" +
                ",'"+ubicacion+"','"+codigoPublicidad+"')");
    }


    public void saveInicioPublicidad(String codigoEmpresa, String codigoPublicidad , String imgPublicidad ){
        getWritableDatabase().execSQL("insert into emp_inicio values('"+ codigoEmpresa +"','"+codigoPublicidad
                +"','"+imgPublicidad+"',0)");
    }

    public Cursor getImgEmpInicioImg (String cod_ini, String  cod_emp) {
        return getReadableDatabase().rawQuery("select img from emp_inicio where cod_emp="+ cod_emp+" and cod_ini="+cod_ini,
                null);
    }

    public Cursor getImgEmpInicio (String cod_ini, String  cod_emp) {
        return getReadableDatabase().rawQuery("select img from emp_inicio where cod_emp="+ cod_emp+" and cod_ini="+cod_ini+" and visible = 1",
                                            null);
    }
    public Cursor getImgEmpInicio () {
        return getReadableDatabase().rawQuery("select * from emp_inicio",
                null);
    }
    public void setVisibilitedPub (String cod_ini_img) {
        getWritableDatabase().execSQL("update emp_inicio set visible = 0");
        getWritableDatabase().execSQL("update emp_inicio set visible= 1 where cod_ini="+ cod_ini_img );
    }



    public void actHorario(int cod_alu, String imag){
        getWritableDatabase().execSQL("update alumno set horario='"+imag+"' where codigo="+cod_alu);
    }
    public Cursor getInfoEmp (String cod_emp) {
        return getReadableDatabase().rawQuery("select emp.nombre, emp.url, epu.ubica, ep.img  from empresa_pub emp, emp_ptos_ubic epu, emp_pub ep  " +
                                                "where emp.cod_emp="+cod_emp+" and epu.cod_emp = "+cod_emp+" and ep.cod_empu = "+cod_emp+"",null);
    }


    public void saveAlumno(String codigo, String nombre, String curso, String cod_cur, String cod_par, String colegio, String ip, String cod_col, String foto){
        getWritableDatabase().execSQL("insert into alumno values("+codigo+",'"+nombre+"','"+curso+"',"+cod_par+","+cod_cur+",'"+colegio+"','"+ip+"',"+cod_col+",'"+foto+"',0,0,'')");
    }

    public Cursor licencias(int cod_alu){//devuelve en un cursor todos los profesores habilitados
        return getReadableDatabase().rawQuery("select l.id,l.codigo,l.cod_tut,l.f_sol,l.h_sol,l.f_ini,l.f_fin,l.obs,t.nombre,e.descrip as estado from licencias l,tutor t,estados e where l.codigo="+cod_alu+" and l.cod_tut=t.codigo and l.estado=e.id_est",null);
    }

    public Cursor tutores(){//devuelve en un cursor todos los tutores habilitados
        return getReadableDatabase().rawQuery("select * from tutor",null);
    }


    public Cursor estudiant(){//devuelve en un cursor todos los tutores habilitados
        return getReadableDatabase().rawQuery("select * from estudiante",null);
    }
    public Cursor profesores(){//devuelve en un cursor todos los profesores habilitados
        return getReadableDatabase().rawQuery("select * from profesor",null);
    }
    public Cursor directores(){//devuelve en un cursor todos los tutores habilitados
        return getReadableDatabase().rawQuery("select * from director",null);
    }
    public Cursor personal(){//devuelve en un cursor todos los tutores habilitados
        return getReadableDatabase().rawQuery("select * from personal",null);
    }
    public Cursor alumnos(){//devuelve en un cursor todos los profesores habilitados
        return getReadableDatabase().rawQuery("select * from alumno where esUser = 1" ,null);
    }


    public Cursor profesor(String codigo){//devuelve los datos de un profesor dado un codigo
        return getReadableDatabase().rawQuery("select * from profesor where codigo='"+codigo+"'",null);
    }


    public void saveAdm(ArrayList<String> valores){//guarda un profesor habilitado
        getWritableDatabase().execSQL("insert into personal values('" + valores.get(0) + "','" + valores.get(1) + "','"+valores.get(2)+"' ,0,'')");
    }


    public void saveDirector(ArrayList<String> valores){//guarda un profesor habilitado
        getWritableDatabase().execSQL("insert into director values('" + valores.get(0) + "','" + valores.get(1) + "','"+valores.get(2)+"',0)");
    }
    public void saveProfesor(ArrayList<String> valores){//guarda un profesor habilitado
        getWritableDatabase().execSQL("insert into profesor values('" + valores.get(0) + "','" + valores.get(1) + "','"+valores.get(2)+"',0)");
    }
    public Cursor getAlus(String tutor){
        return getReadableDatabase().rawQuery("select * from alu_tut where tutor="+tutor,null);
    }
    public Cursor getTutors(String alu){
        return getReadableDatabase().rawQuery("select * from alu_tut where alu="+alu,null);
    }
    public void deleteProfesor(String codigo){
        getWritableDatabase().execSQL("delete from profesor where codigo='"+codigo+"'");




    }
    public void deleteDirector(String codigo){
        getWritableDatabase().execSQL("delete from cursosDir where codDir="+codigo);
        getWritableDatabase().execSQL("delete from AlumnosDir where codDir="+codigo);
        getWritableDatabase().execSQL("delete from dir_col where cod_dir="+codigo);
        getWritableDatabase().execSQL("delete from director where cod_dir='"+codigo+"'");
    }
    public void deletePersonal(String codigo){
        getWritableDatabase().execSQL("delete from personal where codigo='"+codigo+"'");
    }
    public void saveTutor(ArrayList<String> valores){
        getWritableDatabase().execSQL("insert into tutor values('"+valores.get(0)+"','"+valores.get(1)+"','"+valores.get(2)+"',1,'"+valores.get(3)+"','"+valores.get(4)+"')");
/*        userActivo(valores.get(0),"tutor");*/
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
    public Cursor verif_col(int codi_cole){
        return getReadableDatabase().rawQuery("select * from colegios where cod_col="+codi_cole,null);
    }
    public Cursor verif_alu(int codi_alum){
        return getReadableDatabase().rawQuery("select * from alumno where codigo="+codi_alum,null);
    }
    public Cursor director(String codigo){//devuelve los datos de un profesor dado un codigo
        return getReadableDatabase().rawQuery("select * from director where cod_dir='"+codigo+"'",null);
    }
    public Cursor tutor(String cedula,String telefono){
        return getReadableDatabase().rawQuery("select * from tutor where cedula='"+cedula+"' and telefono='"+telefono+"'",null);
    }
    public void deleteEstudiante(String codigo){
        getWritableDatabase().execSQL("delete from estudiante where codigo="+codigo);
    }
    public void deleteAlumno(String codigo){
        getWritableDatabase().execSQL("delete from alumno where codigo="+codigo);
    }

    public void anularLicencia(int codigo){
        getWritableDatabase().execSQL("update licencias set estado=2 where id="+codigo);
    }
    public void deleteTutor(String codigo){

        getWritableDatabase().execSQL("delete from tutor where codigo="+codigo);
        getWritableDatabase().execSQL("delete from notificaciones where cod_tutor="+codigo);
        Cursor cursor2 = getAlus(codigo);
        if (cursor2.moveToFirst()) {
            getWritableDatabase().execSQL("delete from alu_tut where tutor="+codigo);
            do {
                String codAlu = cursor2.getString(1);
                if (!tieneTutor(codAlu)){
                    deleteAlumno(codAlu);
                    deleteMaterias(codAlu);
                }
            } while (cursor2.moveToNext());
        }
    }

    private void deleteMaterias(String codAlu) {
        getWritableDatabase().execSQL("delete from materias where cod_est="+codAlu );
    }

    private boolean tieneTutor(String codAlu) {
        Cursor cursor = getReadableDatabase().rawQuery("select * from alu_tut where alu="+codAlu,null);
        return cursor.moveToFirst();
    }

    public Cursor getNotificacion(String est, String tut , String idmsg){
        return getReadableDatabase().rawQuery("select * from notificaciones where cod_est="+est+" and cod_tutor="+tut+" and codigo="+idmsg ,null);
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
        Cursor estudiant = estudiant();
        Cursor profesores = profesores();
        Cursor directores = directores();
        Cursor personal = personal();
        ArrayList<User> ususarios = new ArrayList<>();

        if (tutores.moveToFirst()){
            do {
                ususarios.add(new User(tutores.getString(0),tutores.getString(1),
                        tutores.getString(2), User.UserType.Tutor));
            }while (tutores.moveToNext());
        }
        if (estudiant.moveToFirst())
        {
            do {
                ususarios.add(new User(estudiant.getString(0),estudiant.getString(1),estudiant.getString(2), User.UserType.Student));
            }while (estudiant.moveToNext());
        }
        if (profesores.moveToFirst())
        {
            do {
                ususarios.add(new User(profesores.getString(0),profesores.getString(1),
                         profesores.getString(2), User.UserType.Teacher));
            }while (profesores.moveToNext());
        }
        if (directores.moveToFirst())
        {
            do {
                ususarios.add(new User(directores.getString(0),directores.getString(1),directores.getString(2), User.UserType.Director));
            }while (directores.moveToNext());
        }
        if (personal.moveToFirst())
        {
            do {
                ususarios.add(new User(personal.getString(0),personal.getString(1),personal.getString(2), User.UserType.Staff));
            }while (personal.moveToNext());
        }
        return  ususarios;
    }



    public Cursor estudiantes(String codigoTutor){
        return getReadableDatabase().rawQuery("select a.codigo, a.nombre, a.curso, a.cod_cur, a.colegio,a.cod_par,a.horario, a.ip, a.cod_col, a.foto " +
                                            "from alumno a,alu_tut t where t.alu = a.codigo and t.tutor='"+codigoTutor+"'",null);
    }

    /*public void saveProfesor(ArrayList<String> valores){//guarda un profesor habilitado
        getWritableDatabase().execSQL("insert into profesor values('" + valores.get(0) + "','" + valores.get(1) + "','"+valores.get(2)+"',0)");
    }*/

/*    db.execSQL("create table estudiante(codigo int,nombre varchar,foto varchar, curso varchar,cod_cur int,cod_par int," +
            "colegio varchar,ip varchar,cod_col int,nivel varchar)");*/

    public void saveEstudiante(ArrayList<String> valores){
        getWritableDatabase().execSQL("insert into estudiante values('" + valores.get(0)+"' , '" + valores.get(1) +"', '"+valores.get(8)+"', '"+valores.get(3)+"'," +
                "'"+valores.get(4)+"', '"+valores.get(5)+"', '"+valores.get(6)+"', '"+valores.get(7)+"', '"+valores.get(2)+"', '"+valores.get(9)+"', 0)");
        userActivo(valores.get(0), User.UserType.Student);
    }


    public Cursor estudiante(String codEstudiante){
        return getReadableDatabase().rawQuery("select * from estudiante where codigo='"+codEstudiante+"'",null);
    }
    public Cursor hayestudiante(String codEstudiante){
        return getReadableDatabase().rawQuery("select * from alumno where codigo='"+codEstudiante+"'",null);
    }
    public Cursor lista_utiles(String cod_alu,int ano){
        return getReadableDatabase().rawQuery("select * from utiles where gestion ="+ano+" and codigo="+cod_alu,null);
    }
    public void tutor_alu(String tutor, String alumno){
        getWritableDatabase().execSQL("insert into alu_tut values('"+tutor+"','"+alumno+"')");
    }
    public void userActivo(String codigo, User.UserType tipo){
        getWritableDatabase().execSQL("update tutor set activo = 0");
        getWritableDatabase().execSQL("update estudiante set activo = 0");
        getWritableDatabase().execSQL("update profesor set activo = 0");
        getWritableDatabase().execSQL("update director set activo = 0");
        getWritableDatabase().execSQL("update personal set activo = 0");

        switch (tipo) {
            case Tutor:
                getWritableDatabase().execSQL("update tutor set activo = 1 where codigo = " + codigo);
                break;
            case Student:
                getWritableDatabase().execSQL("update estudiante set activo = 1 where codigo = " + codigo);
                break;
            case Teacher:
                getWritableDatabase().execSQL("update profesor set activo = 1 where codigo = '" + codigo+"'");
                break;
            case Director:
                getWritableDatabase().execSQL("update director set activo = 1 where cod_dir = " + codigo);
                break;
            case Staff:
                getWritableDatabase().execSQL("update personal set activo = 1 where codigo = " + codigo);
                break;
        }
    }

    public User getProfActivo(){
        @SuppressLint("Recycle") Cursor cursor = getReadableDatabase().rawQuery("select * from profesor where activo = 1",null);
        if (cursor.moveToFirst()&&cursor.getCount()==1){
            return new User(cursor.getString(0),cursor.getString(1), cursor.getString(2), User.UserType.Teacher);
        }
        return new User();
    }
    public User getDirActivo(){
        @SuppressLint("Recycle") Cursor cursor = getReadableDatabase().rawQuery("select * from director where activo = 1",null);
        if (cursor.moveToFirst()&&cursor.getCount()==1){
            return new User(cursor.getString(0),cursor.getString(1), cursor.getString(2), User.UserType.Director);
        }
        return new User();
    }
    public User getEstActivo(){
        @SuppressLint("Recycle") Cursor cursor = getReadableDatabase().rawQuery("select * from estudiante where activo = 1",null);
        if (cursor.moveToFirst()&&cursor.getCount()==1){
            return new User(cursor.getString(0),cursor.getString(1), cursor.getString(2), User.UserType.Student);
        }
        return new User();
    }
    public User getPerActivo(){
        @SuppressLint("Recycle") Cursor cursor = getReadableDatabase().rawQuery("select * from personal where activo = 1",null);
        if (cursor.moveToFirst()&&cursor.getCount()==1){
            return new User(cursor.getString(0),cursor.getString(1), cursor.getString(2), User.UserType.Staff);
        }
        return new User();
    }
    public User getUserActivo(){

        @SuppressLint("Recycle") Cursor cursor = getReadableDatabase().rawQuery("select * from tutor where activo = 1",null);
        if (cursor.moveToFirst()&&cursor.getCount()==1){
            return new User(cursor.getString(0),cursor.getString(1), cursor.getString(2), User.UserType.Tutor);
        }

        @SuppressLint("Recycle") Cursor cursor1 = getReadableDatabase().rawQuery("select * from estudiante where activo = 1",null);
        if (cursor1.moveToFirst()&&cursor1.getCount()==1){
            return new User(cursor1.getString(0),cursor1.getString(1), cursor1.getString(2), User.UserType.Student);
        }

        @SuppressLint("Recycle") Cursor cursor2 = getReadableDatabase().rawQuery("select * from director where activo = 1",null);
        if (cursor2.moveToFirst()&&cursor2.getCount()==1){
            return new User(cursor2.getString(0),cursor2.getString(1), cursor2.getString(2), User.UserType.Director);
        }

        @SuppressLint("Recycle") Cursor cursor3 = getReadableDatabase().rawQuery("select * from profesor where activo = 1",null);
        if (cursor3.moveToFirst()&&cursor3.getCount()==1){
            return new User(cursor3.getString(0),cursor3.getString(1), cursor3.getString(2), User.UserType.Teacher);
        }

        @SuppressLint("Recycle") Cursor cursor4 = getReadableDatabase().rawQuery("select * from personal where activo = 1",null);
        if (cursor4.moveToFirst()&&cursor4.getCount()==1){
            return new User(cursor4.getString(0),cursor4.getString(1), cursor4.getString(2), User.UserType.Staff);
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

    public int notificacionesPendientes (String codEstudiantes, String codTutor) {
        return getReadableDatabase().rawQuery("select * from notificaciones  where visto = 0 " +
                        "and cod_est="+codEstudiantes + " and "+
                        "cod_tutor="+codTutor ,
                null).getCount();
    }

    public void testEstados ( String cod_tutor, String cod_estudiante) {
        getWritableDatabase().execSQL("update notificaciones set visto = 1 " +
                        "where cod_est="+cod_estudiante + " and "+
                        "cod_tutor="+cod_tutor  + " and visto = 0 " );
    }

    public ArrayList<String> obtenerIPS() {
        User userActivo = getUserActivo();
        Cursor cursor = getReadableDatabase().rawQuery("select distinct a.ip from alu_tut at, alumno a where at.alu = a.codigo and at.tutor ="+userActivo.getCodigo(),null);
        ArrayList<String> ips = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                ips.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        return ips;
    }

    public void savemsg(String mensaje) throws JSONException {
        JSONObject jsonObject = new JSONObject(mensaje);
        String id = jsonObject.getString("id");
        String est = jsonObject.getString("cod_est");
        String msg = jsonObject.getString("mensaje");
        String emisor = jsonObject.getString("emisor");
        String codTutor = jsonObject.getString("cod_tutor");
        String fecha = jsonObject.getString("fecha");
        String hora = jsonObject.getString("hora");
        String tipo = jsonObject.getString("tipo");
        String nombreEmisor = jsonObject.getString("nombreemisor");

        getWritableDatabase().execSQL("insert into notificaciones values("+id+","+est+",'"+msg+"'," +
                            "'"+emisor+"',"+codTutor+",'"+fecha+"','"+hora+"',0,"+tipo+",'"+nombreEmisor+"')");
    }
    public Cursor colegiosDir(String codigoTutor){
        return getReadableDatabase().rawQuery("select a.cod_col,a.nombre,a.ip,a.turno from colegios a,dir_col dc where dc.cod_col = a.cod_col and dc.cod_dir='"+codigoTutor+"'",null);
    }
    public Cursor colegiosProf(String codprof){
        return getReadableDatabase().rawQuery("select a.cod_col,a.nombre,a.ip,a.turno from colegios a,prof_col pc where pc.cod_col = a.cod_col and pc.cod_pro='"+codprof+"'",null);
    }
    public String getNombreMateria(String codigo, String emisor) {

        Cursor cursor =  getReadableDatabase().rawQuery("select nomb_materia from materias where cod_est ="+ codigo +
                        " and cod_materia='"+emisor+"'",
                null);
        if (cursor.moveToFirst()){

            return cursor.getString(0);

        }
        return "";

    }
    public Cursor getCursos(String prof,String col){
        return getReadableDatabase().rawQuery("select * from cursos where cod_pro='"+prof+"' and cod_col="+col,null);
    }
    public Cursor getListas(String prof,String col){
        return getReadableDatabase().rawQuery("select * from listaAlumnos where cod_pro='"+prof+"' and cod_col="+col,null);
    }
    public Cursor getMaterias(String prof,String col){
        return getReadableDatabase().rawQuery("select * from materiasProf where cod_pro='"+prof+"' and cod_col="+col,null);
    }
    public boolean existecurso(String CodProf,String codCol,String CodCur,String CodPar){
        Cursor cursor = getReadableDatabase().rawQuery("select * from cursos where cod_pro='"+CodProf+"' and cod_col="+codCol+" and cod_curso="+CodCur+" and cod_par="+CodPar,null);
        return cursor.moveToFirst();
    }

    public void saveCursoProf(String codigo, String codCol, String codCur, String paralelo, String nombre) {
        if (!existecurso(codigo,codCol,codCur,paralelo)){
            getWritableDatabase().execSQL("insert into cursos values('"+codigo+"',"+codCol+","+codCur+","+paralelo+",'"+nombre+"')");
        }
    }
    public boolean existemateria(String CodProf,String codCol,String Codmat){
        Cursor cursor = getReadableDatabase().rawQuery("select * from materiasProf where cod_pro='"+CodProf+"' and cod_col="+codCol+" and cod_mat='"+Codmat+"'",null);
        return cursor.moveToFirst();
    }
    public void saveMateriasProf(String codi_profe, String codCol, String codmat, String nombre) {
        if (!existemateria(codi_profe,codCol,codmat)){
            getWritableDatabase().execSQL("insert into materiasProf values('"+codi_profe+"',"+codCol+",'"+codmat+"','"+nombre+"')");
        }
    }
    public boolean existelistaAlumno(String CodProf,String codCol,String CodCur,String codPar,String codalu){
        Cursor cursor = getReadableDatabase().rawQuery("select * from listaAlumnos where cod_pro='"+CodProf+"' and cod_col="+codCol+" and cod_curso="+CodCur+" and cod_par="+codPar+" and codalu="+codalu,null);
        return cursor.moveToFirst();
    }
    public void saveListaAlumno(String codi_profe, String codCol, String codCur, String codPar, String codAlu, String nombre) {
        if (!existelistaAlumno(codi_profe,codCol,codCur,codPar,codAlu)){
            getWritableDatabase().execSQL("insert into listaAlumnos values('"+codi_profe+"',"+codCol+","+codCur+","+codPar+","+codAlu+",'"+nombre+"')");
        }
    }
    public boolean existecursoDir(String codigo, String codCol, String codcur){
        Cursor cursor = getReadableDatabase().rawQuery("select * from cursosDir where codDir="+codigo+" and codCol="+codCol+" and codCur="+codcur,null);
        return cursor.moveToFirst();
    }
    public void savecursosDir(String codigo, String codCol, String codcur,String codPar ,String nombre) {
        if (!existecursoDir(codigo,codCol,codcur)){
            getWritableDatabase().execSQL("insert into cursosDir values("+codigo+","+codCol+","+codcur+","+codPar+",'"+nombre+"')");
        }
    }
    public Cursor getCursosDir(String codDir,String codCol){
        return getReadableDatabase().rawQuery("select * from CursosDir where codDir="+codDir+" and codCol="+codCol,null);
    }
    public boolean existeAlumnoDir(String codigo, String codCol, String codCur, String codPar, String codest){
        Cursor cursor = getReadableDatabase().rawQuery("select * from AlumnosDir where codDir="+codigo+" and codCol="+codCol+" and codCur="+codCur+" and codPar="+codPar+" and codEst="+codest,null);
        return cursor.moveToFirst();
    }
    public void saveListaAlumnoDir(String codigo, String codCol, String codCur, String codPar, String codest, String nombre) {
        if (!existeAlumnoDir(codigo,codCol,codCur,codPar,codest)){
            getWritableDatabase().execSQL("insert into AlumnosDir values("+codigo+","+codCol+","+codCur+","+codPar+","+codest+",'"+nombre+"')");
        }
    }
    public Cursor getAlumnosDir(String codDir,String codCol,String codCur,String codPar){
        return getReadableDatabase().rawQuery("select * from AlumnosDir where codDir="+codDir+" and codCol="+codCol+" and codCur="+codCur+" and codPar="+codPar,null);
    }
    public void saveNotificacionDirCurso(String codDir, String codCol, String cod_curso, String cod_par,String mensaje,String fecha, String hora, int tipo){
        getWritableDatabase().execSQL("insert into notificacionesDirCurso(codDir,codCol,cod_curso,cod_par,mensaje,fecha,hora,tipo) values('"+codDir+"',"+codCol+","+cod_curso+", "+cod_par+",'"+mensaje+"','"+fecha+"', '"+hora+"', "+tipo+")");
    }

    public Cursor getNotificacionesDirCurso(String codDir, String codCol, String cod_curso, String cod_par) {
        return getReadableDatabase().rawQuery("select * from notificacionesDirCurso where codDir='"+codDir+"' and codCol="+codCol+" and cod_curso="+cod_curso+" and cod_par="+cod_par,null);
    }
    public void saveNotificacionDirAlumno(String codDir, String codEst,String mensaje,String fecha, String hora, int tipo){
        getWritableDatabase().execSQL("insert into notificacionesDirAlumno(codDir,codEst,mensaje,fecha,hora,tipo) values('"+codDir+"','"+codEst+"','"+mensaje+"','"+fecha+"','"+hora+"',"+tipo+")");
    }
    public Cursor getNotificacionesDirAlumno(String codDir, String codEst) {
        return getReadableDatabase().rawQuery("select * from notificacionesDirAlumno where codDir='"+codDir+"' and codEst="+codEst,null);
    }

    public void savemsgAnterior(String id, String codest, String msg, String emisor, String codtut, String fecha, String hora, String estado, String tipo,String nombreEmisor) {
        getWritableDatabase().execSQL("insert into notificaciones values("+id+","+codest+",'"+msg+"','"+emisor+"','"+codtut+"','"+fecha+"','"+hora+"','"+estado+"','"+tipo+"','"+nombreEmisor+"')");
    }

    public void saveColAdm(String codAdm, String nombreCol, String turno, String codCol, String ip, String estado) {
        getWritableDatabase().execSQL("insert into colegio_adm values("+codAdm+",'"+nombreCol+"','"+turno+"',"+codCol+",'"+ip+"',"+estado+")");
    }

    public Cursor getColAdm(String codigo) {
        return getReadableDatabase().rawQuery("select * from colegio_adm where codAdm="+codigo,null);
    }
}