package com.agendadigital.core.modules.contacts.infrastructure;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.shared.domain.database.FeedReaderContract;
import com.agendadigital.core.shared.domain.database.FeedReaderDbHelper;

import java.util.List;

public class ContactRepository {
    private SQLiteDatabase repository;

    public ContactRepository(Context context) {
        repository =new FeedReaderDbHelper(context).getReadableDatabase();
    }

    public long insert(ContactEntity contactEntity) {
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedContact._ID, contactEntity.getId());
        values.put(FeedReaderContract.FeedContact.COL_NAME, contactEntity.getName());
        values.put(FeedReaderContract.FeedContact.COL_TYPE_CONTACT, contactEntity.getTypeContact().getValue());
        return repository.insert(FeedReaderContract.FeedContact.TABLE_NAME, null, values);
    }

    public int deleteAll(){
        return repository.delete(FeedReaderContract.FeedContact.TABLE_NAME, null, null);
    }

    public List<ContactEntity> findAll() throws Exception {
        return FeedReaderContract
                .FeedContact
                .findAll(repository.query(FeedReaderContract.FeedContact.TABLE_NAME,
                        FeedReaderContract.FeedContact.SQL_SELECT_ALL,
                        null,
                        null, null, null, null));
    }

    public ContactEntity findById(String id) throws Exception {
        return FeedReaderContract
                .FeedContact
                .findById(repository.query(FeedReaderContract.FeedContact.TABLE_NAME,
                                            FeedReaderContract.FeedContact.SQL_SELECT_ALL,
                                            FeedReaderContract.FeedContact._ID + "=? and " + FeedReaderContract.FeedContact.COL_TYPE_CONTACT + "=?",
                                            new String[] { id, String.valueOf(ContactEntity.ContactType.Tutor.getValue()) }, null, null, null));
    }
}
