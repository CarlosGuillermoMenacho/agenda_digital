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

    public static class CreateTokenResultWithName {
        private int id;
        private String userId;
        private int userType;
        private String name;
        private String firebaseToken;

        public CreateTokenResultWithName(int id, String userId, int userType, String name, String firebaseToken) {
            this.id = id;
            this.userId = userId;
            this.userType = userType;
            this.name = name;
            this.firebaseToken = firebaseToken;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFirebaseToken() {
            return firebaseToken;
        }

        public void setFirebaseToken(String firebaseToken) {
            this.firebaseToken = firebaseToken;
        }
    }

}
