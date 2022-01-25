package com.agendadigital;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.agendadigital.Fragments.BoletinFragment;
import com.agendadigital.Fragments.FragmentFormAdm;
import com.agendadigital.Fragments.FragmentFormDirector;
import com.agendadigital.Fragments.FragmentFormEst;
import com.agendadigital.Fragments.FragmentFormProfesor;
import com.agendadigital.Fragments.FragmentFormTutor;
import com.agendadigital.Fragments.FragmentListaAlumnos;
import com.agendadigital.Interfaces.Comunicador;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.User;
import com.agendadigital.clases.Usuarios;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.shared.infrastructure.utils.DirectoryManager;
import com.agendadigital.services.ProcessMainClass;
import com.agendadigital.services.restarter.RestartServiceBroadcastReceiver;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity  implements Comunicador {

    private static final String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    NavigationView sNavigationView;
    TextView nameUser;
    ImageView imgUser;
    private AdminSQLite adm;
    private ArrayList<String[]> codigos;
    private final int WRITE_READ_PERMISSIONS_REQUEST = 1;

    @Override
    protected void onPause() {
        super.onPause();
        Globals.tabsActivos.clear();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configTheme();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseApp.initializeApp(this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        sNavigationView = findViewById(R.id.nav_view);
        adm = new AdminSQLite(getApplicationContext(),"agenda",null, 1 );
        View hview = sNavigationView.getHeaderView(0);
        nameUser = hview.findViewById(R.id.tvUser);
        imgUser = hview.findViewById(R.id.ivUser);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.fragmentAgendaDigital, R.id.fragmentBoletin,
                R.id.fragmentLicencia, R.id.fragmentPublicidad,R.id.fragmentKardex,R.id.fragmentHorario,
                R.id.fragmentListaUtiles, R.id.fragment_Registro_Ingreso, R.id.fragment_Registro_Salida,
                R.id.fragment_Lista_Colegios_Prof, R.id.fragmentTabDinamicosEst, R.id.lista_Colegios_Fragment,
                R.id.lista_Colegios_Director_Fragment, R.id.fragmentPracticoAlumnoItem, R.id.fragmentCalendarioItem,
                R.id.fragmentEvaluacionItem, R.id.fragmentContacts
                )
                .setDrawerLayout(drawer)
                .build();


        navigation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_READ_PERMISSIONS_REQUEST) {
            Log.d(TAG, "onRequestPermissionsResult: " + Arrays.toString(permissions));
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    this.finishAffinity();
                }else {
                    switch (permission) {
                        case Manifest.permission.READ_EXTERNAL_STORAGE:
                            break;
                        case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                            DirectoryManager.createDirectories();
//                            DirectoryManager.createDocumentsDirectoryGallery();
//                            DirectoryManager.createImageDirectoryGallery();
//                            DirectoryManager.createVideoDirectoryGallery();
                            break;
                        case Manifest.permission.RECORD_AUDIO:
                            break;
                        case Manifest.permission.CAMERA:
                            break;
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void llenarListas() {

        Usuarios usuarios = new Usuarios(getApplicationContext());
        codigos = new ArrayList<>();

        ArrayList<User> arraUser = new ArrayList<>(usuarios.getUsuarios());

        for (int i = 0 ; i < arraUser.size(); i++){
            codigos.add(new String[]{arraUser.get(i).getCodigo(),arraUser.get(i).getTipo().toString()});
        }
    }
    private void deleteItem() {

        switch (codigos.get(0)[1]) {
            case "Tutor":
                adm.deleteTutor(codigos.get(0)[0]);
                break;
            case "Teacher":
                adm.deleteProfesor(codigos.get(0)[0]);
                break;
            case "Student":
                adm.deleteEstudiante(codigos.get(0)[0]);
                break;
            case "Director":
                adm.deleteDirector(codigos.get(0)[0]);
                break;
            case "Staff":
                adm.deletePersonal(codigos.get(0)[0]);
                break;
        }
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_lightTheme:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                break;
            case R.id.action_darkTheme:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                break;
            case R.id.sessionStop:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("¿Desea eliminar esta cuenta?");
                builder.setPositiveButton("Si", (dialog, which) -> {
                    llenarListas();
                    String codigo = codigos.get(0)[0];
                    deleteItem();

                    if (codigos.isEmpty()){
                        Globals.user = null;

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra("servicio","1");
                        startActivity(intent);
                    }else if (Globals.user.getCodigo().equals(codigo)){
                        Globals.user = null;
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra("servicio","1");
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No",null);
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA
                }, WRITE_READ_PERMISSIONS_REQUEST);
            }
        }else {
            DirectoryManager.createDirectories();
        }
        String extra = getIntent().getStringExtra("restartService");
        if (extra==null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
            } else {
                ProcessMainClass bck = new ProcessMainClass();
                bck.launchService(getApplicationContext());
            }
        }

        String from = getIntent().getStringExtra("from");
        if(from != null && from.equals("notification")){
            Bundle bundle = new Bundle();
            try {
                String contactId = getIntent().getStringExtra("contactId");
                int contactType = getIntent().getIntExtra("contactType", 0);
                ContactRepository contactRepository = new ContactRepository(getApplicationContext());
                ContactEntity currentContactForNotification = contactRepository.findByIdAndType(contactId, contactType);
                bundle.putSerializable("contact", currentContactForNotification);
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_fragmentAgendaDigitalToFragmentChat, bundle);
            } catch (Exception e) {
                Log.d(TAG, "onCreate: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    @Override
    public void enviarDatos(String dato,int fragmnet) {
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction;
        FragmentManager fragmentManager;
        if (fragmnet==R.id.boletinFragment) {
            BoletinFragment boletinFragment = new BoletinFragment();
            bundle.putString("codigo",dato);
            boletinFragment.setArguments(bundle);
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, boletinFragment);
            fragmentTransaction.commit();
        }
        if (fragmnet==R.id.fragment_lista_alumnos) {
            FragmentListaAlumnos galleryFragment = new FragmentListaAlumnos();
            bundle.putString("codigo",dato);
            galleryFragment.setArguments(bundle);
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, galleryFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void cambiarForm(int fragment) {
        FragmentTransaction fragmentTransaction;
        FragmentManager fragmentManager;
        if (fragment==R.id.fragmentFormTutor){
            FragmentFormTutor fragmentFormTutor = new FragmentFormTutor();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_form,fragmentFormTutor);
            fragmentTransaction.commit();
        }
        if (fragment==R.id.fragmentFormEst){
            FragmentFormEst fragmentFormEst = new FragmentFormEst();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_form,fragmentFormEst);
            fragmentTransaction.commit();
        }
        if (fragment==R.id.fragmentFormProfesor){
            FragmentFormProfesor fragmentFormProfesor = new FragmentFormProfesor();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_form,fragmentFormProfesor);
            fragmentTransaction.commit();
        }
        if (fragment==R.id.fragmentFormDirector){
            FragmentFormDirector fragmentFormDirector = new FragmentFormDirector();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_form,fragmentFormDirector);
            fragmentTransaction.commit();
        }
        if (fragment==R.id.fragmentFormAdm){
            FragmentFormAdm fragmentFormAdm = new FragmentFormAdm();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_form,fragmentFormAdm);
            fragmentTransaction.commit();
        }

    }

    private void configTheme() {
        setTheme(R.style.Theme_MyApp);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
    public void navigation(){

        AdminSQLite adm = new AdminSQLite(MainActivity.this, "agenda", null, 1);

        Menu menu = sNavigationView.getMenu();
        MenuItem agendaDigital = menu.findItem(R.id.fragmentAgendaDigital);
        MenuItem licencias = menu.findItem(R.id.fragmentLicencia);
        MenuItem kardexPago = menu.findItem(R.id.fragmentKardex);
        MenuItem boletines = menu.findItem(R.id.fragmentBoletin);
        MenuItem horarios = menu.findItem(R.id.fragmentHorario);
        MenuItem listaUtiles = menu.findItem(R.id.fragmentListaUtiles);
        MenuItem registroIngreso = menu.findItem(R.id.lista_Colegios_Fragment);
        MenuItem registroSalida = menu.findItem(R.id.lista_Colegios_Salida_Fragment);
        MenuItem fragmentTabDinamico = menu.findItem(R.id.fragmentTabDinamico);
        MenuItem fragmentTabDinamicoEst= menu.findItem(R.id.fragmentTabDinamicosEst);
        MenuItem ListaColegiosProf = menu.findItem(R.id.fragment_Lista_Colegios_Prof);
        MenuItem ListaColegiosDir = menu.findItem(R.id.lista_Colegios_Director_Fragment);
        MenuItem FragmentPracticoAlumno = menu.findItem(R.id.fragmentPracticoAlumnoItem);
        MenuItem FragmentCalendario = menu.findItem(R.id.fragmentCalendarioItem);
        MenuItem FragmentEvaluacion = menu.findItem(R.id.fragmentEvaluacionItem);
        // MenuItem ListaColegios = menu.findItem(R.id.lista_Colegios_Fragment);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(sNavigationView, navController);
        NavGraph navGraph = navController.getGraph();

        Globals.user = adm.getUserActivo();

        if (Globals.user.getTipo() != null) {
            switch (Globals.user.getTipo()) {
                case Tutor:
                    nameUser.setText(Globals.user.getNombre());
                    fragmentTabDinamico.setVisible(false);
                    registroIngreso.setVisible(false);
                    registroSalida.setVisible(false);
                    ListaColegiosProf.setVisible(false);
                    fragmentTabDinamicoEst.setVisible(false);
                    ListaColegiosDir.setVisible(false);
                    //ListaColegios.setVisible(false);
                    navGraph.setStartDestination(R.id.fragmentAgendaDigital);
                    navController.setGraph(navGraph);

                    if (Globals.user.getFoto() != null){
                        imgUser.setImageBitmap(Globals.user.getFotoConverter(Globals.user.getFoto()));
                    }

                    break;
                case Student:
                    nameUser.setText(Globals.user.getNombre());
                    agendaDigital.setVisible(false);
                    FragmentPracticoAlumno.setVisible(false);
                    FragmentCalendario.setVisible(false);
                    FragmentEvaluacion.setVisible(false);
                    listaUtiles.setVisible(false);
                    horarios.setVisible(false);
                    boletines.setVisible(false);
                    kardexPago.setVisible(false);
                    ListaColegiosDir.setVisible(false);
                    fragmentTabDinamico.setVisible(false);
                    ListaColegiosProf.setVisible(false);
                    licencias.setVisible(false);
                    registroIngreso.setVisible(false);
                    // ListaColegios.setVisible(false);
                    registroSalida.setVisible(false);
                    navGraph.setStartDestination(R.id.fragmentTabDinamicosEst);
                    navController.setGraph(navGraph);

                    if (Globals.user.getFoto() != null){
                        imgUser.setImageBitmap(Globals.user.getFotoConverter(Globals.user.getFoto()));
                    }
                    break;
                case Teacher:
                    nameUser.setText(Globals.user.getNombre());
                    agendaDigital.setVisible(false);
                    FragmentPracticoAlumno.setVisible(false);
                    FragmentEvaluacion.setVisible(false);
                    FragmentCalendario.setVisible(false);
                    agendaDigital.setVisible(false);
                    listaUtiles.setVisible(false);
                    horarios.setVisible(false);
                    fragmentTabDinamicoEst.setVisible(false);
                    boletines.setVisible(false);
                    kardexPago.setVisible(false);
                    fragmentTabDinamico.setVisible(false);
                    licencias.setVisible(false);
                    //ListaColegios.setVisible(false);
                    ListaColegiosDir.setVisible(false);
                    registroIngreso.setVisible(false);
                    registroSalida.setVisible(false);
                    navGraph.setStartDestination(R.id.fragment_Lista_Colegios_Prof);
                    navController.setGraph(navGraph);

                    if (Globals.user.getFoto() != null){
                        imgUser.setImageBitmap(Globals.user.getFotoConverter(Globals.user.getFoto()));
                    }

                    break;
                case Director:
                    nameUser.setText(Globals.user.getNombre());
                    agendaDigital.setVisible(false);
                    listaUtiles.setVisible(false);
                    horarios.setVisible(false);
                    FragmentEvaluacion.setVisible(false);
                    FragmentPracticoAlumno.setVisible(false);
                    FragmentCalendario.setVisible(false);
                    boletines.setVisible(false);
                    fragmentTabDinamicoEst.setVisible(false);
                    kardexPago.setVisible(false);
                    licencias.setVisible(false);
                    registroIngreso.setVisible(false);
                    // ListaColegios.setVisible(false);
                    registroSalida.setVisible(false);
                    ListaColegiosProf.setVisible(false);
                    fragmentTabDinamico.setVisible(false);
                    navGraph.setStartDestination(R.id.lista_Colegios_Director_Fragment);
                    navController.setGraph(navGraph);
                    if (Globals.user.getFoto() != null){
                        imgUser.setImageBitmap(Globals.user.getFotoConverter(Globals.user.getFoto()));
                    }

                    break;
                case Staff:
                    nameUser.setText(Globals.user.getNombre());
                    fragmentTabDinamico.setVisible(false);
                    ListaColegiosProf.setVisible(false);
                    agendaDigital.setVisible(false);
                    listaUtiles.setVisible(false);
                    FragmentPracticoAlumno.setVisible(false);
                    FragmentCalendario.setVisible(false);
                    fragmentTabDinamicoEst.setVisible(false);
                    horarios.setVisible(false);
                    FragmentEvaluacion.setVisible(false);
                    boletines.setVisible(false);
                    kardexPago.setVisible(false);
                    licencias.setVisible(false);
                    ListaColegiosDir.setVisible(false);
                    navGraph.setStartDestination(R.id.lista_Colegios_Fragment);
                    navController.setGraph(navGraph);

                    if (Globals.user.getFoto() != null){
                        imgUser.setImageBitmap(Globals.user.getFotoConverter(Globals.user.getFoto()));
                    }
                    break;
            }
        } else {
            fragmentTabDinamicoEst.setVisible(false);
            ListaColegiosProf.setVisible(false);
            agendaDigital.setVisible(false);
            listaUtiles.setVisible(false);
            FragmentPracticoAlumno.setVisible(false);
            FragmentCalendario.setVisible(false);
            horarios.setVisible(false);
            boletines.setVisible(false);
            FragmentEvaluacion.setVisible(false);
            kardexPago.setVisible(false);
            licencias.setVisible(false);
            registroIngreso.setVisible(false);
            registroSalida.setVisible(false);
            //ListaColegios.setVisible(false);
            ListaColegiosDir.setVisible(false);
            fragmentTabDinamico.setVisible(false);
            navGraph.setStartDestination(R.id.nav_home);
            navController.setGraph(navGraph);

        }
    }
}

