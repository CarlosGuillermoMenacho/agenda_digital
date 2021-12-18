package com.agendadigital.core.modules.contacts.infrastructure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.core.modules.contacts.domain.ContactBase;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import java.util.ArrayList;
import java.util.List;

public class ContactRepository {
    private final SQLiteDatabase repository;

    public ContactRepository(Context context) {
        repository =new AdminSQLite(context, "dbReader", null, 1).getReadableDatabase();
    }

    public long insert(ContactEntity contactEntity) {
        ContentValues values = new ContentValues();
        values.put(ContactBase._ID, contactEntity.getId());
        values.put(ContactBase.COL_NAME, contactEntity.getName());
        values.put(ContactBase.COL_TYPE_CONTACT, contactEntity.getTypeContact().getValue());
        return repository.insert(ContactBase.TABLE_NAME, null, values);
    }

    public int deleteAll() {
        return repository.delete(ContactBase.TABLE_NAME, null, null);
    }

    public List<ContactEntity> findAll() throws Exception {
        Cursor cursor = repository.query(ContactBase.TABLE_NAME,
                ContactBase.SQL_SELECT_ALL,
                null,
                null, null, null, null);

        List<ContactEntity> contactEntityList = new ArrayList<>();
        while(cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactBase._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactBase.COL_NAME));
            int typeContact = cursor.getInt(cursor.getColumnIndexOrThrow(ContactBase.COL_TYPE_CONTACT));
            ContactEntity.ContactType contactTypeEnum = ContactEntity.ContactType.setValue(typeContact);
            contactEntityList.add(new ContactEntity(id, name, contactTypeEnum));
        }
        cursor.close();
        return contactEntityList;
    }

    public ContactEntity findByIdAndType(String contactId, int type) throws Exception {
        Cursor cursor = repository.query(ContactBase.TABLE_NAME,
                ContactBase.SQL_SELECT_ALL,
                ContactBase._ID + "=? and " + ContactBase.COL_TYPE_CONTACT + "=?",
                new String[] { contactId, String.valueOf(type) }, null, null, null);
        ContactEntity contactEntity = null;
        if(cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactBase._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactBase.COL_NAME));
            int contactType = cursor.getInt(cursor.getColumnIndexOrThrow(ContactBase.COL_TYPE_CONTACT));
            ContactEntity.ContactType contactTypeEnum = ContactEntity.ContactType.setValue(contactType);
            contactEntity = new ContactEntity(id, name, contactTypeEnum);
        }
        cursor.close();
        return contactEntity;
    }
}
