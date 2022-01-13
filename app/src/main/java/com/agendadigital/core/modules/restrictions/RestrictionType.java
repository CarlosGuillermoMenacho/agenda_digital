package com.agendadigital.core.modules.restrictions;

import com.agendadigital.core.modules.contacts.domain.ContactEntity;

public enum RestrictionType {
    Mute(1),
    SendImages(2),
    SendVideos(3),
    SendDocuments(4),
    SendAudio(5);

    private final int value;

    RestrictionType(int value) {
        this.value =value;
    }

    public int getValue() {
        return value;
    }
    public static RestrictionType setValue(int value) throws Exception {
        switch (value){
            case 1:
                return Mute;
            case 2:
                return SendImages;
            case 3:
                return SendVideos;
            case 4:
                return SendDocuments;
            case 5:
                return SendAudio;
            default:
                throw new Exception("RestrictionType inválido." + value);
        }
    }
}
