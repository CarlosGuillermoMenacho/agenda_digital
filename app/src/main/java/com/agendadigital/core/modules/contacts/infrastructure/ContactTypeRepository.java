package com.agendadigital.core.modules.contacts.infrastructure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.core.modules.contacts.domain.ContactBase;
import com.agendadigital.core.modules.contacts.domain.ContactTypeBase;
import com.agendadigital.core.modules.contacts.domain.ContactTypeEntity;
import java.util.ArrayList;
import java.util.List;

public class ContactTypeRepository {

    private final SQLiteDatabase repository;

    public ContactTypeRepository(Context context) {
        repository =new AdminSQLite(context, "dbReader", null, 1).getReadableDatabase();
    }

    public long insert(ContactTypeEntity contactType) {
        ContentValues values = new ContentValues();
        values.put(ContactTypeBase._ID, contactType.getId());
        values.put(ContactTypeBase.COL_DESCRIPTION, contactType.getDescription());
        return repository.insert(ContactTypeBase.TABLE_NAME, null, values);
    }

    public ContactTypeEntity findById(int _id) throws Exception  {
        Cursor cursor = repository.query(ContactTypeBase.TABLE_NAME,
                ContactTypeBase.SQL_SELECT_ALL,
                ContactTypeBase._ID + "=?",
                new String[] { String.valueOf(_id)}, null, null, null);
        ContactTypeEntity contactTypeEntity = null;
        if (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ContactBase._ID));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(ContactTypeBase.COL_DESCRIPTION));
            contactTypeEntity = new ContactTypeEntity(id, description);
        }
        cursor.close();
        return contactTypeEntity;
    }

    public List<ContactTypeEntity> findAll() throws Exception {
        Cursor cursor = repository.query(ContactTypeBase.TABLE_NAME,
                ContactTypeBase.SQL_SELECT_ALL,
                null,
                null, null, null, null);

        List<ContactTypeEntity> contactTypeEntityList = new ArrayList<>();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ContactBase._ID));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(ContactTypeBase.COL_DESCRIPTION));
            contactTypeEntityList.add(new ContactTypeEntity(id, description));
        }
        cursor.close();
        return contactTypeEntityList;
    }

}
