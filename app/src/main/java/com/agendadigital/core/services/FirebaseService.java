package com.agendadigital.core.services;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.agendadigital.MainActivity;
import com.agendadigital.R;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.shared.domain.database.FeedReaderContract;
import com.agendadigital.core.shared.domain.database.FeedReaderDbHelper;
import com.agendadigital.views.modules.chats.components.observers.MessageObservable;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class FirebaseService extends FirebaseMessagingService {

    public static String TAG = "FIREBASE_SERVICE";
    private MessageObservable messageObservable = new MessageObservable();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("newToken", s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();

    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> dataMessage = remoteMessage.getData();
        Log.d(TAG, String.format("Message data payload: %s", dataMessage.toString()));

        if (notification!= null || dataMessage.size() > 0) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String id = dataMessage.get("id");
                int messageTypeId = Integer.valueOf(dataMessage.get("messageTypeId"));
                String deviceFromId = dataMessage.get("deviceFromId");
                String destinationId = (dataMessage.get("destinationId"));
                String data = dataMessage.get("data");
                int forGroup = Integer.valueOf(dataMessage.get("forGroup"));
                MessageEntity.DestinationStatus destinationStatus = MessageEntity.DestinationStatus.Received;
                int status = Integer.valueOf(dataMessage.get("status"));
                Date createdAt = dateFormat.parse(dataMessage.get("createdAt"));
                Date sendedAt = dateFormat.parse(dataMessage.get("sendedAt"));
                long receivedAt = System.currentTimeMillis();
                String notificationBody = dataMessage.get("notificationBody");

                MessageEntity messageEntity = new MessageEntity(id, messageTypeId, deviceFromId, destinationId, data, forGroup, destinationStatus, status, createdAt, sendedAt, new Date(receivedAt));
                if (!isAppOnForeground(getApplicationContext())) {
                    showNotification(messageEntity, notificationBody);
                }
                Log.d(TAG, "Message MessageEntity: " + messageEntity.getId());
                if (!messageEntity.getDeviceFromId().isEmpty() && !messageEntity.getData().isEmpty()) {
                    save(messageEntity);
                    messageObservable.getPublisher().onNext(messageEntity);
                }
            }catch (ParseException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel(String id, String name, int importance)
    {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive
        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

    void showNotification(MessageEntity messageEntity, String notificationBody) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel("CHANNEL_1", "Example channel", NotificationManager.IMPORTANCE_MAX);
        }

        Intent appIntent = new Intent(getApplicationContext(), MainActivity.class);
        appIntent.putExtra("from", "notification");
        appIntent.putExtra("contactId", messageEntity.getDeviceFromId());
        PendingIntent pendingIntent= PendingIntent.getActivity(getApplicationContext(), 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "CHANNEL_1");
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher) // can use any other icon
                .setContentTitle(String.valueOf(messageEntity.getDeviceFromId()))
                .setContentText(messageEntity.getData())
                .setContentIntent(pendingIntent)
                .setGroup(String.valueOf(messageEntity.getDeviceFromId()))
                .setGroupSummary(true);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(notificationBody);

        notificationBuilder.setStyle(bigTextStyle);

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        //save(messageEntity);
        notificationManager.notify(String.valueOf(messageEntity.getDeviceFromId()), 10, notificationBuilder.build());
    }

    void save(MessageEntity messageEntity) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        List<MessageEntity> messages = null;
        try {
            messages = FeedReaderContract.FeedMessage.findAll(db.rawQuery(FeedReaderContract.FeedMessage.SQL_SELECT_FIND_BY_ID(messageEntity.getId()), null));
            if (messages == null || messages.size() == 0) {
                values.put(FeedReaderContract.FeedMessage._ID, messageEntity.getId());
                values.put(FeedReaderContract.FeedMessage.COL_MESSAGE_TYPE_ID, messageEntity.getMessagetypeId());
                values.put(FeedReaderContract.FeedMessage.COL_DEVICE_FROM_ID, messageEntity.getDeviceFromId());
                values.put(FeedReaderContract.FeedMessage.COL_DESTINATION_ID, messageEntity.getDestinationId());
                values.put(FeedReaderContract.FeedMessage.COL_DATA, messageEntity.getData());
                values.put(FeedReaderContract.FeedMessage.COL_FOR_GROUP, messageEntity.getForGroup());
                values.put(FeedReaderContract.FeedMessage.COL_DESTINATION_STATUS, messageEntity.getDestinationStatus().getValue());
                values.put(FeedReaderContract.FeedMessage.COL_STATUS, messageEntity.getStatus());
                values.put(FeedReaderContract.FeedMessage.COL_CREATED_AT, messageEntity.getCreatedAt().getTime());
                values.put(FeedReaderContract.FeedMessage.COL_SENDED_AT, messageEntity.getSentAt().getTime());
                values.put(FeedReaderContract.FeedMessage.COL_RECEIVED_AT, messageEntity.getReceivedAt().getTime());
                db.insert(FeedReaderContract.FeedMessage.TABLE_NAME, null, values);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}

