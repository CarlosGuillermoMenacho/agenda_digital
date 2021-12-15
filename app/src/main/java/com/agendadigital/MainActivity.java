package com.agendadigital;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
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
import com.agendadigital.core.shared.infrastructure.Firebase;
import com.agendadigital.services.ProcessMainClass;
import com.agendadigital.services.restarter.RestartServiceBroadcastReceiver;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity  implements Comunicador {

    private AppBarConfiguration mAppBarConfiguration;
    NavigationView sNavigationView;
    TextView nameUser;
    ImageView imgUser;

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

        View hview = sNavigationView.getHeaderView(0);
        nameUser = hview.findViewById(R.id.tvUser);
        imgUser = hview.findViewById(R.id.ivUser);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.fragmentAgendaDigital, R.id.fragmentBoletin,
                R.id.fragmentLicencia, R.id.fragmentPublicidad,R.id.fragmentKardex,R.id.fragmentHorario,
                R.id.fragmentListaUtiles, R.id.fragment_Registro_Ingreso, R.id.fragment_Registro_Salida,
                R.id.fragment_Lista_Colegios_Prof, R.id.fragmentTabDinamicosEst, R.id.lista_Colegios_Fragment,
                R.id.lista_Colegios_Director_Fragment, R.id.fragmentPracticoAlumnoItem, R.id.fragmentCalendarioItem,
                R.id.fragmentEvaluacionItem
                )
                .setDrawerLayout(drawer)
                .build();
        navigation();
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
        String extra = getIntent().getStringExtra("restartService");
        if (extra==null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
            } else {
                ProcessMainClass bck = new ProcessMainClass();
                bck.launchService(getApplicationContext());
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
        MenuItem fragmentChat = menu.findItem(R.id.fragmentChat);
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
            ListaColegiosDir.setVisible(false);
            fragmentTabDinamico.setVisible(false);
            fragmentChat.setVisible(false);
            navGraph.setStartDestination(R.id.nav_home);
            navController.setGraph(navGraph);
        }
    }
}

