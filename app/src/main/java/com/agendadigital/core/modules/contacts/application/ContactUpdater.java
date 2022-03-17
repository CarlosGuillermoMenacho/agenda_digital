package com.agendadigital.core.modules.contacts.application;

import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.contacts.domain.ContactTypeEntity;
import com.agendadigital.core.modules.contacts.infrastructure.ContactGroupRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactRepository;
import com.agendadigital.core.modules.contacts.infrastructure.ContactTypeRepository;
import com.agendadigital.core.services.contacts.ContactDto;
import java.util.List;

public class ContactUpdater {

    public static class Deleter {

        private final ContactRepository contactRepository;
        private final ContactGroupRepository contactGroupRepository;

        public Deleter(ContactRepository contactRepository, ContactGroupRepository contactGroupRepository) {
            this.contactRepository = contactRepository;
            this.contactGroupRepository = contactGroupRepository;
        }

        public void deleteContactAndCoursesNotFound(List<ContactEntity> actualContactList, List<ContactDto.CreateContactResponse> contactResponseList) {
            for (ContactEntity actualContact : actualContactList) {
                boolean isContactFound = false;
                for (ContactDto.CreateContactResponse contactResponse : contactResponseList) {
                    if (actualContact.getId().equals(contactResponse.getId()) && actualContact.getContactType().getValue() == contactResponse.getContactType() ) {
                        isContactFound = true;
                        List<ContactEntity.ContactGroupEntity> actualContactCourseList = null;
                        try {
                            actualContactCourseList = contactGroupRepository.findAllCoursesByContactId(actualContact.getId(), actualContact.getContactType().getValue());

                            for (ContactEntity.ContactGroupEntity actualContactCourse: actualContactCourseList) {
                                boolean isCourseFound = false;
                                for (ContactDto.CourseResponse courseResponse : contactResponse.getCourses()) {
                                    if (actualContactCourse.getGroupEntity().getCourseId().equals(courseResponse.getId())) {
                                        isCourseFound = true;
                                        break;
                                    }
                                }
                                if (!isCourseFound) {
                                    contactGroupRepository.delete(actualContact.getId(), actualContact.getContactType().getValue(),actualContactCourse.getGroupEntity().getCourseId());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                if (!isContactFound) {
                    contactRepository.delete(actualContact.getId(), actualContact.getContactType().getValue());
                    contactGroupRepository.deleteAll(actualContact.getId(), actualContact.getContactType().getValue());
                }
            }
        }
    }

    public static class Inserter {

        private final ContactRepository contactRepository;
        private final ContactGroupRepository contactGroupRepository;
        private final ContactTypeRepository contactTypeRepository;

        public Inserter(ContactRepository contactRepository, ContactGroupRepository contactGroupRepository, ContactTypeRepository contactTypeRepository) {
            this.contactRepository = contactRepository;
            this.contactGroupRepository = contactGroupRepository;
            this.contactTypeRepository = contactTypeRepository;
        }

        public void insertNewContactsAndCourses(List<ContactEntity> actualContactList, List<ContactDto.CreateContactResponse> contactResponseList) throws Exception {
            for (ContactDto.CreateContactResponse contactResponse : contactResponseList) {
                boolean isContactFound = false;
                for (ContactEntity actualContact : actualContactList) {
                    if (contactResponse.getId().equals(actualContact.getId()) && contactResponse.getContactType() == actualContact.getContactType().getValue() ) {
                        isContactFound = true;
                        List<ContactEntity.ContactGroupEntity> actualContactCourseList = contactGroupRepository.findAllCoursesByContactId(actualContact.getId(), actualContact.getContactType().getValue());
                        for (ContactDto.CourseResponse courseResponse : contactResponse.getCourses()) {
                            for (ContactEntity.ContactGroupEntity actualContactCourse: actualContactCourseList) {
                                if (!actualContactCourse.getGroupEntity().getCourseId().equals(courseResponse.getId())) {
                                    contactGroupRepository.insert(new ContactEntity.ContactGroupEntity(
                                            0,
                                            new ContactEntity.GroupEntity(courseResponse.getId(), courseResponse.getName(), ContactEntity.GroupType.setValue(courseResponse.getType())),
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
                        contactGroupRepository.insert(new ContactEntity.ContactGroupEntity(
                                -1,
                                new ContactEntity.GroupEntity(course.getId(),
                                        course.getName(), ContactEntity.GroupType.setValue(course.getType())),
                                contactResponse.getId(),
                                contactResponse.getContactType()));
                    }
                }
            }
        }
    }

}
