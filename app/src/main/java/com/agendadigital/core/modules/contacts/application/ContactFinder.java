package com.agendadigital.core.modules.contacts.application;

import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactGroupRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import java.util.ArrayList;
import java.util.List;

public class ContactFinder {

    private final ContactGroupRepository contactGroupRepository;
    private final ContactRepository contactRepository;

    public ContactFinder(ContactGroupRepository contactGroupRepository, ContactRepository contactRepository) {
        this.contactGroupRepository = contactGroupRepository;
        this.contactRepository = contactRepository;
    }

    public List<ContactEntity> findAllContactsByCourseAndType(String courseId, int contactType) throws Exception {
        List<ContactEntity> contactEntityList = new ArrayList<>();
        List<ContactEntity.ContactGroupEntity> contactCourseEntities = this.contactGroupRepository.findAllCoursesByCourseId(courseId);
        for(ContactEntity.ContactGroupEntity contactGroupEntity : contactCourseEntities) {
            if (contactGroupEntity.getContactType() == contactType) {
                ContactEntity contactEntity = contactRepository.findByIdAndType(contactGroupEntity.getContactId(), contactGroupEntity.getContactType());
                contactEntityList.add(contactEntity);
            }
        }
        return contactEntityList;
    }
}
