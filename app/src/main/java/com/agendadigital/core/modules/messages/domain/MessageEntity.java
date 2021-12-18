package com.agendadigital.core.modules.messages.domain;

import com.agendadigital.clases.User;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.shared.infrastructure.utils.DateFormatter;

import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import androidx.annotation.NonNull;

public class MessageEntity {
    private String id;
    private int messageType;
    private String deviceFromId;
    private User.UserType deviceFromType;
    private String destinationId;
    private ContactEntity.ContactType destinationType;
    private String data;
    private int forGroup;
    private DestinationState destinationState;
    private int state;
    private Date createdAt;
    private Date sentAt;
    private Date receivedAt;

    public MessageEntity(String id, int messageType, String deviceFromId, User.UserType deviceFromType, String destinationId, ContactEntity.ContactType destinationType, String data, int forGroup, DestinationState destinationState, int state, Date createdAt, Date sentAt, Date receivedAt) {
        this.id = id;
        this.messageType = messageType;
        this.deviceFromId = deviceFromId;
        this.deviceFromType = deviceFromType;
        this.destinationId = destinationId;
        this.destinationType = destinationType;
        this.data = data;
        this.forGroup = forGroup;
        this.destinationState = destinationState;
        this.state = state;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.receivedAt = receivedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getDeviceFromId() {
        return deviceFromId;
    }

    public void setDeviceFromId(String deviceFromId) {
        this.deviceFromId = deviceFromId;
    }

    public User.UserType getDeviceFromType() {
        return deviceFromType;
    }

    public void setDeviceFromType(User.UserType deviceFromType) {
        this.deviceFromType = deviceFromType;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public ContactEntity.ContactType getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(ContactEntity.ContactType destinationType) {
        this.destinationType = destinationType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getForGroup() {
        return forGroup;
    }

    public void setForGroup(int forGroup) {
        this.forGroup = forGroup;
    }

    public DestinationState getDestinationState() {
        return destinationState;
    }

    public void setDestinationState(DestinationState destinationState) {
        this.destinationState = destinationState;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String toJSON() throws JSONException {
            JSONObject jsonObject= new JSONObject();
            if(id != null) {
                jsonObject.put("id", id);
            }
            jsonObject.put("messageType", messageType);
            jsonObject.put("deviceFromId", deviceFromId);
            jsonObject.put("destinationId", destinationId);
            jsonObject.put("data", data);
            jsonObject.put("forGroup", forGroup);
            jsonObject.put("destinationStatus", destinationState.getValue());
            jsonObject.put("state", state);
            jsonObject.put("createdAt", DateFormatter.format(createdAt));
            jsonObject.put("sentAt", DateFormatter.format(sentAt));
            jsonObject.put("receivedAt", receivedAt == null?null: DateFormatter.format(receivedAt));
            return jsonObject.toString(4);
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

        private final int value;
        DestinationState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
        public static DestinationState setValue(int value) throws Exception {
            switch (value){
                case 0:
                    return Create;
                case 1:
                    return Sent;
                case 2:
                    return Received;
                case 3:
                    return Read;
                default:
                    throw new Exception("Type DestinationStatus is not exists.");
            }
        }
    }
}
