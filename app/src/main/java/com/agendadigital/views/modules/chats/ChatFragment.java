package com.agendadigital.views.modules.chats;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.agendadigital.MainActivity;
import com.agendadigital.R;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.modules.messages.domain.MessageBase;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.modules.messages.domain.MultimediaEntity;
import com.agendadigital.core.modules.messages.infrastructure.MessageRepository;
import com.agendadigital.core.services.messages.MessageDto;
import com.agendadigital.core.services.messages.MessageService;
import com.agendadigital.core.shared.infrastructure.utils.DateFormatter;
import com.agendadigital.core.shared.infrastructure.utils.DirectoryManager;
import com.agendadigital.core.shared.infrastructure.utils.FilesUtils;
import com.agendadigital.views.modules.chats.components.adapters.MessageAdapter;
import com.agendadigital.views.modules.chats.components.fab.SendFloatingActionButton;
import com.agendadigital.views.modules.chats.components.observers.MessageObservable;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.User;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChatFragment extends Fragment {

    private final String TAG = "ChatFragment";
    private User currentUser;
    private View view;
    private ContactEntity currentContact;

    private RecyclerView rvMessages;
    private EditText etTextMessageToSend;
    private ImageButton btAttach;

    private MessageAdapter messageAdapter;
    private final MessageObservable messageObservable = new MessageObservable();
    private List<MessageEntity> messageEntityList = new ArrayList<>();

    private MessageService messageService;

    private MessageRepository messageRepository;
    private ContactRepository contactRepository;

    private LinearLayout messageLayout;
    private SendFloatingActionButton sendFloatingActionButton;
    private RecordView rvRecordView;
    private RecordButton rbRecord;
    private MediaRecorder mediaRecorder;
    private String audioPath;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chat, container, false);
        currentUser = Globals.user;
        init();
        Bundle bundle = getArguments();
        if (bundle != null) {
            currentContact = (ContactEntity) bundle.getSerializable("contact");
            contactRepository.resetUnreadMessages(currentContact);
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(currentContact.toString());
        }
        if(ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(view.getContext(), "Sin permisos de lectura", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 1);
        } else {
            Toast.makeText(view.getContext(), "Permisos de lectura", Toast.LENGTH_SHORT).show();
        }
        initViews();
        return view;
    }

    private void init(){
        messageRepository = new MessageRepository(view.getContext());
        contactRepository = new ContactRepository(view.getContext());
        messageService = new MessageService(view.getContext());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private void initViews() {
        etTextMessageToSend = view.findViewById(R.id.etTextMessageToSend);
        btAttach = view.findViewById(R.id.btAttach);

        rvMessages = view.findViewById(R.id.rvMessagesList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);

        sendFloatingActionButton = view.findViewById(R.id.sendButton);
        sendFloatingActionButton.setIconDrawable(getResources().getDrawable(R.drawable.ic_send_white_24dp));

        rvRecordView = view.findViewById(R.id.recordView);
        rvRecordView.setCounterTimeColor(Color.BLACK);
        rbRecord = view.findViewById(R.id.recordButton);
        rbRecord.setRecordView(rvRecordView);
        rbRecord.setListenForRecord(false);

        messageLayout = view.findViewById(R.id.messageLayout);

        initTextMessageListener();
        initButtonAttachListener();
        initRecyclerView();
        initMessageObserver();
        initSentButtonListener();
        initRecordButtonListener();
    }

    private void initTextMessageListener() {
        etTextMessageToSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    sendFloatingActionButton.setVisibility(View.GONE);
                    rbRecord.setVisibility(View.VISIBLE);
                } else {
                    rbRecord.setVisibility(View.GONE);
                    sendFloatingActionButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initButtonAttachListener() {
        btAttach.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), v);
            popupMenu.setOnMenuItemClickListener(item -> {
                Intent pickIntent;
                Intent chooserIntent = null;
                int activityResult = 0;
                switch (item.getItemId()) {
                    case R.id.attachDocument:
                        pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        pickIntent.setType("*/*");
                        pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {
                                "application/msword",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                "text/*",
                                "application/pdf",
                                "application/vnd.ms-excel"
                        });
                        chooserIntent = Intent.createChooser(pickIntent, "Select a document");
                        activityResult = MessageEntity.MessageType.Document.getValue();
                        break;
                    case R.id.attachImage:
                        pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");
                        chooserIntent = Intent.createChooser(pickIntent, "Select a image");
                        activityResult = MessageEntity.MessageType.Image.getValue();
                        break;
                    case R.id.attachVideo:
                        pickIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("video/*");
                        chooserIntent = Intent.createChooser(pickIntent, "Select a video");
                        activityResult = MessageEntity.MessageType.Video.getValue();
                        break;
                }
                startActivityForResult(chooserIntent, activityResult);
                return ChatFragment.super.onOptionsItemSelected(item);
            });
            popupMenu.inflate(R.menu.popup_attachments);
            popupMenu.show();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        assert data != null;

        Uri selectedFile = data.getData();
        if (selectedFile == null || selectedFile.getPath() == null) {
            Toast.makeText(view.getContext(), "Ocurrió un error al seleccionar el archivo.", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(selectedFile.getPath());
        Log.d(TAG, "onActivityResultSelectedFile: " + file.getPath());

        Date currentTime = new Date(System.currentTimeMillis());
        MessageEntity messageEntity = new MessageEntity(UUID.randomUUID().toString()
                , MessageEntity.MessageType.Image
                , currentUser.getCodigo()
                , currentUser.getTipo()
                , currentContact.getId()
                , currentContact.getContactType()
                , ""
                , MessageEntity.isForGroupMessage(currentContact)
                , MessageEntity.DestinationState.Create
                , 1, currentTime, currentTime, null  );

        StorageReference fileReference = null;
        String filePath = "";

        if (requestCode == MessageEntity.MessageType.Image.getValue()) {
            try {
                messageEntity.setMessageType(MessageEntity.MessageType.Image);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), selectedFile);
                File bitmapFile = FilesUtils.bitmapToFile(view.getContext(), bitmap, file.getName());
                filePath = FilesUtils.saveImageJPEG(view.getContext(), bitmapFile, file.getName(), DirectoryManager.getPathToSave(MessageEntity.MessageType.Image, true));
                Log.d(TAG, "onActivityResultImage: " + filePath);
                fileReference = storageReference.child("images/" + file.getName());
            }catch (Exception e) {
                Log.e(TAG, "onActivityResultImage: ", e.fillInStackTrace());
            }
        } else if (requestCode == MessageEntity.MessageType.Video.getValue()) {
            try {
                messageEntity.setMessageType(MessageEntity.MessageType.Video);
                filePath = FilesUtils.saveVideoMP4FromUri(view.getContext(), selectedFile, file.getName(), DirectoryManager.getPathToSave(MessageEntity.MessageType.Video, true));//DirectoryManager.getPathToSave(MessageEntity.MessageType.Video, true) + file.getName() + ".mp4";
                Log.d(TAG, "onActivityResultVideo: " + filePath);
                fileReference = storageReference.child("videos/" + file.getName());
            }catch (Exception e) {
                Log.e(TAG, "onActivityResult: ", e.fillInStackTrace());
            }
        } else if (requestCode == MessageEntity.MessageType.Document.getValue()) {
            try {
            messageEntity.setMessageType(MessageEntity.MessageType.Document);
            filePath = FilesUtils.saveDocumentFromUri(view.getContext(), selectedFile, file.getName(), DirectoryManager.getPathToSave(MessageEntity.MessageType.Document, true));
            Log.d(TAG, "onActivityResultDoc: " + filePath);
            fileReference = storageReference.child("documents/" + file.getName());
            }catch (Exception e) {
                Log.e(TAG, "onActivityResult: ", e.fillInStackTrace());
            }
        }
        MultimediaEntity multimediaEntity = new MultimediaEntity(UUID.randomUUID().toString(), messageEntity.getId(), filePath, "");
        sendMultimediaMessage(fileReference, selectedFile, messageEntity, multimediaEntity);
