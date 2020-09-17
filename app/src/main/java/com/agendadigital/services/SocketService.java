package com.agendadigital.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SocketService extends Service {
    private Socket socket;
    private Handler handler;
    private List<String[]> MessageList;

    @Override
    public void onCreate() {
        handler = new Handler();
        try {
            IO.Options options = new IO.Options();
            options.path = "http://192.168.100.40:4000";
            socket = IO.socket("",options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.connect();
        MessageList = new ArrayList<>();
        super.onCreate();
        socket.on("newMessage", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Objects.requireNonNull(SocketService.this).runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            int id = data.getInt("id");
                            String message = data.getString("message");
                            MessageList.add(new String[]{Integer.toString(id),message});
                            issueNotification();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    void issueNotification()
    {
        Toast.makeText(getApplicationContext(),MessageList.get(0)[1],Toast.LENGTH_SHORT).show();
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel();
        }
        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this, "CHANNEL_1");
        notification
                .setSmallIcon(android.R.mipmap.sym_def_app_icon) // can use any other icon
                .setContentTitle("Nuevo Mensaje: "+MessageList.get(0)[0])
                .setContentText(MessageList.get(0)[0])
                .setNumber(MessageList.size());
        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(1, notification.build());*/
    }
/*
    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel()
    {
        NotificationChannel channel = new NotificationChannel("CHANNEL_1", "Example channel", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive
        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }*/
}
