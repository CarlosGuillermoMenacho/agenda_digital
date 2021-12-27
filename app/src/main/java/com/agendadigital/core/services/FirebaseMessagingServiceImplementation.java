package com.agendadigital.core.services;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
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
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.modules.messages.domain.MultimediaBase;
import com.agendadigital.core.modules.messages.domain.MultimediaEntity;
import com.agendadigital.core.modules.messages.infrastructure.MessageRepository;
import com.agendadigital.core.services.messages.MessageDto;
import com.agendadigital.core.shared.infrastructure.utils.DateFormatter;
import com.agendadigital.core.shared.infrastructure.utils.DirectoryManager;
import com.agendadigital.views.modules.chats.components.observers.MessageObservable;
import com.agendadigital.views.modules.contacts.components.observers.ContactObservable;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class FirebaseMessagingServiceImplementation extends FirebaseMessagingService {

    public static String TAG = "FIREBASE_SERVICE";
    private final MessageObservable messageObservable = new MessageObservable();
    private final ContactObservable contactObservable = new ContactObservable();
    FirebaseStorage storage;
//    @Override
//    public void onNewToken(@NonNull String s) {
//        super.onNewToken(s);
//        Log.e("newToken", s);
//    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        storage = FirebaseStorage.getInstance();
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

                MessageEntity messageEntity = new MessageEntity(id, MessageEntity.MessageType.setValue(messageTypeId), deviceFromId, User.UserType.setValue(deviceFromType), destinationId, ContactEntity.ContactType.setValue(destinationType), data, forGroup, destinationState, status, createdAt, sentAt, new Date(receivedAt));
                if (messageEntity.getMessageType() != MessageEntity.MessageType.Text) {
                    JSONObject multimedia = new JSONObject(dataMessage.get("multimedia"));
                    Log.d(TAG, "onMessageReceived: " + multimedia.toString(4));
                    messageEntity.setMultimediaEntity(new MultimediaEntity(multimedia.getString("id"), multimedia.getString("messageId"), "", multimedia.getString("firebaseUri")));
                    downloadFileFromMessage(messageEntity);
                }else {
                    new MessageRepository(getApplicationContext()).insert(messageEntity);
                }
                ContactEntity contact = new ContactRepository(getApplicationContext()).updateUnreadMessagesAndLastMessage(messageEntity);
                if (!isAppOnForeground(getApplicationContext())) {
                    showNotification(messageEntity, deviceFromType, notificationBody);
                    confirmAck(messageEntity);
                }else {
                    if (messageEntity.getMessageType() == MessageEntity.MessageType.Text){
                        messageObservable.getPublisher().onNext(messageEntity);
                    }
                    contactObservable.getPublisher().onNext(contact);
                }
            } catch (Exception e) {
                Log.e(TAG, "onMessageReceived: " + e.getMessage(), e.fillInStackTrace());
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
            appIntent.putExtra("contactType", messageEntity.getDeviceFromType().getValue());
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int)System.currentTimeMillis(), appIntent, 0);

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

    private void downloadFileFromMessage(MessageEntity message) throws IOException {
        StorageReference fileToDownloadReference = storage.getReferenceFromUrl(message.getMultimediaEntity().getFirebaseUri());
        File localFile = File.createTempFile("images", "jpg");
        fileToDownloadReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            Log.d(TAG, "onSuccessFile: " + localFile.getPath());
            try {
                String pathToSave = "";
                if (message.getMessageType() ==  MessageEntity.MessageType.Image) {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.fromFile(localFile));
                    pathToSave = DirectoryManager.getPathToSave(message.getMessageType());
                    FileOutputStream out = new FileOutputStream(new File(pathToSave, message.getMultimediaEntity().getId() + ".jpg"));
                    pathToSave += message.getMultimediaEntity().getId() + ".jpg";
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                }else if (message.getMessageType() == MessageEntity.MessageType.Video) {
                    pathToSave = DirectoryManager.getPathToSave(message.getMessageType());
                    InputStream inputStream = new FileInputStream(localFile);
                    FileOutputStream out = new FileOutputStream(new File(pathToSave, message.getMultimediaEntity().getId() + ".mp4"));
                    pathToSave += fileToDownloadReference.getName();
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.flush();
                    inputStream.close();
                    out.close();
                }else if (message.getMessageType() == MessageEntity.MessageType.Document) {
                    Log.d(TAG, "onSuccessDocument: " + fileToDownloadReference.getName());
                    pathToSave = DirectoryManager.getPathToSave(message.getMessageType());
                    InputStream inputStream = new FileInputStream(localFile);
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(pathToSave, fileToDownloadReference.getName()), true);
                    pathToSave += fileToDownloadReference.getName();
                    byte[] buf = new byte[5 * 1024];
                    int len;

                    while ((len = inputStream.read(buf)) > 0) {
                        fileOutputStream.write(buf, 0, len);
                    }

                    fileOutputStream.flush();
                    inputStream.close();
                    fileOutputStream.close();
                }

                Log.d(TAG, "onSuccessSave: " + pathToSave);
                message.getMultimediaEntity().setLocalUri(pathToSave);
                 new MessageRepository(getApplicationContext()).insert(message);
                messageObservable.getPublisher().onNext(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
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

