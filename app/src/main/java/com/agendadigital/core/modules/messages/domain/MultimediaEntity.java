package com.agendadigital.core.modules.messages.domain;

public class MultimediaEntity {

    private String id;
    private String messageId;
    private String localUri;
    private String firebaseUri;

    public MultimediaEntity(String id, String messageId, String localUri, String firebaseUri) {
        this.id = id;
        this.messageId = messageId;
        this.localUri = localUri;
        this.firebaseUri = firebaseUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

    public String getFirebaseUri() {
        return firebaseUri;
    }

    public void setFirebaseUri(String firebaseUri) {
        this.firebaseUri = firebaseUri;
    }
}
