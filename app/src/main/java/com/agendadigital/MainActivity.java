package com.agendadigital;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;


import com.agendadigital.Fragments.BoletinFragment;
import com.agendadigital.Fragments.FragmentFormAlumno;
import com.agendadigital.Fragments.FragmentFormProfesor;
import com.agendadigital.Fragments.FragmentFormTutor;
import com.agendadigital.Fragments.FragmentKardex;
import com.agendadigital.Fragments.FragmentPublicidad;
import com.agendadigital.Interfaces.Comunicador;
import com.agendadigital.Fragments.FragmentListaAlumnos;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Globals;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static android.view.Gravity.START;

public class MainActivity extends AppCompatActivity implements Comunicador, NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private AdminSQLite adm;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MyApp);

        setContentView(R.layout.activity_main);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);



        navigationView =   findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        adm = new AdminSQLite(MainActivity.this,"agenda",null,1);

        View hview = navigationView.getHeaderView(0);

        TextView nameUser = hview.findViewById(R.id.tvUser);
        Globals.user = adm.getUserActivo();
        nameUser.setText(Globals.user.getNombre());

        if (Globals.user.getFoto() != null){

            ImageView imgUser = hview.findViewById(R.id.ivUser);
            Globals.user = adm.getUserActivo();
            imgUser.setImageBitmap(Globals.user.getFoto());
        }


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.fragmentAgendaDigital, R.id.nav_slideshow,R.id.otro_Menu,
                R.id.fragmentLicencia, R.id.fragmentPublicidad)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /* Opciones para el escojer el tema "Tema claro / Tema oscuro" */
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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
        if (fragmnet==R.id.nav_gallery) {
            FragmentListaAlumnos galleryFragment = new FragmentListaAlumnos();
            bundle.putString("codigo",dato);
            galleryFragment.setArguments(bundle);
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, galleryFragment);
            fragmentTransaction.commit();
        }
        if (fragmnet==R.id.otro_Menu) {
            FragmentKardex kardexFragment = new FragmentKardex();
            bundle.putString("codigo",dato);
            kardexFragment.setArguments(bundle);
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, kardexFragment);
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
        if (fragment==R.id.fragmentFormProfesor){
            FragmentFormProfesor fragmentFormProfesor = new FragmentFormProfesor();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_form,fragmentFormProfesor);
            fragmentTransaction.commit();
        }
        if (fragment==R.id.fragmentFormAlumno){
            FragmentFormAlumno fragmentFormAlumno = new FragmentFormAlumno();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_form, fragmentFormAlumno);
            fragmentTransaction.commit();
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /* Escojer Menu */
            int id = item.getItemId();

            if (id == R.id.otro_Menu) {
                NavigationView navigation = findViewById(R.id.nav_view);
                navigation.getMenu().clear();
                navigation.inflateMenu(R.menu.otro_menu);
                navigation.setNavigationItemSelectedListener(this);
            }

        return true;

    }

}