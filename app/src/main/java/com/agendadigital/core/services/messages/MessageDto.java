package com.agendadigital.core.services.messages;

import com.agendadigital.core.shared.infrastructure.utils.DateFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MessageDto {

    public static class SendMessageRequest {
        private int messageType;
        private String deviceFromId;
        private int deviceFromType;
        private String destinationId;
        private int destinationType;
        private String data;
        private String groupId;
        private int groupType;
        private Date createdAt;
        private MultimediaDto.SendMultimediaRequest multimedia;

        public SendMessageRequest(int messageType, String deviceFromId, int deviceFromType, String destinationId, int destinationType, String data, String groupId, int groupType, Date createdAt) {
            this.messageType = messageType;
            this.deviceFromId = deviceFromId;
            this.deviceFromType = deviceFromType;
            this.destinationId = destinationId;
            this.destinationType = destinationType;
            this.data = data;
            this.groupId = groupId;
            this.groupType = groupType;
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

        public int getDeviceFromType() {
            return deviceFromType;
        }

        public void setDeviceFromType(int deviceFromType) {
            this.deviceFromType = deviceFromType;
        }

        public String getDestinationId() {
            return destinationId;
        }

        public void setDestinationId(String destinationId) {
            this.destinationId = destinationId;
        }

        public int getDestinationType() {
            return destinationType;
        }

        public void setDestinationType(int destinationType) {
            this.destinationType = destinationType;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public int getGroupType() {
            return groupType;
        }

        public void setGroupType(int groupType) {
            this.groupType = groupType;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public MultimediaDto.SendMultimediaRequest getMultimedia() {
            return multimedia;
        }

        public void setMultimedia(MultimediaDto.SendMultimediaRequest multimedia) {
            this.multimedia = multimedia;
        }

        public String toJSON(){
            JSONObject jsonObject= new JSONObject();
            try {
                jsonObject.put("messageType", messageType);
                jsonObject.put("deviceFromId", deviceFromId);
                jsonObject.put("deviceFromType", deviceFromType);
                jsonObject.put("destinationId", destinationId);
                jsonObject.put("destinationType", destinationType);
                jsonObject.put("data", data);
                jsonObject.put("groupId", groupId);
                jsonObject.put("groupType", groupType);
                jsonObject.put("createdAt", DateFormatter.formatToDate(createdAt));
                jsonObject.put("multimedia", multimedia != null? new JSONObject(multimedia.toJSON()): "");
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

        public String toJSON() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("destinationState", destinationState);
            jsonObject.put("receivedAt", receivedAt);
            return jsonObject.toString(4);
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
