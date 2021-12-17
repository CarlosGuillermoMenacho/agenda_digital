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
        Tutor(1),
        Student(2),
        Teacher(3),
        Director(4),
        Staff(5),
        Course(6);

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
                    return Tutor;
                case 2:
                    return Student;
                case 3:
                    return Teacher;
                case 4:
                    return Director;
                case 5:
                    return Staff;
                case 6:
                    return Course;
                default:
                    throw new Exception("TypeContact inválido.");
            }
        }
    }
}
