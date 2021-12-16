package com.agendadigital.core.services.messages;

import com.agendadigital.core.modules.messages.domain.MessageEntity;

import java.util.Date;

public class MessageDto {

    public static class MessageRequest {
        private int messageType;
        private String deviceFromId;
        private String destinationId;
        private String data;
        private int forGroup;
        private MessageEntity.DestinationStatus destinationStatus;
        private int status;
        private Date createdAt;
        private Date sentAt;
        private Date receivedAt;
    }
}
