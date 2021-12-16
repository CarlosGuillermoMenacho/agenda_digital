package com.agendadigital.core.services.login;

import org.json.JSONException;
import org.json.JSONObject;

public class UserDto {

    public static class LoginUserRequest implements java.io.Serializable {
        private String userId;
        private int userType;
        private String firebaseToken;

        public LoginUserRequest(String userId, int userType, String firebaseToken) {
            this.userId = userId;
            this.userType = userType;
            this.firebaseToken = firebaseToken;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        public String getFirebaseToken() {
            return firebaseToken;
        }

        public void setFirebaseToken(String firebaseToken) {
            this.firebaseToken = firebaseToken;
        }

        @Override
        public String toString() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userId", userId);
                jsonObject.put("userType", userType);
                jsonObject.put("firebaseToken", firebaseToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                return jsonObject.toString(4);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }
    }


}
