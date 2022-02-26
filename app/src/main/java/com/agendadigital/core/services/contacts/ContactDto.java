package com.agendadigital.core.services.contacts;

import com.agendadigital.core.services.login.UserDto;
import com.agendadigital.core.services.restrictions.RestrictionDto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ContactDto {

        public static class CreateContactRequest {
            public String userId;
            public int userType;
            public String schoolId;

            public CreateContactRequest(String userId, int userType, String schoolId) {
                this.userId = userId;
                this.userType = userType;
                this.schoolId = schoolId;
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

            public String getSchoolId() {
                return schoolId;
            }

            public void setSchoolId(String schoolId) {
                this.schoolId = schoolId;
            }

            public String toJSON() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("userId", userId);
                    jsonObject.put("userType", userType);
                    jsonObject.put("schoolId", schoolId);
                    return jsonObject.toString(4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "";
            }
        }

        public static class CourseResponse {
            private String id;
            private String name;

            public CourseResponse(String id, String name) {
                this.id = id;
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class CreateContactResponse {
            private String id;
            private String name;
            private int contactType;
            private List<CourseResponse> courses;

            public CreateContactResponse(String id, String name, int contactType, List<CourseResponse> courses) {
                this.id = id;
                this.name = name;
                this.contactType = contactType;
                this.courses = courses;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getContactType() {
                return contactType;
            }

            public void setContactType(int contactType) {
                this.contactType = contactType;
            }

            public List<CourseResponse> getCourses() {
                return courses;
            }

            public void setCourses(List<CourseResponse> courses) {
                this.courses = courses;
            }
        }

        public static class GetGroupMembersRequest {
            private String deviceFromId;
            private int deviceFromType;
            private String destinationId;
            private int destinationType;

            public GetGroupMembersRequest(String deviceFromId, int deviceFromType, String destinationId, int destinationType) {
                this.deviceFromId = deviceFromId;
                this.deviceFromType = deviceFromType;
                this.destinationId = destinationId;
                this.destinationType = destinationType;
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

            public String toJSON() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("deviceFromId", deviceFromId);
                    jsonObject.put("deviceFromType", deviceFromType);
                    jsonObject.put("destinationId", destinationId);
                    jsonObject.put("destinationType", destinationType);
                    return jsonObject.toString(4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "";
            }
        }

        public static class GroupMemberRestrictionResult {
            private UserDto.CreateTokenResultWithName userToken;
            private List<RestrictionDto.CreateUserRestrictionResponse> userRestrictions;

            public GroupMemberRestrictionResult(UserDto.CreateTokenResultWithName userToken, List<RestrictionDto.CreateUserRestrictionResponse> userRestrictions) {
                this.userToken = userToken;
                this.userRestrictions = userRestrictions;
            }

            public UserDto.CreateTokenResultWithName getUserToken() {
                return userToken;
            }

            public void setUserToken(UserDto.CreateTokenResultWithName userToken) {
                this.userToken = userToken;
            }

            public List<RestrictionDto.CreateUserRestrictionResponse> getUserRestrictions() {
                return userRestrictions;
            }

            public void setUserRestrictions(List<RestrictionDto.CreateUserRestrictionResponse> userRestrictions) {
                this.userRestrictions = userRestrictions;
            }
        }
    }
