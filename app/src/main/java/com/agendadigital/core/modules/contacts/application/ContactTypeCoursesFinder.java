package com.agendadigital.core.modules.contacts.application;

import com.agendadigital.core.modules.contacts.domain.ContactTypeEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactGroupRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactTypeRepository;
import java.util.ArrayList;
import java.util.List;

public class ContactTypeCoursesFinder {
    private final ContactTypeRepository contactTypeRepository;
    private final ContactGroupRepository contactGroupRepository;

    public ContactTypeCoursesFinder(ContactTypeRepository contactTypeRepository, ContactGroupRepository contactGroupRepository) {
        this.contactTypeRepository = contactTypeRepository;
        this.contactGroupRepository = contactGroupRepository;
    }

    public List<ContactTypeEntity.ContactTypeCourses> findAll() throws Exception {
        List<ContactTypeEntity.ContactTypeCourses> contactTypeCourses = new ArrayList<>();
        List<ContactTypeEntity> contactTypeEntityList = contactTypeRepository.findAll();
        for (ContactTypeEntity contactTypeEntity: contactTypeEntityList) {
            ContactTypeEntity.ContactTypeCourses contactTypeCourse = new ContactTypeEntity.ContactTypeCourses(contactTypeEntity);
            contactTypeCourse.setCourseEntityList(contactGroupRepository.findAllCoursesByContactType(contactTypeEntity.getId()));
            contactTypeCourses.add(contactTypeCourse);
        }
        return contactTypeCourses;
    }
}
