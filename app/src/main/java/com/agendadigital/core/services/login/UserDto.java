package com.agendadigital.core.services.login;

public class UserDto {
    public static class LoginUserRequest {
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
    }


}
