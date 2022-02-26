package com.agendadigital.core.modules.contacts.application;

import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactCourseRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;

import java.util.ArrayList;
import java.util.List;

public class ContactFinder {

    private ContactCourseRepository contactCourseRepository;
    private ContactRepository contactRepository;

    public ContactFinder(ContactCourseRepository contactCourseRepository, ContactRepository contactRepository) {
        this.contactCourseRepository = contactCourseRepository;
        this.contactRepository = contactRepository;
    }

    public List<ContactEntity> findAllContactsByCourseAndType(String courseId, int contactType) throws Exception {
        List<ContactEntity> contactEntityList = new ArrayList<>();
        List<ContactEntity.ContactCourseEntity> contactCourseEntities = this.contactCourseRepository.findAllCoursesByCourseId(courseId);
        for(ContactEntity.ContactCourseEntity contactCourseEntity: contactCourseEntities) {
            if (contactCourseEntity.getContactType() == contactType) {
                ContactEntity contactEntity = contactRepository.findByIdAndType(contactCourseEntity.getContactId(), contactCourseEntity.getContactType());
                contactEntityList.add(contactEntity);
            }
        }
        return contactEntityList;
    }
}
