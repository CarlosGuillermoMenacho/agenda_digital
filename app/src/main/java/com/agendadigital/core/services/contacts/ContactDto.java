package com.agendadigital.core.services.contacts;

public class ContactDto {
    public static class CreateContactResponse {
        private String id;
        private String name;
        private int typeContact;

        public CreateContactResponse(String id, String name, int typeContact) {
            this.id = id;
            this.name = name;
            this.typeContact = typeContact;
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

        public int getTypeContact() {
            return typeContact;
        }

        public void setTypeContact(int typeContact) {
            this.typeContact = typeContact;
        }
    }

    public static class CreateContactRequest {
        public String userId;
        public int userType;

        public CreateContactRequest(String userId, int userType) {
            this.userId = userId;
            this.userType = userType;
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
    }
}
