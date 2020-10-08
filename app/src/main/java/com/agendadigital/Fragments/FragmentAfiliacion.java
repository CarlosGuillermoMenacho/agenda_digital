package com.agendadigital.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


import com.agendadigital.MainActivity;

import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.Menus;
import com.agendadigital.clases.User;
import com.agendadigital.clases.Usuarios;
import com.agendadigital.services.Service;

import java.util.ArrayList;

import static com.agendadigital.R.id;
import static com.agendadigital.R.layout;

public class FragmentAfiliacion extends Fragment {
    private ArrayList<String[]> codigos;
    private Button btnNew;
    private Button btnDelete;
    private Button btnListo;
    private TextView title;
    private ListView lista;
    private boolean delete = false;
    private AdminSQLite adm;
    private Usuarios usuarios;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(layout.fragment_afiliacion,container,false);
        adm = new AdminSQLite(getContext(),"agenda",null, 1 );
        Globals.menu = Menus.ADMCUENTA;
        enlaces(vista);
        llenarListas();
        onclick();
        return vista;
    }
    private void onclick() {

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(v).navigate(id.fragmentFormAfiliacion);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"SetTextI18n"})
            @Override
            public void onClick(View v) {
                btnListo.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.GONE);
                btnNew.setVisibility(View.GONE);
                title.setText("Eliminar Cuenta");
                delete = true;
            }
        });
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (delete){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("¿Desea eliminar esta cuenta?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String codigo = codigos.get(position)[0];
                            deleteItem(position);

                            llenarListas();

                            if (codigos.isEmpty()){
                                Globals.user = null;
                                Globals.user.setFoto(null);
                                Globals.user.setNombre(null);
                                requireActivity().stopService(new Intent(getContext(), Service.class));
                            }else if (Globals.user.getCodigo().equals(codigo)){
                                Globals.user = null;
                                requireActivity().stopService(new Intent(getContext(), Service.class));
                                startActivity(new Intent(getContext(), MainActivity.class));
                            }
                        }
                    });
                    builder.setNegativeButton("No",null);
                    builder.show();
                }else {
                    /*Globals.user = usuarios.getUsuarios().get(position);*/
                    adm.userActivo(usuarios.getUsuarios().get(position).getCodigo(),usuarios.getUsuarios().get(position).getTipo());
                    requireActivity().stopService(new Intent(getContext(),Service.class));
                    startActivity(new Intent(getContext(), MainActivity.class));
                    /*Navigation Drawer*/
                    /*switch (Globals.user.getTipo())
                    {
                        case "tutor":
                            adm.actUltTipo("tutor");
                            requireActivity().stopService(new Intent(getContext(),Service.class));
                            startActivity(new Intent(getContext(), MainActivity.class));
                            break;
                        case "estudiante":
                            adm.actUltTipo("estudiante");
                            requireActivity().stopService(new Intent(getContext(),Service.class));
                            startActivity(new Intent(getContext(), Estudiantes.class));
                            break;
                        case "profesor":
                            adm.actUltTipo("profesor");
                            requireActivity().stopService(new Intent(getContext(),Service.class));
                            startActivity(new Intent(getContext(), Profesor.class));
                            break;
                        case "director":
                            adm.actUltTipo("director");
                            startActivity(new Intent(getContext(), Director.class));
                            break;
                        case "personal":
                            adm.actUltTipo("personal");
                            requireActivity().stopService(new Intent(getContext(),Service.class));
                            startActivity(new Intent(getContext(), PersonalAdministrativo.class));
                            break;
                    }*/

                }
            }
        });
        btnListo.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"SetTextI18n", "ResourceAsColor"})
            @Override
            public void onClick(View v) {
                title.setText("Cambiar Cuenta");
                btnDelete.setVisibility(View.VISIBLE);
                btnNew.setVisibility(View.VISIBLE);
                btnListo.setVisibility(View.GONE);
                delete = false;

            }
        });
    }

    private void deleteItem(int position) {

        switch (codigos.get(position)[1]) {
            case "tutor":
                adm.deleteTutor(codigos.get(position)[0]);
                break;
            case "profesor":
                adm.deleteProfesor(codigos.get(position)[0]);
                break;
            case "estudiante":
                adm.deleteEstudiante(codigos.get(position)[0]);
                break;
            case "director":
                adm.deleteDirector(codigos.get(position)[0]);
                break;
            case "personal":
                adm.deletePersonal(codigos.get(position)[0]);
                break;
        }
    }


    private void enlaces(View vista) {
        btnNew = vista.findViewById(id.btnNewUser);
        btnDelete = vista.findViewById(id.btnDeleteUser);
        btnListo = vista.findViewById(id.btnListoafiliacion);
        title = vista.findViewById(id.textCambiarCuenta);
        lista = vista.findViewById(id.lvAfiliados);
    }

    private void llenarListas() {

        usuarios = new Usuarios(getContext());
        codigos = new ArrayList<>();
        ArrayList<String> nombres = new ArrayList<>();

        ArrayList<User> arraUser = new ArrayList<>(usuarios.getUsuarios());

        for (int i = 0 ; i < arraUser.size(); i++){

            codigos.add(new String[]{arraUser.get(i).getCodigo(),arraUser.get(i).getTipo()});
            nombres.add(arraUser.get(i).getTipo().toUpperCase()+": "+arraUser.get(i).getNombre());

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, nombres);
        lista.setAdapter(adapter);
    }
}