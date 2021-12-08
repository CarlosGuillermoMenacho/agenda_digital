package com.agendadigital.Fragments;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.icu.text.SimpleDateFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.agendadigital.BuildConfig;
import com.agendadigital.R;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Alumno;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.Cursos;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.Materia;
import com.agendadigital.clases.MySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FragmentFormMsgProf extends Fragment {
    private Button btnSendAll;
    private Button btnSendEstudiante;
    private EditText etmsg;
    private String cod_prof,clave_prof;
    private Spinner cursos,materias,Alumnos;
    private ArrayList<Cursos> cursosArray;
    private ArrayList<String> opcionesCursos;
    private ArrayList<Materia> materiasArray;
    private ArrayList<String> opcionesMaterias;
    private ArrayList<Alumno> alumnosArray,listaArray;
    private ArrayList<String> opcionesAlumnos;
    private Cursos cursoSelected;
    private Materia materiaSelected;
    private Alumno alumnoSelected;
    private ArrayList<String> notas;

    private ListView lvNotasRapidas;
    private ImageView back,next,imgCamera,imgGaleria;

    private static final int CAMARA = 1;
    private static final int GALERIA = 2;
    private static final int PERMISO_GALERIA = 3;
    private static final int PERMISO_CAMARA = 4;

    String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    private AdminSQLite adm;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    String h_actual = simpleDateFormat.format(new Date());

    private File actualImage;
    private Uri imagePath;
    private String curretPhotoPath;
    public FragmentFormMsgProf() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_form_msg_prof, container, false);
        adm = new AdminSQLite(getContext(),"agenda",null, 1);
        cursoSelected=null;

        hacerCast(vista);
        obtenerListas();
        mostrarListas();
        llenarNotasRapidas();
        oncliks();
        return vista;
    }

    private void llenarNotasRapidas() {
        notas = new ArrayList<>();
        notas.add("Agrede vervalmente a sus compañeros");
        notas.add("Su conducta fue regular");
        notas.add("Se portó mal en clases");
        notas.add("Sr. padre de familia debe presentarse en el colegio");
        notas.add("Llega tarde a clases");
        notas.add("No cumple con el uniforme");
        notas.add("Se levanta del asiento y pasea");
        notas.add("No trae material respectivo");
        notas.add("Uso de celular en clases");
        notas.add("No participa en clases");
        notas.add("Su conducta fue muy buena");
        notas.add("Buena participación en clases");
        notas.add("Es muy inteligente");
        notas.add("Respeta a sus compañeros");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,notas);
        lvNotasRapidas.setAdapter(adapter);
    }

    private void mostrarListas() {
        ArrayAdapter<String> adapterCursos = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,opcionesCursos);
        ArrayAdapter<String> adapterMaterias = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,opcionesMaterias);
        ArrayAdapter<String> adapterAlumnos = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,opcionesAlumnos);

        cursos.setAdapter(adapterCursos);
        materias.setAdapter(adapterMaterias);
        Alumnos.setAdapter(adapterAlumnos);


        cursos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0){
                    cursoSelected = cursosArray.get(position-1);
                    llenarAlumnos(cursosArray.get(position-1).getCod_curso(),cursosArray.get(position-1).getCod_par());
                    Alumnos.setAdapter(new ArrayAdapter<>(requireContext(),R.layout.item_spinner,opcionesAlumnos));
                }else{
                    materias.setSelection(0);
                    cursoSelected = null;
                    opcionesAlumnos.clear();
                    opcionesAlumnos.add("Seleccionar Alumno");
                    listaArray.clear();
                    Alumnos.setAdapter(new ArrayAdapter<>(requireContext(),R.layout.item_spinner,opcionesAlumnos));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        materias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0){
                    materiaSelected = materiasArray.get(position-1);
                }else{
                    materiaSelected = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Alumnos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0){
                    alumnoSelected = alumnosArray.get(position-1);
                }else{
                    alumnoSelected = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void llenarAlumnos(String cod_curso, String cod_par) {
        opcionesAlumnos.clear();
        opcionesAlumnos.add("Seleccionar Alumno");
        listaArray = new ArrayList<>();
        for (int i = 0; i < alumnosArray.size(); i++){
            if (alumnosArray.get(i).getCod_cur().equals(cod_curso)&&alumnosArray.get(i).getCod_par().equals(cod_par)){
                opcionesAlumnos.add(alumnosArray.get(i).getNombre());
                listaArray.add(alumnosArray.get(i));
            }
        }
    }

    private void obtenerListas() {
        Cursor cursor = adm.getCursos(Globals.user.getCodigo(),Globals.colegio.getCodigo());
        opcionesCursos = new ArrayList<>();
        opcionesMaterias = new ArrayList<>();
        opcionesAlumnos = new ArrayList<>();
        cursosArray = new ArrayList<>();
        materiasArray = new ArrayList<>();
        alumnosArray = new ArrayList<>();
        opcionesCursos.add("Seleccionar Curso");
        opcionesMaterias.add("Seleccionar Materia");
        opcionesAlumnos.add("Seleccionar Alumno");
        listaArray = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                cursosArray.add(new Cursos(cursor.getString(2),cursor.getString(3),cursor.getString(4)));
                opcionesCursos.add(cursor.getString(4));
            }while (cursor.moveToNext());
        }
        cursor = adm.getMaterias(Globals.user.getCodigo(),Globals.colegio.getCodigo());
        if (cursor.moveToFirst()){
            do {
                materiasArray.add(new Materia(cursor.getString(2),cursor.getString(3)));
                opcionesMaterias.add(cursor.getString(3));
            }while (cursor.moveToNext());
        }
        cursor = adm.getListas(Globals.user.getCodigo(),Globals.colegio.getCodigo());
        if (cursor.moveToFirst()){
            do {
                alumnosArray.add(new Alumno(cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5)));
            }while (cursor.moveToNext());
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode ==  PERMISO_GALERIA) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectedGalery();
            } else {
                Toast.makeText(getContext(), "Habilite los permisos", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode ==  PERMISO_CAMARA) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePhoto();
            } else {
                Toast.makeText(getContext(), "Habilite los permisos", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    private void selectedGalery() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        cameraIntent.setType("image/*");
        if(cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, GALERIA);
        }
    }
    public void capturePhoto() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
            startActivityForResult(intent, CAMARA);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    public String getImagePath(Uri uri){
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContext().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    public String getBase64ImageString(Bitmap photo) {
        String imgString;
        if(photo != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] profileImage = outputStream.toByteArray();

            imgString = Base64.encodeToString(profileImage,
                    Base64.NO_WRAP);
        }else{
            imgString = "";
        }

        return imgString;
    }
    public static String convertBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMARA && resultCode == RESULT_OK) {
            Bitmap pathFotoCamara = compressImageFoto(curretPhotoPath);
            String base64Foto = convertBase64(pathFotoCamara); // Aqui obtengo base 64

            if (validarCursoMateria()) {
                if (Alumnos.getSelectedItemPosition() > 0) {
                    enviaraNODEJSIMAG(alumnosArray.get(Alumnos.getSelectedItemPosition()-1).getCod_alu(),
                            materiasArray.get(materias.getSelectedItemPosition()-1).getCod_mat(),
                            Globals.user.getCodigo(), base64Foto);
                }else{
                    enviaraNODEJSIMAGALL(cursosArray.get(cursos.getSelectedItemPosition()).getCod_curso(),
                            cursosArray.get(cursos.getSelectedItemPosition()-1).getCod_par(),
                            materiasArray.get(materias.getSelectedItemPosition()-1).getCod_mat(),Globals.user.getCodigo(),Globals.user.getNombre(),base64Foto);
                }
            }
            // si queres ver la base64 de arriba pon un punto de debug abajo de esta linea ...
            /*String base64Foto1 = convertBase64(pathFotoCamara);*/
        }
        if (requestCode == GALERIA && resultCode == RESULT_OK) {
            Uri pathGallery = data.getData();
            String pathFotoGallery = getImagePath(pathGallery);
            Bitmap pathGalery = compressImageFoto(pathFotoGallery);
            String base64Galeria = convertBase64(pathGalery); // Aqui obtengo base 64
            if (validarCursoMateria()) {
                if (Alumnos.getSelectedItemPosition() > 0) {
                    enviaraNODEJSIMAG(alumnosArray.get(Alumnos.getSelectedItemPosition()-1).getCod_alu(),
                            materiasArray.get(materias.getSelectedItemPosition()-1).getCod_mat(),
                            Globals.user.getCodigo(), base64Galeria);
                }else{
                    enviaraNODEJSIMAGALL(cursosArray.get(cursos.getSelectedItemPosition()).getCod_curso(),
                            cursosArray.get(cursos.getSelectedItemPosition()).getCod_par(),
                            materiasArray.get(materias.getSelectedItemPosition()-1).getCod_mat(),Globals.user.getCodigo(),Globals.user.getNombre(),base64Galeria);
                }
            }
            /*String base64Galeria1 = convertBase64(pathGalery);*/

        }
    }
    private void oncliks() {

        imgGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, PERMISO_GALERIA);
                } else {
                    selectedGalery();
                }
            }
        });
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISO_CAMARA);
                } else {
                    capturePhoto();
                }
            }
        });
        btnSendAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCursoMateria()) {
                    for (int i = 0; i < listaArray.size(); i++){
                        String nomMateria = materiaSelected.getCod_mat();
                        String nombProf = Globals.user.getNombre();
                        String msg = etmsg.getText().toString();
                        String cod_cur = cursoSelected.getCod_curso();
                        String cod_par = cursoSelected.getCod_par();
                        String cod_mat = materiaSelected.getCod_mat();
                        String cod_alu = listaArray.get(i).getCod_alu();
                        String obs = "-1";
                        String cod_prof = Globals.user.getCodigo();
                        habilitar( nomMateria, nombProf, msg, cod_cur, cod_par, cod_mat, cod_alu, obs, cod_prof);
                        enviaraNODEJS(cod_alu,cod_mat,cod_prof,msg);
                    }

                }else {
                    Toast.makeText(getContext(),"Debe elegir Curso y materia...",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSendEstudiante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarDatos()) {
                    String nomMateria = materiaSelected.getCod_mat();
                    String nombProf = Globals.user.getNombre();
                    String msg = etmsg.getText().toString();
                    String cod_cur = cursoSelected.getCod_curso();
                    String cod_par = cursoSelected.getCod_par();
                    String cod_mat = materiaSelected.getCod_mat();
                    String cod_alu = alumnoSelected.getCod_alu();
                    String obs = "-1";
                    String cod_prof = Globals.user.getCodigo();
                    habilitar(nomMateria, nombProf, msg, cod_cur, cod_par, cod_mat, cod_alu, obs, cod_prof);
                    enviaraNODEJS(cod_alu, cod_mat, cod_prof, msg);
                }else {
                    Toast.makeText(getContext(),"Debe elegir Curso, materia y estudiante...",Toast.LENGTH_SHORT).show();
                }
            }
        });
        lvNotasRapidas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (validarDatos()){
                    String nomMateria = materiaSelected.getCod_mat();
                    String nombProf = Globals.user.getNombre();
                    String msg = notas.get(position);
                    String cod_cur = cursoSelected.getCod_curso();
                    String cod_par = cursoSelected.getCod_par();
                    String cod_mat = materiaSelected.getCod_mat();
                    String cod_alu = alumnoSelected.getCod_alu();
                    String obs = Integer.toString(position);
                    String cod_prof = Globals.user.getCodigo();
                    habilitar(nomMateria, nombProf, msg, cod_cur, cod_par, cod_mat, cod_alu, obs, cod_prof);
                    enviaraNODEJS(cod_alu, cod_mat, cod_prof, msg);
                }else {
                    Toast.makeText(getContext(),"Debe elegir Curso, materia y estudiante...",Toast.LENGTH_SHORT).show();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Alumnos.getSelectedItemPosition();
                if (position < listaArray.size()){
                    Alumnos.setSelection(position+1);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Alumnos.getSelectedItemPosition();
                if (position > 0){
                    Alumnos.setSelection(position-1);
                }
            }
        });
    }
    private File createImageFile() throws IOException {
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDir);
        curretPhotoPath = image.getAbsolutePath();
        return image;
    }


    public Bitmap compressImageFoto(String imageUri) {

        String filePath = imageUri;
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 1200.0f;
        float maxWidth = 900.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scaledBitmap;
    }
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }


    private boolean validarCursoMateria() {
        return (cursoSelected !=null) && (materiaSelected!=null);
    }

    private void enviaraNODEJS(String cod_alu, String cod_mat, String cod_prof, String msg) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("codEst",cod_alu);
            jsonObject.put("codEmit", cod_prof);
            jsonObject.put("codmat",cod_mat);
            jsonObject.put("msg",msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "http://"+Globals.colegio.getIp()+"/agenda/mensajeprof";
        //String url = "http://192.168.100.96:3000/agenda/mensajeprof";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("ok")){
                    Toast.makeText(getContext(),"Mensaje enviado a nodeJS",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonObject==null? null : jsonObject.toString().getBytes();
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(stringRequest);
    }
    private void enviaraNODEJSIMAGALL(String cod_cur,String cod_par, String cod_mat, String cod_prof,String nombre, String msg) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("codCur",cod_cur);
            jsonObject.put("codEmit", cod_prof);
            jsonObject.put("codPar",cod_par);
            jsonObject.put("codMat",cod_mat);
            jsonObject.put("nombre",nombre);
            jsonObject.put("msg",msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "http://"+Globals.colegio.getIp()+"/agenda/mensajeimagprofall";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("ok")){
                    Toast.makeText(getContext(),"Imágen enviada a nodeJS",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonObject==null? null : jsonObject.toString().getBytes();
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(stringRequest);
    }

    private void enviaraNODEJSIMAG(String cod_alu, String cod_mat, String cod_prof, String msg) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("codEst",cod_alu);
            jsonObject.put("codEmit", cod_prof);
            jsonObject.put("codmat",cod_mat);
            jsonObject.put("msg",msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+Globals.colegio.getIp()+"/agenda/mensajeimagprof", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("ok")){
                    Toast.makeText(getContext(),"Imágen enviada a nodeJS",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonObject==null? null : jsonObject.toString().getBytes();
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(stringRequest);
    }

    private void habilitar(final String l_mate, final String l_nomprof, final String l_msg, final String l_cur, final String l_par, final String l_mat, final String l_alu, final String l_obs, final String l_prof) {
        if (validarDatos()){
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Validando...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+Globals.colegio.getIp() + "/agendadigital/grab_msg_prof.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("ok")) {

                            builder.setMessage("Msg enviado con exito...");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Navigation.findNavController(v).navigate(R.id.nav_home);
                                }
                            });
                        } else {
                            builder.setMessage("El usuario no existe...");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        }
                        builder.show();
                    } catch (JSONException e) {
                        Toast.makeText(getContext(),"Error al procesar los datos...",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),"Error en la red...",Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("materia", l_mate);
                    params.put("nombre_prof", l_nomprof);
                    params.put("msg",l_msg);
                    params.put("codcur",l_cur);
                    params.put("codpar",l_par);
                    params.put("codmat",l_mat);
                    params.put("codalu",l_alu);
                    params.put("codobs",l_obs);
                    params.put("codprof",l_prof);
                    params.put("fecha", date);
                    params.put("hora", h_actual);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getContext()).addToRequest(stringRequest);
        }else{
            Toast.makeText(getContext(),"Faltan datos...",Toast.LENGTH_SHORT).show();
        }
    }


    private boolean validarDatos() {
        return (cursoSelected !=null) && (materiaSelected!=null)&& (alumnoSelected!=null);
    }

    private void hacerCast(View vista) {
        //btnCancelar = vista.findViewById(R.id.btnCancelarformprofesormsg);
        cursos = vista.findViewById(R.id.spCurso);
        materias = vista.findViewById(R.id.spMateria);
        Alumnos = vista.findViewById(R.id.spAlumno);
        lvNotasRapidas = vista.findViewById(R.id.lvNotasRapidas);
        btnSendAll = vista.findViewById(R.id.btnSendAll);
        btnSendEstudiante = vista.findViewById(R.id.btnSendAlumno);
        etmsg = vista.findViewById(R.id.editTextTextMultiLine);
        back = vista.findViewById(R.id.imgAnterior);
        next = vista.findViewById(R.id.imgNext);
        imgCamera = vista.findViewById(R.id.imgCamera);
        imgGaleria = vista.findViewById(R.id.imggaleria);
        // clave = vista.findViewById(R.id.etclaveformprofesormsg);
    }
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem dark = menu.findItem(R.id.action_darkTheme);
        MenuItem light = menu.findItem(R.id.action_lightTheme);
        if ( dark != null) {
            dark.setVisible(false);
        }
        if ( light != null) {
            light.setVisible(false);
        }
    }
}