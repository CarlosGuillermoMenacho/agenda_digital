package com.agendadigital.core.modules.contacts.domain;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class ContactEntity implements Serializable {
    private String id;
    private String name;
    private ContactType contactType;

    public ContactEntity(String id, String name, ContactType contactType) {
        this.id = id;
        this.name = name;
        this.contactType = contactType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContactType getTypeContact() {
        return contactType;
    }

    public void setTypeContact(ContactType contactType) {
        this.contactType = contactType;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public enum ContactType {
        Course(1),
        Teacher(2),
        Tutor(3);

        private final int value;

        ContactType(int value) {
            this.value =value;
        }

        public int getValue() {
            return value;
        }
        public static ContactType setValue(int value) throws Exception {
            switch (value){
                case 1:
                    return Course;
                case 2:
                    return Teacher;
                case 3:
                    return Tutor;
                default:
                    throw new Exception("TypeContact inválido.");
            }
        }
    }
}
