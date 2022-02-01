package com.agendadigital.core.services;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.agendadigital.MainActivity;
import com.agendadigital.R;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.ConstantsGlobals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.clases.User;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.modules.messages.domain.MultimediaEntity;
import com.agendadigital.core.modules.messages.infrastructure.MessageRepository;
import com.agendadigital.core.services.messages.MessageDto;
import com.agendadigital.core.shared.infrastructure.utils.DateFormatter;
import com.agendadigital.core.shared.infrastructure.utils.DirectoryManager;
import com.agendadigital.core.shared.infrastructure.utils.FilesUtils;
import com.agendadigital.views.modules.chats.components.observers.MessageObservable;
import com.agendadigital.views.modules.contacts.components.observers.ContactObservable;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
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
                String groupId = Objects.requireNonNull(dataMessage.get("groupId"));
                int groupType = Integer.parseInt(Objects.requireNonNull(dataMessage.get("groupType")));
                MessageEntity.DestinationState destinationState = MessageEntity.DestinationState.Received;
                int status = Integer.parseInt(Objects.requireNonNull(dataMessage.get("state")));
                Date createdAt = DateFormatter.parse(dataMessage.get("createdAt"));
                Date sentAt = DateFormatter.parse(dataMessage.get("sentAt"));
                long receivedAt = System.currentTimeMillis();
                String notificationBody = dataMessage.get("notificationBody");

                MessageEntity messageEntity = new MessageEntity(id, MessageEntity.MessageType.setValue(messageTypeId), deviceFromId, User.UserType.setValue(deviceFromType), destinationId, ContactEntity.ContactType.setValue(destinationType), data, groupId, ContactEntity.ContactType.setValue(groupType), destinationState, status, createdAt, sentAt, new Date(receivedAt));
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
                    showNotification(messageEntity, notificationBody);
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

    void showNotification(MessageEntity messageEntity, String notificationBody) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel();
        }
        try {
            String contactId;
            int contactType;
            if (messageEntity.getGroupId().isEmpty()) {
                contactId = messageEntity.getDeviceFromId();
                contactType = messageEntity.getDeviceFromType().getValue();
            } else {
                contactId = messageEntity.getGroupId();
                contactType = messageEntity.getGroupType().getValue();
            }
            Intent appIntent = new Intent(getApplicationContext(), MainActivity.class);
            appIntent.putExtra("from", "notification");
            appIntent.putExtra("contactId", contactId);
            appIntent.putExtra("contactType", contactType);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int)System.currentTimeMillis(), appIntent, 0);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, "CHANNEL_1");
            notificationBuilder
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(new ContactRepository(getApplicationContext()).findByIdAndType(contactId, contactType).getName())
                    .setContentText(messageEntity.getData())
                    .setContentIntent(pendingIntent)
                    .setGroup(contactId)
                    .setGroupSummary(true);

            notificationBuilder.setAutoCancel(true);
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(notificationBody);

            notificationBuilder.setStyle(bigTextStyle);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.notify(contactId, 10, notificationBuilder.build());
        }catch (Exception e) {
            Log.d(TAG, "showNotification: " + e.getMessage());
        }
    }

    private void confirmAck(MessageEntity message) throws UnsupportedEncodingException, JSONException {
        JSONObject params = new JSONObject();
        MessageDto.ConfirmMessageRequest confirmMessageRequest = new MessageDto.ConfirmMessageRequest(message.getId(), message.getDestinationState().getValue(), DateFormatter.formatToDate(message.getReceivedAt()));
        params.put("message", new JSONObject(confirmMessageRequest.toJSON()));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/confirm-message", params, response -> {
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

    private void downloadFileFromMessage(MessageEntity message) {
        StorageReference fileToDownloadReference = storage.getReferenceFromUrl(message.getMultimediaEntity().getFirebaseUri());
        String filename = message.getMessageType() == MessageEntity.MessageType.Image? message.getMultimediaEntity().getId(): fileToDownloadReference.getName();
        String pathToSave = DirectoryManager.getPathToSave(message.getMessageType(), false);
        File localFile = new File(pathToSave, filename);
        fileToDownloadReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            message.getMultimediaEntity().setLocalUri(pathToSave + filename);
            new MessageRepository(getApplicationContext()).insert(message);
            messageObservable.getPublisher().onNext(message);
        }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
//        File localFile = File.createTempFile("images", "jpg");
//        fileToDownloadReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
//            Log.d(TAG, "onSuccessFile: " + localFile.getPath());
//            try {
//                String pathToSave = "";
//                if (message.getMessageType() ==  MessageEntity.MessageType.Image) {
//                    pathToSave = FilesUtils.saveImageJPEG(getApplicationContext()
//                            , localFile
//                            , message.getMultimediaEntity().getId()
//                            , DirectoryManager.getPathToSave(message.getMessageType(), false));
//                }else if (message.getMessageType() == MessageEntity.MessageType.Video) {
//                    pathToSave = FilesUtils.saveVideoMP4FromFile(getApplicationContext()
//                            , localFile
//                            , fileToDownloadReference.getName()
//                            , DirectoryManager.getPathToSave(message.getMessageType(), false));
//                }else if (message.getMessageType() == MessageEntity.MessageType.Document) {
//                    Log.d(TAG, "onSuccessDocument: " + fileToDownloadReference.getName());
//                    pathToSave = FilesUtils.saveDocument(getApplicationContext()
//                            , localFile
//                            , fileToDownloadReference.getName()
//                            , DirectoryManager.getPathToSave(message.getMessageType(), false));
//                }else if (message.getMessageType() == MessageEntity.MessageType.Audio) {
//                    Log.d(TAG, "onSuccessDocument: " + fileToDownloadReference.getName());
//
//                }
//
//                Log.d(TAG, "onSuccessSave: " + pathToSave);
//                message.getMultimediaEntity().setLocalUri(pathToSave);
//                 new MessageRepository(getApplicationContext()).insert(message);
//                messageObservable.getPublisher().onNext(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
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

