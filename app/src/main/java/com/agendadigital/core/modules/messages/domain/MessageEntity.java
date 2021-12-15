package com.agendadigital.core.modules.messages.domain;

import android.icu.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import androidx.annotation.NonNull;

public class MessageEntity {
    private String id;
    private int messageTypeId;
    private String deviceFromId;
    private String destinationId;
    private String data;
    private int forGroup;
    private DestinationStatus destinationStatus;
    private int status;
    private Date createdAt;
    private Date sentAt;
    private Date receivedAt;

    public MessageEntity(String id, int messageTypeId, String deviceFromId, String destinationId, String data, int forGroup, DestinationStatus destinationStatus, int status, Date createdAt, Date sentAt, Date receivedAt) {
        this.id = id;
        this.messageTypeId = messageTypeId;
        this.deviceFromId = deviceFromId;
        this.destinationId = destinationId;
        this.data = data;
        this.forGroup = forGroup;
        this.destinationStatus = destinationStatus;
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
        return messageTypeId;
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

    public void setDestinationStatus(DestinationStatus destinationStatus) {
        this.destinationStatus = destinationStatus;
    }

    public DestinationStatus getDestinationStatus() {
        return destinationStatus;
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
            jsonObject.put("messageTypeId", messageTypeId);
            jsonObject.put("deviceFromId", deviceFromId);
            jsonObject.put("destinationId", destinationId);
            jsonObject.put("data", data);
            jsonObject.put("forGroup", forGroup);
            jsonObject.put("destinationStatus", destinationStatus.getValue());
            jsonObject.put("status", status);
            jsonObject.put("createdAt", simpleDateFormat.format(createdAt));
            jsonObject.put("sendedAt", simpleDateFormat.format(sentAt));
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
            int messageTypeId = jsonObject.getInt("messageTypeId");
            String deviceFromId = jsonObject.getString("deviceFromId");
            String destinationId = jsonObject.getString("destinationId");
            String data = jsonObject.getString("data");
            int forGroup = jsonObject.getInt("forGroup");
            DestinationStatus destinationStatus = DestinationStatus.setValue(jsonObject.getInt("destinationStatus"));
            int status = jsonObject.getInt("status");
            Date createdAt = simpleDateFormat.parse(jsonObject.getString("createdAt"));
            Date sendedAt = simpleDateFormat.parse( jsonObject.getString("sendedAt"));
            Date receivedAt = jsonObject.has("receivedAt")? simpleDateFormat.parse( jsonObject.getString("receivedAt")):null;
            return new MessageEntity(id, messageTypeId, deviceFromId, destinationId, data, forGroup, destinationStatus, status, createdAt, sendedAt, receivedAt);
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

    public enum DestinationStatus {
        Sent(0),
        Received(1),
        Read(2);

        private int value;
        DestinationStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
        public static DestinationStatus setValue(int value) throws Exception {
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
