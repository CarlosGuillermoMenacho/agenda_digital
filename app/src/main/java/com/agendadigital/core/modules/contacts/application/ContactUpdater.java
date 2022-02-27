package com.agendadigital.core.modules.contacts.application;

import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.domain.ContactTypeEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactCourseRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactTypeRepository;
import com.agendadigital.core.services.contacts.ContactDto;
import java.util.List;

public class ContactUpdater {

    public static class Deleter {

        private final ContactRepository contactRepository;
        private final ContactCourseRepository contactCourseRepository;

        public Deleter(ContactRepository contactRepository, ContactCourseRepository contactCourseRepository) {
            this.contactRepository = contactRepository;
            this.contactCourseRepository = contactCourseRepository;
        }

        public void deleteContactAndCoursesNotFound(List<ContactEntity> actualContactList, List<ContactDto.CreateContactResponse> contactResponseList) {
            for (ContactEntity actualContact : actualContactList) {
                boolean isContactFound = false;
                for (ContactDto.CreateContactResponse contactResponse : contactResponseList) {
                    if (actualContact.getId().equals(contactResponse.getId()) && actualContact.getContactType().getValue() == contactResponse.getContactType() ) {
                        isContactFound = true;
                        List<ContactEntity.ContactCourseEntity> actualContactCourseList = contactCourseRepository.findAllCoursesByContactId(actualContact.getId(), actualContact.getContactType().getValue());
                        for (ContactEntity.ContactCourseEntity actualContactCourse: actualContactCourseList) {
                            boolean isCourseFound = false;
                            for (ContactDto.CourseResponse courseResponse : contactResponse.getCourses()) {
                                if (actualContactCourse.getCourseEntity().getCourseId().equals(courseResponse.getId())) {
                                    isCourseFound = true;
                                    break;
                                }
                            }
                            if (!isCourseFound) {
                                contactCourseRepository.delete(actualContact.getId(), actualContact.getContactType().getValue(),actualContactCourse.getCourseEntity().getCourseId());
                            }
                        }
                        break;
                    }
                }
                if (!isContactFound) {
                    contactRepository.delete(actualContact.getId(), actualContact.getContactType().getValue());
                    contactCourseRepository.deleteAll(actualContact.getId(), actualContact.getContactType().getValue());
                }
            }
        }
    }

    public static class Inserter {

        private final ContactRepository contactRepository;
        private final ContactCourseRepository contactCourseRepository;
        private final ContactTypeRepository contactTypeRepository;

        public Inserter(ContactRepository contactRepository, ContactCourseRepository contactCourseRepository, ContactTypeRepository contactTypeRepository) {
            this.contactRepository = contactRepository;
            this.contactCourseRepository = contactCourseRepository;
            this.contactTypeRepository = contactTypeRepository;
        }

        public void insertNewContactsAndCourses(List<ContactEntity> actualContactList, List<ContactDto.CreateContactResponse> contactResponseList) throws Exception {
            for (ContactDto.CreateContactResponse contactResponse : contactResponseList) {
                boolean isContactFound = false;
                for (ContactEntity actualContact : actualContactList) {
                    if (contactResponse.getId().equals(actualContact.getId()) && contactResponse.getContactType() == actualContact.getContactType().getValue() ) {
                        isContactFound = true;
                        List<ContactEntity.ContactCourseEntity> actualContactCourseList = contactCourseRepository.findAllCoursesByContactId(actualContact.getId(), actualContact.getContactType().getValue());
                        for (ContactDto.CourseResponse courseResponse : contactResponse.getCourses()) {
                            for (ContactEntity.ContactCourseEntity actualContactCourse: actualContactCourseList) {
                                if (!actualContactCourse.getCourseEntity().getCourseId().equals(courseResponse.getId())) {
                                    contactCourseRepository.insert(new ContactEntity.ContactCourseEntity(
                                            0,
                                            new ContactEntity.CourseEntity(courseResponse.getId(), courseResponse.getName()),
                                            actualContact.getId(),
                                            actualContact.getContactType().getValue()));
                                }
                            }
                        }
                        break;
                    }
                }
                if (!isContactFound) {
                    if (contactTypeRepository.findById(contactResponse.getContactType()) == null) {
                        ContactEntity.ContactType contactType = ContactEntity.ContactType.setValue(contactResponse.getContactType());
                        contactTypeRepository.insert(new ContactTypeEntity(contactType.getValue(), contactType.getForLabel()));
                    }
                    contactRepository.insert(new ContactEntity(
                            contactResponse.getId(),
                            contactResponse.getName(),
                            ContactEntity.ContactType.setValue(contactResponse.getContactType()),
                            0,
                            "",
                            null));
                    for(ContactDto.CourseResponse course: contactResponse.getCourses()) {
                        contactCourseRepository.insert(new ContactEntity.ContactCourseEntity(
                                -1,
                                new ContactEntity.CourseEntity(course.getId(),
                                        course.getName()),
                                contactResponse.getId(),
                                contactResponse.getContactType()));
                    }
                }
            }
        }
    }

}
