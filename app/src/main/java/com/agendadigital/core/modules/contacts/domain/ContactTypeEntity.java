package com.agendadigital.core.modules.contacts.domain;

import java.util.List;

public class ContactTypeEntity {

    private int id;
    private String description;

    public ContactTypeEntity(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class ContactTypeCourses {

        private ContactTypeEntity contactTypeEntity;
        private List<ContactEntity.CourseEntity> courseEntityList;

        public ContactTypeCourses(ContactTypeEntity contactTypeEntity) {
            this.contactTypeEntity = contactTypeEntity;
        }

        public ContactTypeEntity getContactTypeEntity() {
            return contactTypeEntity;
        }

        public void setContactTypeEntity(ContactTypeEntity contactTypeEntity) {
            this.contactTypeEntity = contactTypeEntity;
        }

        public List<ContactEntity.CourseEntity> getCourseEntityList() {
            return courseEntityList;
        }

        public void setCourseEntityList(List<ContactEntity.CourseEntity> courseEntityList) {
            this.courseEntityList = courseEntityList;
        }
    }
}
