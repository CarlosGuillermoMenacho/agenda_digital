package com.agendadigital;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.agendadigital.clases.Utils;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity  implements Comunicador {

    private AppBarConfiguration mAppBarConfiguration;
    private IntentFilter filter = new IntentFilter("SENDMESSAGE");
    NavigationView sNavigationView;
    private TextView nameUser;
    private AdminSQLite adm;

    private Utils utils;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "ON Destroyee",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "ON Pauseee",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configTheme();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        sNavigationView = findViewById(R.id.nav_view);

        View hview = sNavigationView.getHeaderView(0);
        nameUser = hview.findViewById(R.id.tvUser);

        adm = new AdminSQLite(MainActivity.this, "agenda", null, 1);


/*        Globals.user = adm.getUltUsr();*/

        Menu menu = sNavigationView.getMenu();
        MenuItem admnistrarCuentas = menu.findItem(R.id.nav_home);
        MenuItem agendaDigital = menu.findItem(R.id.fragmentAgendaDigital);
        MenuItem licencias = menu.findItem(R.id.fragmentLicencia);
        MenuItem kardexPago = menu.findItem(R.id.fragmentKardex);
        MenuItem boletines = menu.findItem(R.id.fragmentBoletin);
        MenuItem horarios = menu.findItem(R.id.fragmentHorario);
        MenuItem listaUtiles = menu.findItem(R.id.fragmentListaUtiles);
        MenuItem publicidad = menu.findItem(R.id.fragmentPublicidad);
        MenuItem registroIngreso = menu.findItem(R.id.fragment_Registro_Ingreso);
        MenuItem registroSalida = menu.findItem(R.id.fragment_Registro_Salida);
        MenuItem fragmentTabDinamico = menu.findItem(R.id.fragmentTabDinamico);


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.fragmentAgendaDigital, R.id.fragmentBoletin,
                R.id.fragmentLicencia, R.id.fragmentPublicidad,R.id.fragmentKardex,R.id.fragmentHorario,
                R.id.fragmentListaUtiles, R.id.fragment_Registro_Ingreso, R.id.fragment_Registro_Salida,
                R.id.fragmentTabDinamico)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(sNavigationView, navController);
        NavGraph navGraph = navController.getGraph();

        Globals.user = adm.getUserActivo();
        if (Globals.user.getTipo() != null) {

            switch (Globals.user.getTipo()) {

                case "tutor":
                    nameUser.setText(Globals.user.getNombre());
                    fragmentTabDinamico.setVisible(false);
                    registroIngreso.setVisible(false);
                    registroSalida.setVisible(false);

                    if (Globals.user.getFoto() != null){

                        ImageView imgUser = hview.findViewById(R.id.ivUser);
                        imgUser.setImageBitmap(Globals.user.getFotoConverter(Globals.user.getFoto()));
                    }

                    break;
                case "estudiante":
                    nameUser.setText(Globals.user.getNombre());
                    agendaDigital.setVisible(false);
                    listaUtiles.setVisible(false);
                    horarios.setVisible(false);
                    boletines.setVisible(false);
                    kardexPago.setVisible(false);
                    licencias.setVisible(false);
                    registroIngreso.setVisible(false);
                    registroSalida.setVisible(false);
                    navGraph.setStartDestination(R.id.fragmentTabDinamico);
                    navController.setGraph(navGraph);

                    if (Globals.user.getFoto() != null){

                        ImageView imgUser = hview.findViewById(R.id.ivUser);
                        imgUser.setImageBitmap(Globals.user.getFotoConverter(Globals.user.getFoto()));
                    }
                    break;
                case "profesor":
                    nameUser.setText(Globals.user.getNombre());
                    agendaDigital.setVisible(false);
                    listaUtiles.setVisible(false);
                    horarios.setVisible(false);
                    boletines.setVisible(false);
                    kardexPago.setVisible(false);
                    licencias.setVisible(false);
                    registroIngreso.setVisible(false);
                    registroSalida.setVisible(false);
                    navGraph.setStartDestination(R.id.fragmentTabDinamico);
                    navController.setGraph(navGraph);

                    if (Globals.user.getFoto() != null){

                        ImageView imgUser = hview.findViewById(R.id.ivUser);
                        imgUser.setImageBitmap(Globals.user.getFotoConverter(Globals.user.getFoto()));
                    }

                    break;
                case "director":
                    nameUser.setText(Globals.user.getNombre());
                    agendaDigital.setVisible(false);
                    listaUtiles.setVisible(false);
                    horarios.setVisible(false);
                    boletines.setVisible(false);
                    kardexPago.setVisible(false);
                    licencias.setVisible(false);
                    registroIngreso.setVisible(false);
                    registroSalida.setVisible(false);
                    navGraph.setStartDestination(R.id.fragmentTabDinamico);
                    navController.setGraph(navGraph);
                    if (Globals.user.getFoto() != null){

                        ImageView imgUser = hview.findViewById(R.id.ivUser);
                        imgUser.setImageBitmap(Globals.user.getFotoConverter(Globals.user.getFoto()));
                    }

                    break;
                case "personal":
                    nameUser.setText(Globals.user.getNombre());
                    fragmentTabDinamico.setVisible(false);
                    agendaDigital.setVisible(false);
                    listaUtiles.setVisible(false);
                    horarios.setVisible(false);
                    boletines.setVisible(false);
                    kardexPago.setVisible(false);
                    licencias.setVisible(false);
                    navGraph.setStartDestination(R.id.fragment_Registro_Ingreso);
                    navController.setGraph(navGraph);

                    if (Globals.user.getFoto() != null){

                        ImageView imgUser = hview.findViewById(R.id.ivUser);
                        imgUser.setImageBitmap(Globals.user.getFotoConverter(Globals.user.getFoto()));
                    }

                    break;
            }

        } else {

            agendaDigital.setVisible(false);
            listaUtiles.setVisible(false);
            horarios.setVisible(false);
            boletines.setVisible(false);
            kardexPago.setVisible(false);
            licencias.setVisible(false);
            registroIngreso.setVisible(false);
            registroSalida.setVisible(false);
            fragmentTabDinamico.setVisible(false);
            navGraph.setStartDestination(R.id.nav_home);
            navController.setGraph(navGraph);

        }





    }


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
   /* @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }
        getApplication().registerReceiver(receiver,filter);
    }*/
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
        if (fragmnet==R.id.nav_gallery) {
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(),"Reaccionando",Toast.LENGTH_SHORT).show();
        }
    };

    private void configTheme() {
        setTheme(R.style.Theme_MyApp);
    }
}

