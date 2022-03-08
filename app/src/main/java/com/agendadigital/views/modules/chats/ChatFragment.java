package com.agendadigital.views.modules.chats;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;
import com.agendadigital.BuildConfig;
import com.agendadigital.R;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.modules.messages.domain.MessageBase;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.modules.messages.domain.MultimediaEntity;
import com.agendadigital.core.modules.messages.infrastructure.MessageRepository;
import com.agendadigital.core.services.messages.MessageDto;
import com.agendadigital.core.services.messages.MessageService;
import com.agendadigital.core.shared.infrastructure.utils.DirectoryManager;
import com.agendadigital.core.shared.infrastructure.utils.FilesUtils;
import com.agendadigital.databinding.FragmentChatBinding;
import com.agendadigital.views.modules.chats.components.adapters.MessageAdapter;
import com.agendadigital.views.modules.chats.components.observers.MessageObservable;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.User;
import com.agendadigital.views.shared.infrastructure.ViewHelpers;
import com.devlomi.record_view.OnRecordListener;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChatFragment extends Fragment {

    private final String TAG = "ChatFragment";
    private FragmentChatBinding binding;
    private Context context;
    private User currentUser;
    private ContactEntity currentContact;
    private final int CAMERA_REQUEST = 1001;
    private File photoFile;
    private MessageAdapter messageAdapter;
    private Disposable messageDisposable;
    private final MessageObservable messageObservable = new MessageObservable();
    private MessageService messageService;
    private MessageRepository messageRepository;
    private ContactRepository contactRepository;
    private MediaRecorder mediaRecorder;
    private String audioPath;
    private StorageReference storageReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            currentContact = (ContactEntity) bundle.getSerializable("contact");
            ActionBar actionBar = ViewHelpers.getActionBar(getActivity());
            if (actionBar != null) {
                actionBar.setTitle(currentContact.toString());
            }
        }
        View view = binding.getRoot();
        context = view.getContext();
        currentUser = Globals.user;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            initRepositories();
            verifyPermissions();
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        contactRepository.resetUnreadMessages(currentContact);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        messageDisposable.dispose();
    }

    private void initRepositories(){
        messageRepository = new MessageRepository(context);
        contactRepository = new ContactRepository(context);
        messageService = new MessageService(context);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private void verifyPermissions() {
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Sin permisos de lectura", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 1);
        }
    }

    private void initViews() throws Exception {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        binding.rvMessagesList.setLayoutManager(layoutManager);
        Drawable sendButtonDrawable = ContextCompat.getDrawable(context, R.drawable.ic_send_white_24dp);
        if (sendButtonDrawable == null) {
            throw new Exception("Send button not found");
        }
        binding.sendButton.setIconDrawable(sendButtonDrawable);
        binding.recordView.setCounterTimeColor(Color.BLACK);
        binding.recordButton.setRecordView(binding.recordView);
        binding.recordButton.setListenForRecord(false);

        initTextMessageListener();
        initButtonAttachListener();
        initButtonCameraListener();
        initRecyclerView();
        initMessageObserver();
        initSentButtonListener();
        initRecordButtonListener();
    }

    private void initTextMessageListener() {
        binding.etTextMessageToSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    binding.sendButton.setVisibility(View.GONE);
                    binding.recordButton.setVisibility(View.VISIBLE);
                } else {
                    binding.recordButton.setVisibility(View.GONE);
                    binding.sendButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initButtonAttachListener() {
        binding.btAttach.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.setOnMenuItemClickListener(item -> {
                Intent pickIntent;
                Intent chooserIntent = null;
                int activityResult = 0;
                if (item.getItemId() == R.id.attachDocument) {
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
                } else if (item.getItemId() == R.id.attachImage) {
                    pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    pickIntent.setType("image/*");
                    pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String [] {
                            "image/jpeg",
                            "image/png"
                    });
                    chooserIntent = Intent.createChooser(pickIntent, "Select a image");
                    activityResult = MessageEntity.MessageType.Image.getValue();
                }else if (item.getItemId() == R.id.attachVideo) {
                    pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    pickIntent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
                    chooserIntent = Intent.createChooser(pickIntent, "Select a video");
                    activityResult = MessageEntity.MessageType.Video.getValue();
                }
                startActivityForResult(chooserIntent,activityResult);
                return ChatFragment.super.onOptionsItemSelected(item);
            });
            popupMenu.inflate(R.menu.popup_attachments);
            popupMenu.show();
        });
    }

    private void initButtonCameraListener() {
        binding.btCamera.setOnClickListener(click -> {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            FragmentActivity fragmentActivity = getActivity();
            if (fragmentActivity != null && cameraIntent.resolveActivity(fragmentActivity.getPackageManager()) != null) {
                try {
                    photoFile = FilesUtils.createImageTempFile(context);
                }catch (Exception e) {
                    Log.d(TAG, "initButtonCameraListener: " + e.getMessage());
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(context
                            , BuildConfig.APPLICATION_ID + ".provider", photoFile);

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null && resultCode == Activity.RESULT_CANCELED)
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
                selectedFile = Uri.fromFile(photoFile);
                filePath = FilesUtils.saveFileFromUri(context, selectedFile, photoFile.getName(), DirectoryManager.getPathToSave(MessageEntity.MessageType.Image, true));
                Log.d(TAG, "onActivityResultCamera: " + filePath);
                fileReference = storageReference.child("images/" + photoFile.getName());
            }catch (Exception e) {
                Log.e(TAG, "onActivityResultImage: ", e.fillInStackTrace());
            }
        } else {
            if (data == null)
                return;
            selectedFile = data.getData();
            if (selectedFile == null || selectedFile.getPath() == null) {
                Toast.makeText(context, "Ocurrió un error al seleccionar el archivo.", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "onActivityAttach: " + selectedFile.getPath());

            String filename;
            Cursor cursor = context.getContentResolver().query(selectedFile,null,null,null,null);
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
                    Toast.makeText(context, "Seleccione el formato correcto", Toast.LENGTH_SHORT).show();
                    return;
                }
                messageEntity.setMessageType(messageType);
                filePath = FilesUtils.saveFileFromUri(context, selectedFile, filename, DirectoryManager.getPathToSave(messageEntity.getMessageType(), true));
                fileReference = storageReference.child(messageEntity.getMessageType().toString() + "/" + filename);
            }catch(Exception e) {
                Log.e(TAG, "onActivityResult: ", e.fillInStackTrace());
            }

        }
        if (fileReference == null)
            return;
        MultimediaEntity multimediaEntity = new MultimediaEntity(UUID.randomUUID().toString(), messageEntity.getId(), filePath, "");
        sendMultimediaMessage(fileReference, selectedFile, messageEntity, multimediaEntity);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initRecyclerView(){
        try {
            List<MessageEntity> messageEntityList;
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
            messageAdapter.notifyItemRangeChanged(0, messageAdapter.getItemCount());
        } catch (Exception e) {
            Log.d(TAG, "initRecyclerView: " + e.getMessage());
            e.printStackTrace();
        }
        binding.rvMessagesList.setAdapter(messageAdapter);
    }

    private void initMessageObserver(){
        try {
           messageDisposable =
               messageObservable.getNotificationObservable()
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(messageEntity -> {
                       Log.d(TAG, "initMessageObserver:1 " + (messageEntity.getDestinationId()));
                        if ((currentContact.isGroup() && currentContact.getId().equals(messageEntity.getGroupId()) && currentContact.getContactType() == messageEntity.getGroupType())
                            || (!currentContact.isGroup() && messageEntity.getGroupId().isEmpty() && currentUser.getCodigo().equals(messageEntity.getDestinationId()) && currentUser.getTipo().getValue() == messageEntity.getDestinationType().getValue()
                                && currentContact.getId().equals(messageEntity.getDeviceFromId()) && currentContact.getContactType().getValue() == messageEntity.getDeviceFromType().getValue()) ) {
                            try {
                                confirmAck(messageEntity);
                                messageAdapter.add(messageEntity);
                                Log.d(TAG, "initMessageObserver: 2" + messageEntity.getId());
                                binding.rvMessagesList.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                            } catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                   },error-> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show());
        }catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initSentButtonListener(){
        binding.sendButton.setOnClickListener(v -> {
            Date currentTime = new Date(System.currentTimeMillis());
            if(!binding.etTextMessageToSend.getText().toString().trim().isEmpty()) {
                MessageEntity messageSend = new MessageEntity(UUID.randomUUID().toString(),
                        MessageEntity.MessageType.Text,
                        currentUser.getCodigo(),
                        currentUser.getTipo(),
                        currentContact.getId(),
                        currentContact.getContactType(),
                        binding.etTextMessageToSend.getText().toString(),
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
                binding.etTextMessageToSend.setText("");
            }
        });
    }

    private void initRecordButtonListener() {
        binding.recordButton.setOnClickListener(view -> binding.recordButton.setListenForRecord(true));
        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                setUpRecording();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.messageLayout.setVisibility(View.GONE);
                binding.recordView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                boolean isDeleted = false;
                if (file.exists()) {
                    isDeleted = file.delete();
                }
                if (!isDeleted) {
                    Toast.makeText(context, "No se pudo eliminar el audio", Toast.LENGTH_SHORT).show();
                }
                binding.recordView.setVisibility(View.GONE);
                binding.messageLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish(long recordTime) {
                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                binding.recordView.setVisibility(View.GONE);
                binding.messageLayout.setVisibility(View.VISIBLE);

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
                boolean isDeleted = false;
                if (file.exists()) {
                    isDeleted = file.delete();
                }
                if (!isDeleted) {
                    Toast.makeText(context, "No se pudo eliminar el audio", Toast.LENGTH_SHORT).show();
                }
                binding.recordView.setVisibility(View.GONE);
                binding.messageLayout.setVisibility(View.VISIBLE);
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
            Log.d(TAG, "confirmAck: " + currentContact.getName());
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
                messageRepository.updateMessageSentOk(messageToSend, sendMessageResponse.getId(), sendMessageResponse.getSentAt());
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
                if (body.contains("no tiene permisos") || body.contains("cuenta activa")) {
                    messageRepository.delete(messageToSend);
                    messageAdapter.delete(messageToSend);
                }
                Toast.makeText(getContext(), statusCode + ":" + body, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMultimediaMessage(StorageReference storageReference, Uri fileToSend, MessageEntity messageToSend, MultimediaEntity multimediaToSend) {
        messageToSend.setMultimediaEntity(multimediaToSend);
        messageRepository.insert(messageToSend);
        messageAdapter.add(messageToSend);

        UploadTask uploadTask = storageReference.putFile(fileToSend);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Exception exception = task.getException();
                if (exception == null) {
                    throw new Exception("Ocurrió un error al enviar el archivo.");
                }
                throw exception;
            }
            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            Log.d(TAG, "onComplete: " + task.isSuccessful());
            if (!task.isSuccessful()) {
                Log.d(TAG, "onCompleteError: " + task.getException());
                Toast.makeText(context, "No se pudo subir el archivo.", Toast.LENGTH_SHORT).show();
            } else {
                Uri downloadUri = task.getResult();
                if (downloadUri != null) {
                    messageToSend.getMultimediaEntity().setFirebaseUri(downloadUri.toString());
                    sendMessage(messageToSend);
                    binding.rvMessagesList.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                }
            }
        });
    }
}