//        UploadTask uploadTask = fileReference.putFile(selectedFile);

//        StorageReference finalFileReference = fileReference;
//        uploadTask.continueWithTask(task -> {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//                return finalFileReference.getDownloadUrl();
//            }).addOnCompleteListener(task -> {
//                Log.d(TAG, "onComplete: " + task.isSuccessful());
//                if (!task.isSuccessful()) {
//                    Log.d(TAG, "onCompleteError: " + task.getException());
//                    Toast.makeText(view.getContext(), "No se pudo subir el archivo.", Toast.LENGTH_SHORT).show();
//                } else {
//                    Uri downloadUri = task.getResult();
//                    multimediaEntity.setFirebaseUri(downloadUri.toString());
//
//                    messageEntity.setMultimediaEntity(multimediaEntity);
//                    messageRepository.insert(messageEntity);
//                    messageAdapter.add(messageEntity);
//
////                    JSONObject params = new JSONObject();
////                    try {
////                        MessageDto.SendMessageRequest sendMessageRequest = new MessageDto.SendMessageRequest(messageEntity.getMessageType().getValue()
////                                , messageEntity.getDeviceFromId()
////                                , messageEntity.getDeviceFromType().getValue()
////                                , messageEntity.getDestinationId()
////                                , messageEntity.getDestinationType().getValue()
////                                , messageEntity.getData(), messageEntity.getForGroup(), messageEntity.getCreatedAt());
////
////                        sendMessageRequest.setMultimedia(new MultimediaDto.SendMultimediaRequest(multimediaEntity.getId(), multimediaEntity.getFirebaseUri()));
////
////                        Log.d(TAG, "MessageSent: " + sendMessageRequest.toJSON());
////                        params.put("message", new JSONObject(sendMessageRequest.toJSON()));
////
////
////                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/send-message", params, response -> {
//                    messageService.sendMultimediaMessage(messageEntity, multimediaEntity, response -> {
//                        try {
//                            MessageDto.SendMessageResponse sendMessageResponse = new Gson().fromJson(response.getString("message"),
//                                    new TypeToken<MessageDto.SendMessageResponse>() {}
//                                            .getType());
//
//                            ContentValues contentValues = new ContentValues();
//                            contentValues.put(MessageBase._ID, sendMessageResponse.getId());
//                            contentValues.put(MessageBase.COL_DESTINATION_STATE, MessageEntity.DestinationState.Sent.getValue());
//                            contentValues.put(MessageBase.COL_SENT_AT, currentTime.getTime());
//                            messageRepository.update(contentValues, MessageBase._ID + "= ?", new String[] { messageEntity.getId() });
//
//                            messageEntity.setDestinationState(MessageEntity.DestinationState.Sent);
//                            messageAdapter.updateDestinationState(messageEntity);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        Log.d(TAG, "onResponse: " + response);
//                    }, error -> {
//                        String body;
//                        String statusCode = String.valueOf(error.networkResponse.statusCode);
//                        if(error.networkResponse.data!=null) {
//                            body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
//                            Log.d(TAG, "SendMultimediaonErrorResponse: " + body);
//                            Toast.makeText(getContext(), statusCode + ":" + body, Toast.LENGTH_SHORT).show();
//                        }
//                    });
////                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
////                        MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
//                }
//            });
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initRecyclerView(){
        try {
            messageEntityList = messageRepository.findAll(currentContact.getId());
            messageAdapter = new MessageAdapter(messageEntityList);
            for (MessageEntity message: messageAdapter.getMessageEntities()) {
                if ( (message.getDestinationId().equals(currentUser.getCodigo()) || message.getForGroup() == 1)
                        && message.getDestinationState() == MessageEntity.DestinationState.Received){
                    confirmAck(message);
                }
            }
            messageAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(TAG, "initRecyclerView: " + e.getMessage());
            e.printStackTrace();
        }
        rvMessages.setAdapter(messageAdapter);
        rvMessages.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    rvMessages.postDelayed(() ->
                            rvMessages.smoothScrollToPosition(messageAdapter.getItemCount() - 1), 100);
                }
            }
        });
    }

    private void initMessageObserver(){
        try {
            messageObservable
                .getNotificationObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(MessageEntity messageEntity) {
                        if(messageEntity.getDestinationId().equals(currentUser.getCodigo()) || messageEntity.getForGroup() == 1){
                            try {
                                Log.d(TAG, "onNext: " + messageEntity.toJSON());
                                confirmAck(messageEntity);
                                messageAdapter.add(messageEntity);
                            } catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                            Log.d(TAG, "onComplete: ");
                        }
                });
        }catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, e.getMessage());
        }
    }

    private void initSentButtonListener(){
        sendFloatingActionButton.setOnClickListener(v -> {
            Date currentTime = new Date(System.currentTimeMillis());
            if(!etTextMessageToSend.getText().toString().trim().isEmpty()) {
                MessageEntity messageSend = new MessageEntity(UUID.randomUUID().toString(),
                        MessageEntity.MessageType.Text,
                        currentUser.getCodigo(),
                        currentUser.getTipo(),
                        currentContact.getId(),
                        currentContact.getContactType(),
                        etTextMessageToSend.getText().toString(),
                        MessageEntity.isForGroupMessage(currentContact),
                        MessageEntity.DestinationState.Create,
                        1,
                        currentTime,
                        currentTime,
                        null);

//                JSONObject params = new JSONObject();
//                try {
//                    MessageDto.SendMessageRequest sendMessageRequest = new MessageDto.SendMessageRequest(messageSend.getMessageType().getValue()
//                            , messageSend.getDeviceFromId()
//                            , messageSend.getDeviceFromType().getValue()
//                            , messageSend.getDestinationId()
//                            , messageSend.getDestinationType().getValue()
//                            , messageSend.getData(), messageSend.getForGroup(), messageSend.getCreatedAt());
//
//                    Log.d(TAG, "MessageSent: " + sendMessageRequest.toJSON());
//                    params.put("message", new JSONObject(sendMessageRequest.toJSON()));

//                messageSend.setId(UUID.randomUUID().toString());
                messageRepository.insert(messageSend);
                messageAdapter.add(messageSend);
                sendMessage(messageSend);
//                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/send-message", params, response -> {
//                messageService.sendMessage(messageSend, response ->{
//                    try {
//                        MessageDto.SendMessageResponse sendMessageResponse = new Gson().fromJson(response.getString("message"),
//                                                                                                new TypeToken<MessageDto.SendMessageResponse>() {}
//                                                                                                    .getType());
//
//                        ContentValues contentValues = new ContentValues();
//                        contentValues.put(MessageBase._ID, sendMessageResponse.getId());
//                        contentValues.put(MessageBase.COL_DESTINATION_STATE, MessageEntity.DestinationState.Sent.getValue());
//                        contentValues.put(MessageBase.COL_SENT_AT, DateFormatter.parse(sendMessageResponse.getSentAt()).getTime());
//                        messageRepository.update(contentValues, MessageBase._ID + "= ?", new String[] { messageSend.getId() });
//
//                        messageSend.setDestinationState(MessageEntity.DestinationState.Sent);
//                        messageAdapter.updateDestinationState(messageSend);
//                    } catch (JSONException | ParseException e) {
//                        e.printStackTrace();
//                    }
//                    Log.d(TAG, "onResponse: " + response);
//                }, error -> {
//                    String body;
//                    String statusCode = String.valueOf(error.networkResponse.statusCode);
//                    if(error.networkResponse.data!=null) {
//                        body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
//                        Log.d(TAG, "sendButtononErrorResponse: " + body);
//                        Toast.makeText(getContext(), statusCode + ":" + body, Toast.LENGTH_SHORT).show();
//                    }
//                });
//                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                    MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);

//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                etTextMessageToSend.setText("");
            }
        });
    }

    private void initRecordButtonListener() {
        rbRecord.setOnClickListener(view -> {
            rbRecord.setListenForRecord(true);
        });

        rvRecordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                setUpRecording();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                messageLayout.setVisibility(View.GONE);
                rvRecordView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists())
                    file.delete();
                rvRecordView.setVisibility(View.GONE);
                messageLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish(long recordTime) {
                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rvRecordView.setVisibility(View.GONE);
                messageLayout.setVisibility(View.VISIBLE);

                Date currentTime = new Date(System.currentTimeMillis());
                MessageEntity messageEntity = new MessageEntity(UUID.randomUUID().toString()
                        , MessageEntity.MessageType.Audio
                        , currentUser.getCodigo()
                        , currentUser.getTipo()
                        , currentContact.getId()
                        , currentContact.getContactType()
                        , ""
                        , MessageEntity.isForGroupMessage(currentContact)
                        , MessageEntity.DestinationState.Create
                        , 1, currentTime, currentTime, null  );

                File audioFile = new File(audioPath);
                MultimediaEntity multimediaEntity = new MultimediaEntity(UUID.randomUUID().toString(), messageEntity.getId(), audioPath, "");
                StorageReference audioReference = storageReference.child("audios/" + audioFile.getName());
                sendMultimediaMessage(audioReference, Uri.fromFile(audioFile), messageEntity, multimediaEntity);
            }

            @Override
            public void onLessThanSecond() {
                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists())
                    file.delete();
                rvRecordView.setVisibility(View.GONE);
                messageLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setUpRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        audioPath = DirectoryManager.getPathToSave(MessageEntity.MessageType.Audio, true) + System.currentTimeMillis() + ".3gp";
        mediaRecorder.setOutputFile(audioPath);
    }

    private void confirmAck(MessageEntity message) throws UnsupportedEncodingException, JSONException {
        message.setDestinationState(MessageEntity.DestinationState.Read);
//        JSONObject params = new JSONObject();
//        MessageDto.ConfirmMessageRequest confirmMessageRequest = new MessageDto.ConfirmMessageRequest(message.getId(), message.getDestinationState().getValue(), DateFormatter.formatToDate(message.getReceivedAt()));
//        params.put("message", new JSONObject(confirmMessageRequest.toJSON()));
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/confirm-message", params, response -> {
        messageService.confirmAckMessage(message, response -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MessageBase.COL_DESTINATION_STATE, message.getDestinationState().getValue());
            messageRepository.update(contentValues, MessageBase._ID + "= ?", new String[] { message.getId() });
            contactRepository.resetUnreadMessages(currentContact);
        }, error -> {
            String body;
            String statusCode = String.valueOf(error.networkResponse.statusCode);
            if(error.networkResponse.data!=null) {
                body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                Log.d(TAG, "ackonErrorResponse: " + body);
                Toast.makeText(getContext(), statusCode + ":" + body, Toast.LENGTH_SHORT).show();
            }
        });
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);
    }

    private void sendMessage(MessageEntity messageToSend) {
        messageService.sendMessage(messageToSend, response ->{
            try {
                MessageDto.SendMessageResponse sendMessageResponse = new Gson().fromJson(response.getString("message"),
                        new TypeToken<MessageDto.SendMessageResponse>() {}
                                .getType());
                messageToSend.setDestinationState(MessageEntity.DestinationState.Sent);

                ContentValues contentValues = new ContentValues();
                contentValues.put(MessageBase._ID, sendMessageResponse.getId());
                contentValues.put(MessageBase.COL_DESTINATION_STATE, messageToSend.getDestinationState().getValue());
                contentValues.put(MessageBase.COL_SENT_AT, DateFormatter.parse(sendMessageResponse.getSentAt()).getTime());

                messageRepository.update(contentValues, MessageBase._ID + "= ?", new String[] { messageToSend.getId() });
                messageAdapter.updateDestinationState(messageToSend);
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onResponse: " + response);
        }, error -> {
            String body;
            String statusCode = String.valueOf(error.networkResponse.statusCode);
            if(error.networkResponse.data!=null) {
                body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                Log.d(TAG, "sendButtononErrorResponse: " + body);
                Toast.makeText(getContext(), statusCode + ":" + body, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMultimediaMessage(StorageReference storageReference, Uri fileToSend, MessageEntity messageToSend, MultimediaEntity multimediaToSend) {
        UploadTask uploadTask = storageReference.putFile(fileToSend);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            Log.d(TAG, "onComplete: " + task.isSuccessful());
            if (!task.isSuccessful()) {
                Log.d(TAG, "onCompleteError: " + task.getException());
                Toast.makeText(view.getContext(), "No se pudo subir el archivo.", Toast.LENGTH_SHORT).show();
            } else {
                Uri downloadUri = task.getResult();
                multimediaToSend.setFirebaseUri(downloadUri.toString());

                messageToSend.setMultimediaEntity(multimediaToSend);
                messageRepository.insert(messageToSend);
                messageAdapter.add(messageToSend);
                sendMessage(messageToSend);
//                    JSONObject params = new JSONObject();
//                    try {
//                        MessageDto.SendMessageRequest sendMessageRequest = new MessageDto.SendMessageRequest(messageEntity.getMessageType().getValue()
//                                , messageEntity.getDeviceFromId()
//                                , messageEntity.getDeviceFromType().getValue()
//                                , messageEntity.getDestinationId()
//                                , messageEntity.getDestinationType().getValue()
//                                , messageEntity.getData(), messageEntity.getForGroup(), messageEntity.getCreatedAt());
//
//                        sendMessageRequest.setMultimedia(new MultimediaDto.SendMultimediaRequest(multimediaEntity.getId(), multimediaEntity.getFirebaseUri()));
//
//                        Log.d(TAG, "MessageSent: " + sendMessageRequest.toJSON());
//                        params.put("message", new JSONObject(sendMessageRequest.toJSON()));
//
//
//                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/send-message", params, response -> {
//                messageService.sendMultimediaMessage(messageToSend, multimediaToSend, response -> {
//                    try {
//                        MessageDto.SendMessageResponse sendMessageResponse = new Gson().fromJson(response.getString("message"),
//                                new TypeToken<MessageDto.SendMessageResponse>() {}
//                                        .getType());
//
//                        ContentValues contentValues = new ContentValues();
//                        contentValues.put(MessageBase._ID, sendMessageResponse.getId());
//                        contentValues.put(MessageBase.COL_DESTINATION_STATE, MessageEntity.DestinationState.Sent.getValue());
//                        contentValues.put(MessageBase.COL_SENT_AT, messageToSend.getSentAt().getTime());
//                        messageRepository.update(contentValues, MessageBase._ID + "= ?", new String[] { messageToSend.getId() });
//
//                        messageToSend.setDestinationState(MessageEntity.DestinationState.Sent);
//                        messageAdapter.updateDestinationState(messageToSend);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Log.d(TAG, "onResponse: " + response);
//                }, error -> {
//                    String body;
//                    String statusCode = String.valueOf(error.networkResponse.statusCode);
//                    if(error.networkResponse.data!=null) {
//                        body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
//                        Log.d(TAG, "SendMultimediaonErrorResponse: " + body);
//                        Toast.makeText(getContext(), statusCode + ":" + body, Toast.LENGTH_SHORT).show();
//                    }
//                });
//                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                        MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
            }
        });
    }
}
