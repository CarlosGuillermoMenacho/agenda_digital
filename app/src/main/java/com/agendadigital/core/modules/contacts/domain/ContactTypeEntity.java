package com.agendadigital.core.modules.contacts.domain;

import java.util.ArrayList;
import java.util.List;

public class ContactTypeEntity {
    public int id;
    public String description;
    public List<ContactEntity> contactEntityList;

    public ContactTypeEntity(int id, String description) {
        this.id = id;
        this.description = description;
        this.contactEntityList = new ArrayList<>();
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

    public List<ContactEntity> getContactEntityList() {
        return contactEntityList;
    }

    public void setContactEntityList(List<ContactEntity> contactEntityList) {
        this.contactEntityList = contactEntityList;
    }

    public void addContactEntity(ContactEntity contactEntity) {
        this.contactEntityList.add(contactEntity);
    }
}
