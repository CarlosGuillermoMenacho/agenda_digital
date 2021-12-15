package com.agendadigital.views.modules.chats.components.observers;

import com.agendadigital.core.modules.messages.domain.MessageEntity;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class MessageObservable {
    private static PublishSubject<MessageEntity> notificationPublisher;

    public PublishSubject<MessageEntity> getPublisher() {
        if (notificationPublisher == null) {
            notificationPublisher = PublishSubject.create();
        }

        return notificationPublisher;
    }

    public Observable<MessageEntity> getNotificationObservable() {
        return getPublisher();
    }
}
