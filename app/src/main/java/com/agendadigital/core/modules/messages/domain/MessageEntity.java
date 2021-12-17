package com.agendadigital.core.modules.messages.domain;

import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import androidx.annotation.NonNull;

public class MessageEntity {
    private String id;
    private int messageType;
    private String deviceFromId;
    private String destinationId;
    private String data;
    private int forGroup;
    private DestinationState destinationState;
    private int status;
    private Date createdAt;
    private Date sentAt;
    private Date receivedAt;

    public MessageEntity(String id, int messageType, String deviceFromId, String destinationId, String data, int forGroup, DestinationState destinationState, int status, Date createdAt, Date sentAt, Date receivedAt) {
        this.id = id;
        this.messageType = messageType;
        this.deviceFromId = deviceFromId;
        this.destinationId = destinationId;
        this.data = data;
        this.forGroup = forGroup;
        this.destinationState = destinationState;
        this.status = status;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.receivedAt = receivedAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() { return id; }
    public int getMessagetypeId() {
        return messageType;
    }

    public String getDeviceFromId() {
        return deviceFromId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public String getData() {
        return data;
    }

    public int getForGroup() {
        return forGroup;
    }

    public void setDestinationState(DestinationState destinationState) {
        this.destinationState = destinationState;
    }

    public DestinationState getDestinationState() {
        return destinationState;
    }

    public int getStatus() {
        return status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    public String toJSON(){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                JSONObject jsonObject= new JSONObject();
                try {
                    if(id != null) {
                        jsonObject.put("id", id);
                    }
                    jsonObject.put("messageType", messageType);
                    jsonObject.put("deviceFromId", deviceFromId);
                    jsonObject.put("destinationId", destinationId);
                    jsonObject.put("data", data);
                    jsonObject.put("forGroup", forGroup);
                    jsonObject.put("destinationStatus", destinationState.getValue());
                    jsonObject.put("status", status);
                    jsonObject.put("createdAt", simpleDateFormat.format(createdAt));
                    jsonObject.put("sentAt", simpleDateFormat.format(sentAt));
                    jsonObject.put("receivedAt", receivedAt == null?null: simpleDateFormat.format(receivedAt));
                    return jsonObject.toString(4);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }

    }

    public static MessageEntity fromJSON(JSONObject jsonObject){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            String id = jsonObject.getString("id");
            int messageType = jsonObject.getInt("messageType");
            String deviceFromId = jsonObject.getString("deviceFromId");
            String destinationId = jsonObject.getString("destinationId");
            String data = jsonObject.getString("data");
            int forGroup = jsonObject.getInt("forGroup");
            DestinationState destinationState = DestinationState.setValue(jsonObject.getInt("destinationStatus"));
            int status = jsonObject.getInt("status");
            Date createdAt = simpleDateFormat.parse(jsonObject.getString("createdAt"));
            Date sendedAt = simpleDateFormat.parse( jsonObject.getString("sendedAt"));
            Date receivedAt = jsonObject.has("receivedAt")? simpleDateFormat.parse( jsonObject.getString("receivedAt")):null;
            return new MessageEntity(id, messageType, deviceFromId, destinationId, data, forGroup, destinationState, status, createdAt, sendedAt, receivedAt);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    @NonNull
    @Override
    public String toString() {
        return deviceFromId + ":" + data;
    }

    public enum DestinationState {
        Create(0),
        Sent(1),
        Received(2),
        Read(3);

        private int value;
        DestinationState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
        public static DestinationState setValue(int value) throws Exception {
            switch (value){
                case 0:
                    return Sent;
                case 1:
                    return Received;
                case 2:
                    return Read;
                default:
                    throw new Exception("Type DestinationStatus is not exists.");
            }
        }
    }
}
