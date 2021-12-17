package com.agendadigital.core.services.messages;

import com.agendadigital.core.modules.messages.domain.MessageEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageDto {

    public static class SendMessageRequest {
        private int messageType;
        private String deviceFromId;
        private String destinationId;
        private int destinationUserType;
        private String data;
        private int forGroup;
        private Date createdAt;

        public SendMessageRequest(int messageType, String deviceFromId, String destinationId, int destinationUserType, String data, int forGroup, Date createdAt) {
            this.messageType = messageType;
            this.deviceFromId = deviceFromId;
            this.destinationId = destinationId;
            this.destinationUserType = destinationUserType;
            this.data = data;
            this.forGroup = forGroup;
            this.createdAt = createdAt;
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

        public String getDestinationId() {
            return destinationId;
        }

        public void setDestinationId(String destinationId) {
            this.destinationId = destinationId;
        }

        public int getDestinationUserType() {
            return destinationUserType;
        }

        public void setDestinationUserType(int destinationUserType) {
            this.destinationUserType = destinationUserType;
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

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public String toJSON(){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JSONObject jsonObject= new JSONObject();
            try {
                jsonObject.put("messageType", messageType);
                jsonObject.put("deviceFromId", deviceFromId);
                jsonObject.put("destinationId", destinationId);
                jsonObject.put("destinationUserType", destinationUserType);
                jsonObject.put("data", data);
                jsonObject.put("forGroup", forGroup);
                jsonObject.put("createdAt", simpleDateFormat.format(createdAt));
                return jsonObject.toString(4);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "";
            }

        }
    }

    public static class SendMessageResponse {
        private String id;
        private int state;
        private String sentAt;

        public SendMessageResponse(String id, int state, String sentAt) {
            this.id = id;
            this.state = state;
            this.sentAt = sentAt;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getSentAt() {
            return sentAt;
        }

        public void setSentAt(String sentAt) {
            this.sentAt = sentAt;
        }
    }

    public static class ConfirmMessageRequest {
        private String id;
        private int destinationState;
        private String receivedAt;

        public ConfirmMessageRequest(String id, int destinationState, String receivedAt) {
            this.id = id;
            this.destinationState = destinationState;
            this.receivedAt = receivedAt;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getDestinationState() {
            return destinationState;
        }

        public void setDestinationState(int destinationState) {
            this.destinationState = destinationState;
        }

        public String getReceivedAt() {
            return receivedAt;
        }

        public void setReceivedAt(String receivedAt) {
            this.receivedAt = receivedAt;
        }
    }

    private static class ConfirmMessageResponse {
        private String id;

        public ConfirmMessageResponse(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
