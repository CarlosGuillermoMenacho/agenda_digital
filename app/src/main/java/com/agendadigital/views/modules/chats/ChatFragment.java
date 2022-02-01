package com.agendadigital.views.modules.chats;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
    private ImageButton btCamera;
    private final int CAMERA_REQUEST = 1001;

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
        btCamera = view.findViewById(R.id.btCamera);

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
        initButtonCameraListener();
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
                Intent pickIntent = null;
                Intent chooserIntent = null;
                int activityResult = 0;
                switch (item.getItemId()) {
                    case R.id.attachDocument:
                        pickIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        pickIntent.setType("*/*");
                        pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {
                                "text/plain",
                                "application/msword",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                "application/vnd.ms-excel",
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                "application/vnd.ms-powerpoint",
                                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                                "application/pdf"
                        });
                        chooserIntent = Intent.createChooser(pickIntent, "Select a document");
                        activityResult = MessageEntity.MessageType.Document.getValue();
                        break;
                    case R.id.attachImage:
                        pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        pickIntent.setType("image/*");
                        pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String [] {
                                "image/jpeg",
                                "image/png"
                        });
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
                startActivityForResult(chooserIntent,activityResult);
                return ChatFragment.super.onOptionsItemSelected(item);
            });
            popupMenu.inflate(R.menu.popup_attachments);
            popupMenu.show();
        });
    }

    private void initButtonCameraListener() {
        btCamera.setOnClickListener(click -> {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null)
            return;

        Date currentTime = new Date(System.currentTimeMillis());
        MessageEntity messageEntity = new MessageEntity(
                UUID.randomUUID().toString()
                , MessageEntity.MessageType.Image
                , currentUser.getCodigo()
                , currentUser.getTipo()
                , currentContact.getId()
                , currentContact.getContactType()
                , ""
                , MessageEntity.getGroupId(currentContact)
                , MessageEntity.getGroupId(currentContact).isEmpty()? ContactEntity.ContactType.None: currentContact.getContactType()
                , MessageEntity.DestinationState.Create
                , 1, currentTime, currentTime, null  );

        Uri selectedFile = null;
        StorageReference fileReference = null;
        String filePath = "";
        if (requestCode == CAMERA_REQUEST) {
            try {
                messageEntity.setMessageType(MessageEntity.MessageType.Image);
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                String filename = "Photo".concat(String.valueOf(System.currentTimeMillis()));
                File bitmapFile = FilesUtils.bitmapToFile(view.getContext(), bitmap, filename);
                selectedFile = Uri.fromFile(bitmapFile);
                filePath = FilesUtils.saveFileFromUri(view.getContext(), selectedFile, bitmapFile.getName(), DirectoryManager.getPathToSave(MessageEntity.MessageType.Image, true));
                Log.d(TAG, "onActivityResultCamera: " + filePath);
                fileReference = storageReference.child("images/" + filename);
            }catch (Exception e) {
                Log.e(TAG, "onActivityResultImage: ", e.fillInStackTrace());
            }
        } else {
            selectedFile = data.getData();
            if (selectedFile == null || selectedFile.getPath() == null) {
                Toast.makeText(view.getContext(), "Ocurrió un error al seleccionar el archivo.", Toast.LENGTH_SHORT).show();
                return;
            }

            File file = new File(selectedFile.getPath());

            if(file.getAbsolutePath() != null){
                String filename;
                Cursor cursor = view.getContext().getContentResolver().query(selectedFile,null,null,null,null);

                if(cursor == null)
                    filename=selectedFile.getPath();
                else{
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    filename = cursor.getString(idx);
                    cursor.close();
                }

                String extension = filename.substring(filename.lastIndexOf("."));
                try {
                    MessageEntity.MessageType messageType = MessageEntity.MessageType.setValue(requestCode);
                    if (!FilesUtils.validateExtension(extension,messageType)) {
                        Toast.makeText(view.getContext(), "Seleccione el formato correcto", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    messageEntity.setMessageType(messageType);
                    filePath = FilesUtils.saveFileFromUri(view.getContext(), selectedFile, filename, DirectoryManager.getPathToSave(messageEntity.getMessageType(), true));
                    fileReference = storageReference.child(messageEntity.getMessageType().toString() + "/" + file.getName());
                }catch(Exception e) {
                    Log.e(TAG, "onActivityResult: ", e.fillInStackTrace());
                }
            } else {
                return;
            }
        }
        MultimediaEntity multimediaEntity = new MultimediaEntity(UUID.randomUUID().toString(), messageEntity.getId(), filePath, "");
        sendMultimediaMessage(fileReference, selectedFile, messageEntity, multimediaEntity);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initRecyclerView(){
        try {
            if (currentContact.isGroup()) {
                messageEntityList = messageRepository.findAllForGroup(currentContact.getId(), currentContact.getContactType().getValue());
            } else {
                messageEntityList = messageRepository.findAll(currentContact.getId(), currentContact.getContactType().getValue());
            }
            messageAdapter = new MessageAdapter(messageEntityList);
            for (MessageEntity message: messageAdapter.getMessageEntities()) {
                if ( (message.getDestinationId().equals(currentUser.getCodigo()) || !message.getGroupId().isEmpty())
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
                        if(messageEntity.getDestinationId().equals(currentUser.getCodigo()) || !messageEntity.getGroupId().isEmpty()){
                            try {
                                Log.d(TAG, "onNext: " + messageEntity.toJSON());
                                confirmAck(messageEntity);
                                messageAdapter.add(messageEntity);
                                rvMessages.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
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
                        MessageEntity.getGroupId(currentContact),
                        MessageEntity.getGroupId(currentContact).isEmpty() ? ContactEntity.ContactType.None: currentContact.getContactType(),
                        MessageEntity.DestinationState.Create,
                        1,
                        currentTime,
                        currentTime,
                        null);

                messageRepository.insert(messageSend);
                messageAdapter.add(messageSend);
                sendMessage(messageSend);
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
                        , MessageEntity.getGroupId(currentContact)
                        ,MessageEntity.getGroupId(currentContact).isEmpty() ? ContactEntity.ContactType.None: currentContact.getContactType()
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
                rvMessages.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });
    }
}
