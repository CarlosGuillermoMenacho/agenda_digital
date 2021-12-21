package com.agendadigital.views.modules.contacts.components.observers;

import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class ContactObservable {
        private static PublishSubject<ContactEntity> notificationPublisher;

        public PublishSubject<ContactEntity> getPublisher() {
            if (notificationPublisher == null) {
                notificationPublisher = PublishSubject.create();
            }

            return notificationPublisher;
        }

        public Observable<ContactEntity> getNotificationObservable() {
            return getPublisher();
        }

}
