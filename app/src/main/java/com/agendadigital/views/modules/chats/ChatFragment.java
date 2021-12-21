package com.agendadigital.views.modules.chats;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.Toast;
import com.agendadigital.MainActivity;
import com.agendadigital.R;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.ConstantsGlobals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.modules.messages.domain.MessageBase;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.modules.messages.infrastructure.MessageRepository;
import com.agendadigital.core.services.messages.MessageDto;
import com.agendadigital.core.shared.infrastructure.utils.DateFormatter;
import com.agendadigital.views.modules.chats.components.adapters.MessageAdapter;
import com.agendadigital.views.modules.chats.components.fab.SendFloatingActionButton;
import com.agendadigital.views.modules.chats.components.observers.MessageObservable;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.User;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
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
    private EditText etTextMessageToSend;
    private ImageButton btAttach;
    private MessageAdapter messageAdapter;
    private final MessageObservable messageObservable = new MessageObservable();
    private List<MessageEntity> messageEntityList = new ArrayList<>();

    private MessageRepository messageRepository;

    SendFloatingActionButton sendFloatingActionButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chat, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            currentContact = (ContactEntity) bundle.getSerializable("contact");
            new ContactRepository(view.getContext()).resetUnreadMessages(currentContact);
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(currentContact.toString());
        }
        init();
        initSentButton();
        initRecyclerView();
        initAttachButton();
        return view;
    }

    
    
    private void init(){
        messageRepository = new MessageRepository(view.getContext());
        btAttach = view.findViewById(R.id.btAttach);
        etTextMessageToSend = view.findViewById(R.id.etTextMessageToSend);
        currentUser = Globals.user;
    }
    
    private void initAttachButton() {
        btAttach.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), v);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.fileAttach:
                            Toast.makeText(view.getContext(), "FileAttach", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.imageAttach:
                            Toast.makeText(view.getContext(), "ImageAttach", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return ChatFragment.super.onOptionsItemSelected(item);
                }
            });
            popupMenu.inflate(R.menu.popup_attachments);
            popupMenu.show();
        });
    }
    
    private void initRecyclerView(){
        RecyclerView rvMessages = view.findViewById(R.id.rvMessagesList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        try {
            messageEntityList = messageRepository.findAll(currentContact.getId());
            List<MessageEntity> messagesToConfirm = new ArrayList<>();
            for (MessageEntity message: messageEntityList) {
                if (message.getDestinationId().equals(currentUser.getCodigo()) &&
                        message.getDestinationState() == MessageEntity.DestinationState.Received) {
                    messagesToConfirm.add(message);
                }
            }
            if(messagesToConfirm.size() > 0){
                for (MessageEntity messageToConfirm: messagesToConfirm
                ) {
                    messageToConfirm.setDestinationState(MessageEntity.DestinationState.Read);
                    confirmAck(messageToConfirm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        messageAdapter = new MessageAdapter(messageEntityList);
        rvMessages.setAdapter(messageAdapter);
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
                            if(messageEntity.getDestinationId().equals(currentUser.getCodigo())){
                                try {
                                    Log.d(TAG, "onNext: " + messageEntity.toJSON());
                                    messageEntity.setDestinationState(MessageEntity.DestinationState.Read);
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

    private void initSentButton(){
        sendFloatingActionButton = view.findViewById(R.id.sendButton);
        sendFloatingActionButton.setIconDrawable(getResources().getDrawable(R.drawable.ic_send_white_24dp));
        sendFloatingActionButton.setOnClickListener(v -> {
            Date currentTime = new Date(System.currentTimeMillis());
            if(!etTextMessageToSend.getText().toString().trim().isEmpty()) {
                MessageEntity messageSend = new MessageEntity("-1",
                        1,
                        currentUser.getCodigo(),
                        currentUser.getTipo(),
                        currentContact.getId(),
                        currentContact.getContactType(),
                        etTextMessageToSend.getText().toString(),
                        currentContact.getContactType() == ContactEntity.ContactType.Course ? 1 : 0,
                        MessageEntity.DestinationState.Create,
                        1,
                        currentTime,
                        currentTime,
                        null);
                JSONObject params = new JSONObject();
                try {
                    MessageDto.SendMessageRequest sendMessageRequest = new MessageDto.SendMessageRequest(messageSend.getMessageType()
                            , messageSend.getDeviceFromId()
                            , messageSend.getDeviceFromType().getValue()
                            , messageSend.getDestinationId()
                            , messageSend.getDestinationType().getValue()
                            , messageSend.getData(), messageSend.getForGroup(), messageSend.getCreatedAt());

                    Log.d(TAG, "MessageSent: " + sendMessageRequest.toJSON());
                    params.put("message", new JSONObject(sendMessageRequest.toJSON()));

                    messageSend.setId(UUID.randomUUID().toString());
                    messageRepository.insert(messageSend);
                    messageAdapter.add(messageSend);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/send-message", params, response -> {
                        try {
                            MessageDto.SendMessageResponse sendMessageResponse = new Gson().fromJson(response.getString("message"),
                                                                                                    new TypeToken<MessageDto.SendMessageResponse>() {}
                                                                                                        .getType());

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MessageBase._ID, sendMessageResponse.getId());
                            contentValues.put(MessageBase.COL_DESTINATION_STATE, MessageEntity.DestinationState.Sent.getValue());
                            contentValues.put(MessageBase.COL_SENT_AT, currentTime.getTime());
                            messageRepository.update(contentValues, MessageBase._ID + "= ?", new String[] { messageSend.getId() });

                            messageSend.setDestinationState(MessageEntity.DestinationState.Sent);
                            messageAdapter.updateDestinationState(messageSend);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onResponse: " + response);
                    }, error -> {
                        String body;
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
                        if(error.networkResponse.data!=null) {
                            body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Log.d(TAG, "onErrorResponse: " + body);
                            Toast.makeText(getContext(), statusCode + ":" + body, Toast.LENGTH_SHORT).show();
                        }
                    });
                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                etTextMessageToSend.setText("");
            }
        });
    }

    private void confirmAck(MessageEntity message) throws UnsupportedEncodingException, JSONException {
        JSONObject params = new JSONObject();
        MessageDto.ConfirmMessageRequest confirmMessageRequest = new MessageDto.ConfirmMessageRequest(message.getId(), message.getDestinationState().getValue(), DateFormatter.format(message.getReceivedAt()));
        params.put("message", new JSONObject(confirmMessageRequest.toJSON()));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/send-message", params, response -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MessageBase.COL_DESTINATION_STATE, MessageEntity.DestinationState.Read.getValue());
            messageRepository.update(contentValues, MessageBase._ID + "= ?", new String[] { message.getId() });
        }, error -> {
            String body;
            String statusCode = String.valueOf(error.networkResponse.statusCode);
            if(error.networkResponse.data!=null) {
                body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                Log.d(TAG, "onErrorResponse: " + body);
                Toast.makeText(getContext(), statusCode + ":" + body, Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);
    }

    
}
