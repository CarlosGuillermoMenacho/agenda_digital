package com.agendadigital.views.modules.chats;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.agendadigital.MainActivity;
import com.agendadigital.R;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.ConstantsGlobals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.modules.messages.infrastructure.MessageRepository;
import com.agendadigital.core.services.messages.MessageDto;
import com.agendadigital.core.shared.domain.database.FeedReaderContract;
import com.agendadigital.core.shared.infrastructure.AsyncHttpRest;
import com.agendadigital.views.modules.chats.components.adapters.MessageAdapter;
import com.agendadigital.views.modules.chats.components.fab.SendFloatingActionButton;
import com.agendadigital.views.modules.chats.components.observers.MessageObservable;
import com.agendadigital.clases.Globals;
import com.agendadigital.clases.User;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;
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

    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private MessageObservable messageObservable = new MessageObservable();
    private List<MessageEntity> messageEntityList = new ArrayList<>();

    private MessageRepository messageRepository;

    SendFloatingActionButton sendFloatingActionButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chat, container, false);
        Bundle bundle = getArguments();
        currentContact= (ContactEntity) bundle.getSerializable("contact");
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(currentContact.toString());
        init();
        initSentButton();
        initRecyclerView();
        return view;
    }

    private void init(){
        messageRepository = new MessageRepository(view.getContext());
        etTextMessageToSend = view.findViewById(R.id.etTextMessageToSend);
        currentUser = Globals.user;
    }

    private void initRecyclerView(){
        rvMessages = view.findViewById(R.id.rvMessagesList);
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
                                Log.d(TAG, "onNext: " + messageEntity.toJSON());
                                try {
                                    confirmAck(messageEntity);
                                    messageEntity.setDestinationState(MessageEntity.DestinationState.Received);
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
                MessageEntity messageSend = new MessageEntity(
                        "",
                        1,
                        currentUser.getCodigo(),
                        currentContact.getId(),
                        etTextMessageToSend.getText().toString(),
                        currentContact.getTypeContact() == ContactEntity.ContactType.Course ? 1 : 0,
                        MessageEntity.DestinationState.Create,
                        1,
                        currentTime,
                        currentTime,
                        null);
                JSONObject params = new JSONObject();
                try {
                    MessageDto.SendMessageRequest sendMessageRequest = new MessageDto.SendMessageRequest(messageSend.getMessagetypeId()
                            , messageSend.getDeviceFromId()
                            , messageSend.getDestinationId()
                            , currentContact.getTypeContact().getValue()
                            , messageSend.getData(), messageSend.getForGroup(), messageSend.getCreatedAt());

                    Log.d(TAG, "MessageSent: " + sendMessageRequest.toJSON());
                    params.put("message", new JSONObject(sendMessageRequest.toJSON()));

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/send-message", params, response -> {
                        try {
                            JSONObject messageResponse = response.getJSONObject("message");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onResponse: " + response);
                    }, error -> {
                        String body;
                        //get status code here
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
                        //get response body and parse with appropriate encoding
                        if(error.networkResponse.data!=null) {
                            body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Log.d(TAG, "onErrorResponse: " + body);
                            Toast.makeText(getContext(), statusCode + ":" + body, Toast.LENGTH_SHORT).show();

                        }
                    });
                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    MySingleton.getInstance(getContext()).addToRequest(jsonObjectRequest);

//                        AsyncHttpRest.post(getContext(), "/send-message", params, new JsonHttpResponseHandler() {
//                            @Override
//                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                                super.onSuccess(statusCode, headers, response);
//                                Log.d(TAG, "onSuccess: JSONObject" + response + "->" + messageSend.toJSON());
//                                try {
//                                    messageSend.setId(response.getString("id"));
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                Log.d(TAG, "onSuccessReceive: " + messageSend.toJSON());
//                                long id = messageRepository.insert(messageSend);
//                                messageAdapter.add(messageSend);
//                                rvMessages.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
//                                messageAdapter.notifyDataSetChanged();
//                            }
//
//                            @Override
//                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                                super.onFailure(statusCode, headers, responseString, throwable);
//                                Log.d(TAG, "onFailure:" + throwable + "->" + responseString);
//                                Toast.makeText(getContext(), responseString, Toast.LENGTH_SHORT).show();
//                            }
//                        });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                etTextMessageToSend.setText("");
            }
        });
    }

    private void confirmAck(MessageEntity message) throws UnsupportedEncodingException, JSONException {
        JSONObject params = new JSONObject();
        params.put("message", message.toJSON());
        AsyncHttpRest.post(getContext(), "/confirm-message", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ContentValues contentValues = new ContentValues();
                contentValues.put("destinationStatus", MessageEntity.DestinationState.Read.getValue());
                messageRepository.update(contentValues, FeedReaderContract.FeedMessage._ID + "= ?", new String[] { message.getId() });
                Log.d(TAG, "onSuccess: JSONObject" + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, "onFailure:" + throwable);
            }
        });
    }
}
