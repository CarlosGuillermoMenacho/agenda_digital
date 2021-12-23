package com.agendadigital.core.services.messages;

import org.json.JSONException;
import org.json.JSONObject;

public class MultimediaDto {
    public static class SendMultimediaRequest {
        private String id;
        private String firebaseUri;

        public SendMultimediaRequest(String id, String firebaseUri) {
            this.id = id;
            this.firebaseUri = firebaseUri;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirebaseUri() {
            return firebaseUri;
        }

        public void setFirebaseUri(String firebaseUri) {
            this.firebaseUri = firebaseUri;
        }

        public String toJSON() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("firebaseUri", firebaseUri);
            return jsonObject.toString(4);
        }
    }
}
