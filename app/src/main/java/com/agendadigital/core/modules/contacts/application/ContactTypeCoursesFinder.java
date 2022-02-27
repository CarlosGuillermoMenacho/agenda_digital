package com.agendadigital.core.modules.contacts.application;

import com.agendadigital.core.modules.contacts.domain.ContactTypeEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactCourseRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactTypeRepository;
import java.util.ArrayList;
import java.util.List;

public class ContactTypeCoursesFinder {
    private final ContactTypeRepository contactTypeRepository;
    private final ContactCourseRepository contactCourseRepository;

    public ContactTypeCoursesFinder(ContactTypeRepository contactTypeRepository, ContactCourseRepository contactCourseRepository) {
        this.contactTypeRepository = contactTypeRepository;
        this.contactCourseRepository = contactCourseRepository;
    }

    public List<ContactTypeEntity.ContactTypeCourses> findAll() throws Exception {
        List<ContactTypeEntity.ContactTypeCourses> contactTypeCourses = new ArrayList<>();
        List<ContactTypeEntity> contactTypeEntityList = contactTypeRepository.findAll();
        for (ContactTypeEntity contactTypeEntity: contactTypeEntityList) {
            ContactTypeEntity.ContactTypeCourses contactTypeCourse = new ContactTypeEntity.ContactTypeCourses(contactTypeEntity);
            contactTypeCourse.setCourseEntityList(contactCourseRepository.findAllCoursesByContactType(contactTypeEntity.getId()));
            contactTypeCourses.add(contactTypeCourse);
        }
        return contactTypeCourses;
    }
}
