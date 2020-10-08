/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com.agendadigital.services;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.agendadigital.MainActivity;
import com.agendadigital.R;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.User;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;


public class Service extends android.app.Service {
    protected static final int NOTIFICATION_ID = 1337;
    private static String TAG = "Service";
    private boolean reiniciar = true;
    public Service() {
        super();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
        //mCurrentService = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "restarting Service !!");
        //counter = 0;

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(this);
        }

        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }

        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "restarting foreground");
            try {
                Notification notification = new Notification();
                startForeground(NOTIFICATION_ID, notification.setNotification(this, "Service notification", "This is the service's notification", R.drawable.ic_launcher_foreground));
                Log.i(TAG, "restarting foreground successful");
                startTimer();
            } catch (Exception e) {
                Log.e(TAG, "Error in notification " + e.getMessage());
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
        // restart the never ending service
        for (int i = 0; i < sockets.size(); i++){
            sockets.get(i).disconnect();
        }
        Intent broadcastIntent = new Intent(Global.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved called");
        Intent broadcastIntent = new Intent(Global.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
    }

    private ArrayList<Socket> sockets;

    public void startTimer() {
        sockets=new ArrayList<>();
        ArrayList<String> ips;
        AdminSQLite adm = new AdminSQLite(getApplicationContext(),"agenda",null,1);
        ips = adm.obtenerIPS();
        final User user = adm.getUserActivo();

        try {

            for (int i = 0; i < ips.size(); i++){
                IO.Options options = new IO.Options();
                options.path="/agenda/socket.io";
                sockets.add(IO.socket("http://"+ips.get(i),options));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        for (int i = 0 ; i < sockets.size(); i++){
            sockets.get(i).connect();
            final int pos = i;
            sockets.get(i).on("restart", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    stopService(new Intent(getApplicationContext(),Service.class));
                }
            });
            sockets.get(i).on("mensaje", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    reiniciar = false;
                    AdminSQLite adm = new AdminSQLite(getApplicationContext(),"agenda",null,1);
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        adm.savemsg(jsonObject.getString("mensaje"));
                        JSONObject notificacion = jsonObject.getJSONObject("mensaje");
                        crearNotificacion(notificacion.getString("mensaje"));
                        sockets.get(pos).emit("msgReceived",notificacion.get("id"));
                        Intent intent = new Intent();
                        intent.setAction("SENDMESSAGE");
                        sendBroadcast(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            sockets.get(i).on("nomsgpend", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    crearNotificacion(args[0].toString());
                    reiniciar = false;
                }
            });
            sockets.get(i).on("validar", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    sockets.get(pos).emit("codigo", user.getCodigo());
                }
            });

        }
        restart();
        super.onCreate();
    }

    private void restart() {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // acciones que se ejecutan tras los milisegundos
                    if (reiniciar){
                        stopService(new Intent(getApplicationContext(),Service.class));
                    }
                }
            }, 10000);

    }

    private void crearNotificacion(String mensaje) {
        getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        String channelID = "agenda_notif";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int priority = NotificationManager.IMPORTANCE_HIGH;
            CharSequence name = "Notificacion";
            NotificationChannel mChannel = new NotificationChannel(channelID,name,priority);
            mChannel.setDescription(mensaje);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
            mBuilder = new NotificationCompat.Builder(this, channelID).setContentIntent(pendingIntent).setAutoCancel(true);
        }
        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Notificacion")
        .setContentText(mensaje).setContentIntent(pendingIntent).setAutoCancel(true);
        mBuilder.setChannelId(channelID);
        int idNotification = 1523;
        assert notificationManager != null;
        notificationManager.notify(idNotification, mBuilder.build());
    }
}
