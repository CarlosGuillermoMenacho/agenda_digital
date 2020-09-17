package com.agendadigital.clases;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {
    @SuppressLint("StaticFieldLeak")
    private  static MySingleton mInstance;
    private RequestQueue requestQueue;
    @SuppressLint("StaticFieldLeak")
    private  static Context contex;

    private MySingleton(Context context){
        contex = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized MySingleton getInstance(Context context ){
        if (mInstance==null){
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue==null){
            requestQueue = Volley.newRequestQueue(contex.getApplicationContext());

        }
        return requestQueue;
    }

    public <T>void addToRequest(Request<T> request){
        requestQueue.add(request);

    }
}
