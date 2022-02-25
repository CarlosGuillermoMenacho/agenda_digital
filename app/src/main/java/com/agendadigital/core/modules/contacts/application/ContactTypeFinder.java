package com.agendadigital.core.modules.contacts.application;

import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.domain.ContactTypeEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactTypeRepository;
import java.util.List;

public class ContactTypeFinder {

    private final ContactTypeRepository contactTypeRepository;
    private final ContactRepository contactRepository;

    public ContactTypeFinder(ContactTypeRepository contactTypeRepository, ContactRepository contactRepository) {
        this.contactTypeRepository = contactTypeRepository;
        this.contactRepository = contactRepository;
    }

    public List<ContactTypeEntity> findAll() throws Exception {
        List<ContactTypeEntity> contactTypeEntityList = contactTypeRepository.findAll();
        if (contactTypeEntityList.size() == 0) {
            List<ContactEntity.ContactType> contactTypes = contactRepository.getContactTypes();
            for (ContactEntity.ContactType contactType : contactTypes) {
                ContactTypeEntity contactTypeEntity = new ContactTypeEntity(contactType.getValue(), contactType.getForLabel());
                contactTypeRepository.insert(contactTypeEntity);
                contactTypeEntityList.add(contactTypeEntity);
            }
        }
        for (ContactTypeEntity contactTypeEntity: contactTypeEntityList) {
            contactTypeEntity.setContactEntityList(contactRepository.findAllByContactType(contactTypeEntity.getId()));
        }
        return contactTypeEntityList;
    }
}
