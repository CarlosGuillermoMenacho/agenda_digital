package com.agendadigital.core.services;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.agendadigital.MainActivity;
import com.agendadigital.R;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.ConstantsGlobals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.clases.User;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.modules.messages.domain.MessageBase;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.modules.messages.infrastructure.MessageRepository;
import com.agendadigital.core.services.messages.MessageDto;
import com.agendadigital.core.shared.infrastructure.utils.DateFormatter;
import com.agendadigital.views.modules.chats.components.observers.MessageObservable;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class FirebaseService extends FirebaseMessagingService {

    public static String TAG = "FIREBASE_SERVICE";
    private final MessageObservable messageObservable = new MessageObservable();

//    @Override
//    public void onNewToken(@NonNull String s) {
//        super.onNewToken(s);
//        Log.e("newToken", s);
//    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> dataMessage = remoteMessage.getData();
        Log.d(TAG, String.format("Message data payload: %s", dataMessage.toString()));

        if (notification!= null || dataMessage.size() > 0) {
            try {
                String id = dataMessage.get("id");
                int messageTypeId = Integer.parseInt(Objects.requireNonNull(dataMessage.get("messageType")));
                String deviceFromId = dataMessage.get("deviceFromId");
                int deviceFromType = Integer.parseInt(Objects.requireNonNull(dataMessage.get("deviceFromType")));
                String destinationId = (dataMessage.get("destinationId"));
                int destinationType = Integer.parseInt(Objects.requireNonNull(dataMessage.get("destinationType")));
                String data = dataMessage.get("data");
                int forGroup = Integer.parseInt(Objects.requireNonNull(dataMessage.get("forGroup")));
                MessageEntity.DestinationState destinationState = MessageEntity.DestinationState.Received;
                int status = Integer.parseInt(Objects.requireNonNull(dataMessage.get("state")));
                Date createdAt = DateFormatter.parse(dataMessage.get("createdAt"));
                Date sentAt = DateFormatter.parse(dataMessage.get("sentAt"));
                long receivedAt = System.currentTimeMillis();
                String notificationBody = dataMessage.get("notificationBody");

                MessageEntity messageEntity = new MessageEntity(id, messageTypeId, deviceFromId, User.UserType.setValue(deviceFromType), destinationId, ContactEntity.ContactType.setValue(destinationType), data, forGroup, destinationState, status, createdAt, sentAt, new Date(receivedAt));
                if (!isAppOnForeground(getApplicationContext())) {
                    showNotification(messageEntity, deviceFromType, notificationBody);
                    confirmAck(messageEntity);
                }
                Log.d(TAG, "Message MessageEntity: " + messageEntity.getId());
                if (!messageEntity.getDeviceFromId().isEmpty() && !messageEntity.getData().isEmpty()) {
                    new MessageRepository(getApplicationContext()).insert(messageEntity);
                    messageObservable.getPublisher().onNext(messageEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel()
    {
        NotificationChannel channel = new NotificationChannel("CHANNEL_1", "Example channel", NotificationManager.IMPORTANCE_HIGH);
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive
        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

    void showNotification(MessageEntity messageEntity, int deviceFromType, String notificationBody) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel();
        }
        try {
            Intent appIntent = new Intent(getApplicationContext(), MainActivity.class);
            appIntent.putExtra("from", "notification");
            appIntent.putExtra("contactId", messageEntity.getDeviceFromId());
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, "CHANNEL_1");
            notificationBuilder
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(new ContactRepository(getApplicationContext()).findByIdAndType(messageEntity.getDeviceFromId(), deviceFromType).getName())
                    .setContentText(messageEntity.getData())
                    .setContentIntent(pendingIntent)
                    .setGroup(String.valueOf(messageEntity.getDeviceFromId()))
                    .setGroupSummary(true);

            notificationBuilder.setAutoCancel(true);
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(notificationBody);

            notificationBuilder.setStyle(bigTextStyle);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.notify(String.valueOf(messageEntity.getDeviceFromId()), 10, notificationBuilder.build());
        }catch (Exception e) {
            Log.d(TAG, "showNotification: " + e.getMessage());
        }
    }
    private void confirmAck(MessageEntity message) throws UnsupportedEncodingException, JSONException {
        JSONObject params = new JSONObject();
        MessageDto.ConfirmMessageRequest confirmMessageRequest = new MessageDto.ConfirmMessageRequest(message.getId(), message.getDestinationState().getValue(), DateFormatter.format(message.getReceivedAt()));
        params.put("message", new JSONObject(confirmMessageRequest.toJSON()));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/send-message", params, response -> {
            Log.d(TAG, "confirmAck: " + response);
        }, error -> {
            String body;
            String statusCode = String.valueOf(error.networkResponse.statusCode);
            if (error.networkResponse.data != null) {
                body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                Log.d(TAG, "onErrorResponse: " + body);
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getApplicationContext()).addToRequest(jsonObjectRequest);
    }
//    void save(MessageEntity messageEntity) {
//        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(getApplicationContext());
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        List<MessageEntity> messages = null;
//        try {
//            messages = FeedReaderContract.FeedMessage.findAll(db.rawQuery(FeedReaderContract.FeedMessage.SQL_SELECT_FIND_BY_ID(messageEntity.getId()), null));
//            if (messages == null || messages.size() == 0) {
//                values.put(FeedReaderContract.FeedMessage._ID, messageEntity.getId());
//                values.put(FeedReaderContract.FeedMessage.COL_MESSAGE_TYPE_ID, messageEntity.getMessagetypeId());
//                values.put(FeedReaderContract.FeedMessage.COL_DEVICE_FROM_ID, messageEntity.getDeviceFromId());
//                values.put(FeedReaderContract.FeedMessage.COL_DESTINATION_ID, messageEntity.getDestinationId());
//                values.put(FeedReaderContract.FeedMessage.COL_DATA, messageEntity.getData());
//                values.put(FeedReaderContract.FeedMessage.COL_FOR_GROUP, messageEntity.getForGroup());
//                values.put(FeedReaderContract.FeedMessage.COL_DESTINATION_STATUS, messageEntity.getDestinationState().getValue());
//                values.put(FeedReaderContract.FeedMessage.COL_STATE, messageEntity.getState());
//                values.put(FeedReaderContract.FeedMessage.COL_CREATED_AT, messageEntity.getCreatedAt().getTime());
//                values.put(FeedReaderContract.FeedMessage.COL_SENT_AT, messageEntity.getSentAt().getTime());
//                values.put(FeedReaderContract.FeedMessage.COL_RECEIVED_AT, messageEntity.getReceivedAt().getTime());
//                db.insert(FeedReaderContract.FeedMessage.TABLE_NAME, null, values);
//        }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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

