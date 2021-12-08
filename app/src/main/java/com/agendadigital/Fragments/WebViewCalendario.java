package com.agendadigital.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.agendadigital.R;
import com.agendadigital.clases.Globals;

public class WebViewCalendario extends Fragment {
    private String codigoAlumno;
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
        View vista = inflater.inflate(R.layout.fragment_web_view_calendario, container, false);
        WebView myWebView = (WebView) vista.findViewById(R.id.webview);
        myWebView.clearCache(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.loadUrl("http://192.168.100.65/aizama/aizama/vista/calendarioWebView.php?codAlu=" + codigoAlumno);
        return vista;
    }



}