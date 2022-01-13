package com.agendadigital.core.services.restrictions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class RestrictionDto {

    public static class CreateUserRestrictionRequest {
        private String userRestricted;
        private int restrictionType;
        private String userId;
        private int userType;
        private int state;

        public CreateUserRestrictionRequest(String userRestricted, int restrictionType, String userId, int userType, int state) {
            this.userRestricted = userRestricted;
            this.restrictionType = restrictionType;
            this.userId = userId;
            this.userType = userType;
            this.state = state;
        }

        public String getUserRestricted() {
            return userRestricted;
        }

        public void setUserRestricted(String userRestricted) {
            this.userRestricted = userRestricted;
        }

        public int getRestrictionType() {
            return restrictionType;
        }

        public void setRestrictionType(int restrictionType) {
            this.restrictionType = restrictionType;
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

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String toJSON() {
            JSONObject jsonObject= new JSONObject();
            try {
                jsonObject.put("userRestricted", userRestricted);
                jsonObject.put("restrictionType", restrictionType);
                jsonObject.put("userId", userId);
                jsonObject.put("userType", userType);
                jsonObject.put("state", state);
                return jsonObject.toString(4);
            }catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    public static class CreateUserRestrictionResponse {
        private String id;
        private int restrictionType;
        private String createdAt;

        public CreateUserRestrictionResponse(String id, int restrictionType, String createdAt) {
            this.id = id;
            this.restrictionType = restrictionType;
            this.createdAt = createdAt;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getRestrictionType() {
            return restrictionType;
        }

        public void setRestrictionType(int restrictionType) {
            this.restrictionType = restrictionType;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class DeleteUserRestrictionRequest {
        private String id;
        private int state;

        public DeleteUserRestrictionRequest(String id, int state) {
            this.id = id;
            this.state = state;
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

        public String toJSON() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", id);
                jsonObject.put("state", state);
                return jsonObject.toString(4);
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    public static class DeleteUserRestrictionResponse {
        private String id;
        private String updatedAt;

        public DeleteUserRestrictionResponse(String id, String updatedAt) {
            this.id = id;
            this.updatedAt = updatedAt;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
