/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com.agendadigital.services;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.agendadigital.MainActivity;
import com.agendadigital.R;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.NoticationType;
import com.agendadigital.clases.User;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.itextpdf.text.pdf.codec.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;


public class Service extends android.app.Service {
    protected static final int NOTIFICATION_ID = 1337;
    private static final String TAG = "Service";
    public final  IBinder iBinder = new LocalBinder();
    private int conteo = 0;
    public Service() {
        super();
    }
    public class LocalBinder extends Binder {
        public Service getService(){
            return Service.this;
        }
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //desconectarSockets();
            startTimer();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
        IntentFilter filter = new IntentFilter("restarSockets");
        registerReceiver(broadcastReceiver,filter);
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
        return iBinder;
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
        Intent broadcastIntent = new Intent(Global.RESTART_INTENT);
        desconectarSockets();
        sendBroadcast(broadcastIntent);

    }
    public void restartService(){
        for (int i = 0; i < sockets.size(); i++){
            sockets.get(i).close();
        }
        getApplication().stopService(new Intent(getApplicationContext(), Service.class));
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved called");
        Intent broadcastIntent = new Intent(Global.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
    }

    public ArrayList<Socket> sockets;

    public void startTimer() {
        desconectarSockets();
        sockets=new ArrayList<>();
        ArrayList<String> ips;
        AdminSQLite adm = new AdminSQLite(getApplicationContext(),"agenda",null,1);

        final User user = adm.getUserActivo();
        if (user.getTipo()!=null&&user.getTipo().equals("tutor")){
           // int i = 0;
            ips = adm.obtenerIPS();
        try {

           for (int i = 0; i < ips.size(); i++){
                IO.Options options = new IO.Options();
                options.path="/agenda/socket.io";
                sockets.add(IO.socket("http://"+ips.get(i),options));
                //sockets.add(IO.socket("http://192.168.100.96:3000",options));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        for (int i = 0 ; i < sockets.size(); i++){
            if(!sockets.get(i).connected()){
                sockets.get(i).connect();
            }
            final int pos = i;
            sockets.get(i).on("restart", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    crearNotificacion(args[0].toString());
                    restartService();
                }
            });
            sockets.get(i).on("mensaje", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    AdminSQLite adm = new AdminSQLite(getApplicationContext(),"agenda",null,1);
                    JSONObject jsonObject = (JSONObject) args[0];

                    conteo = 0;
                    try {
                        JSONObject notificacion = jsonObject.getJSONObject("mensaje");
                        Cursor cursor = adm.getNotificacion(notificacion.getString("cod_est"),
                                notificacion.getString("cod_tutor"),notificacion.getString("id"));
                        if (!cursor.moveToFirst()) {
                            String json = "";
                            if (notificacion.getString("tipo").equals(Integer.toString(NoticationType.TEXT))) {
                                adm.savemsg(jsonObject.getString("mensaje"));
                                json = notificacion.toString();
                            }
                            if (notificacion.getString("tipo").equals(Integer.toString(NoticationType.IMAGEN))) {
                                json = saveImagen(notificacion);
                            }

                            String emit = notificacion.getString("emisor");

                            sockets.get(pos).emit("msgReceived", notificacion.get("id"));
                            ArrayList<String> tabs = new ArrayList<>(Globals.tabsActivos);
                            if (tabs != null && !tabs.isEmpty()) {
                                for (int i = 0; i < tabs.size(); i++) {
                                    if (tabs.get(i).equals(emit + notificacion.getString("cod_est"))) {
                                        Intent intent = new Intent();
                                        intent.setAction(tabs.get(i));
                                        intent.putExtra("mensaje", json);
                                        sendBroadcast(intent);
                                    }
                                }
                                if (tabs.get(0).equals("LISTA")) {
                                    Intent intent = new Intent();
                                    intent.setAction("LISTA");
                                    intent.putExtra("est", notificacion.getString("cod_est"));
                                    sendBroadcast(intent);
                                } else if (tabs.get(0).equals("X" + notificacion.getString("cod_est"))) {
                                    Intent intent = new Intent();
                                    intent.setAction("X" + notificacion.getString("cod_est"));
                                    intent.putExtra("mensaje", json);
                                    sendBroadcast(intent);
                                }
                            } else {
                                if (notificacion.getString("tipo").equals(Integer.toString(NoticationType.IMAGEN))){
                                    crearNotificacion("Hola has recibido un nuevo mensaje...");
                                }else {
                                    crearNotificacion(notificacion.getString("mensaje"));
                                }
                            }

                        }else {
                            sockets.get(pos).emit("msgReceived",notificacion.get("id"));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            sockets.get(i).on("msgpend", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        AdminSQLite adm = new AdminSQLite(getApplicationContext(),"agenda",null,1);
                        JSONArray listamensajes = jsonObject.getJSONArray("mensajes");
                        for (int i = 0 ; i < listamensajes.length(); i++){
                            JSONObject mensaje = listamensajes.getJSONObject(i);
                            Cursor cursor = adm.getNotificacion(mensaje.getString("cod_est"),
                                    mensaje.getString("cod_tutor"),mensaje.getString("id"));
                            if (!cursor.moveToFirst()) {
                                String json = "";
                                if (mensaje.getString("tipo").equals(Integer.toString(NoticationType.TEXT))) {
                                    adm.savemsg(mensaje.toString());
                                    json = mensaje.toString();
                                }
                                if (mensaje.getString("tipo").equals(Integer.toString(NoticationType.IMAGEN))) {
                                    json = saveImagen(mensaje);
                                }

                                String emit = mensaje.getString("emisor");

                                sockets.get(pos).emit("msgReceived", mensaje.getString("id"));
                                ArrayList<String> tabs = new ArrayList<>(Globals.tabsActivos);
                                if (tabs != null && !tabs.isEmpty()) {
                                    for (int j = 0; j < tabs.size(); j++) {
                                        if (tabs.get(j).equals(emit + mensaje.getString("cod_est"))) {
                                            Intent intent = new Intent();
                                            intent.setAction(tabs.get(j));
                                            intent.putExtra("mensaje", json);
                                            sendBroadcast(intent);
                                        }
                                    }
                                    if (tabs.get(0).equals("LISTA")) {
                                        Intent intent = new Intent();
                                        intent.setAction("LISTA");
                                        intent.putExtra("est", mensaje.getString("cod_est"));
                                        sendBroadcast(intent);
                                    } else if (tabs.get(0).equals("X" + mensaje.getString("cod_est"))) {
                                        Intent intent = new Intent();
                                        intent.setAction("X" + mensaje.getString("cod_est"));
                                        intent.putExtra("mensaje", json);
                                        sendBroadcast(intent);
                                    }
                                } else {
                                    if (mensaje.getString("tipo").equals(Integer.toString(NoticationType.IMAGEN))){
                                        crearNotificacion("Hola has recibido un nuevo mensaje...");
                                    }else {
                                        crearNotificacion(mensaje.getString("mensaje"));
                                    }
                                }
                            }else {
                                sockets.get(pos).emit("msgReceived",mensaje.get("id"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            sockets.get(i).on("nomsgpend", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    /*crearNotificacion(args[0].toString());*/
                }
            });
            sockets.get(i).on("validar", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    sockets.get(pos).emit("codigo", user.getCodigo());
                }
            });

        }
        }
        super.onCreate();
    }

    private void desconectarSockets() {
        if (sockets!=null) {
            for (int i = 0; i < sockets.size(); i++) {
                sockets.get(i).disconnect();
                sockets.get(i).off();
            }
            sockets.clear();
        }
    }


    private String saveImagen(JSONObject notificacion) throws JSONException {

        Bitmap finalBitmap = decodeBase64(notificacion.getString("mensaje"));

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject newNotification = new JSONObject();
        newNotification.put("id",notificacion.getString("id"));
        newNotification.put("cod_est",notificacion.getString("cod_est"));
        newNotification.put("mensaje",fname);
        newNotification.put("emisor",notificacion.getString("emisor"));
        newNotification.put("cod_tutor",notificacion.getString("cod_tutor"));
        newNotification.put("fecha",notificacion.getString("fecha"));
        newNotification.put("hora",notificacion.getString("hora"));
        newNotification.put("estado",notificacion.getString("estado"));
        newNotification.put("tipo",notificacion.getString("tipo"));
        newNotification.put("nombreemisor",notificacion.getString("nombreemisor"));
        AdminSQLite adm = new AdminSQLite(getApplicationContext(),"agenda",null,1);
        adm.savemsg(newNotification.toString());
        return newNotification.toString();
    }

    private static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
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
            CharSequence name = "notificacion";
            NotificationChannel mChannel = new NotificationChannel(channelID,name,priority);
            mChannel.setDescription(mensaje);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100,200,300,400,100,400,300,200,400});
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
            mBuilder = new NotificationCompat.Builder(this, channelID).setContentIntent(pendingIntent).setAutoCancel(true);
        }
        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Agenda Digital")
        .setContentText(mensaje).setContentIntent(pendingIntent).setAutoCancel(true);
        mBuilder.setChannelId(channelID);
        int idNotification = 1523;
        assert notificationManager != null;
        notificationManager.notify(idNotification, mBuilder.build());
    }
}
