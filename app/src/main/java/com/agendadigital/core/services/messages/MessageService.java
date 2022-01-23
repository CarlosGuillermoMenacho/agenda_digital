package com.agendadigital.core.services.messages;

import android.content.Context;
import android.util.Log;
import com.agendadigital.clases.Constants;
import com.agendadigital.clases.ConstantsGlobals;
import com.agendadigital.clases.MySingleton;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.modules.messages.domain.MultimediaEntity;
import com.agendadigital.core.shared.infrastructure.utils.DateFormatter;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageService {
    private static final String TAG = "MESSAGE_SERVICE";

    private final Context context;

    public MessageService(Context context) {
        this.context = context;
    }

    public void sendMessage(MessageEntity messageToSend, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        JSONObject params = new JSONObject();
        try {
            MessageDto.SendMessageRequest sendMessageRequest = new MessageDto.SendMessageRequest(messageToSend.getMessageType().getValue()
                    , messageToSend.getDeviceFromId()
                    , messageToSend.getDeviceFromType().getValue()
                    , messageToSend.getDestinationId()
                    , messageToSend.getDestinationType().getValue()
                    , messageToSend.getData(), messageToSend.getForGroup(), messageToSend.getCreatedAt());
            if(messageToSend.getMessageType() != MessageEntity.MessageType.Text) {
                sendMessageRequest.setMultimedia(new MultimediaDto.SendMultimediaRequest(messageToSend.getMultimediaEntity().getId(), messageToSend.getMultimediaEntity().getFirebaseUri()));
            }

            Log.d(TAG, "MessageSent: " + sendMessageRequest.toJSON());
            params.put("message", new JSONObject(sendMessageRequest.toJSON()));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/send-message", params, responseListener, errorListener);
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(context).addToRequest(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void confirmAckMessage(MessageEntity message, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        JSONObject params = new JSONObject();
        try {
            MessageDto.ConfirmMessageRequest confirmMessageRequest = new MessageDto.ConfirmMessageRequest(message.getId(), message.getDestinationState().getValue(), DateFormatter.formatToDate(message.getReceivedAt()));
            params.put("message", new JSONObject(confirmMessageRequest.toJSON()));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstantsGlobals.urlChatServer + "/confirm-message", params, responseListener, errorListener);
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(context).addToRequest(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
