package com.agendadigital.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TableLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.agendadigital.R;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.Menus;


public class WebViewPractico extends Fragment {
    private String mParam1;
    private String mParam2;
    private String codigoAlumno;
    AlertDialog.Builder builder;
    private TableLayout tableLayout;
    private String id2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        codigoAlumno = Globals.estudiante.getCodigo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_practico_alumno2, container, false);
        WebView myWebView = (WebView) vista.findViewById(R.id.webView);
        myWebView.clearCache(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.loadUrl("http://192.168.100.65/aizama/aizama/FragmentPracticoAlumno.php?codAlu=" + codigoAlumno);
        return vista;
    }
}