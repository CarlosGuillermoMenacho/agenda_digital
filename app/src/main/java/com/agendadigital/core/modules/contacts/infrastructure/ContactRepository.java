package com.agendadigital.core.modules.contacts.infrastructure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.core.modules.contacts.domain.ContactBase;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import java.util.ArrayList;
import java.util.Date;
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
        values.put(ContactBase.COL_TYPE_CONTACT, contactEntity.getContactType().getValue());
        values.put(ContactBase.COL_UNREAD_MESSAGES, contactEntity.getUnreadMessages());
        values.put(ContactBase.COL_LAST_MESSAGE_DATA, contactEntity.getLastMessageData());
        values.put(ContactBase.COL_LAST_MESSAGE_RECEIVED_AT, contactEntity.getLastMessageReceived() == null?null: contactEntity.getLastMessageReceived().getTime());
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
            int unreadMessages = cursor.getInt(cursor.getColumnIndexOrThrow(ContactBase.COL_UNREAD_MESSAGES));
            String lastMessageData = cursor.getString(cursor.getColumnIndexOrThrow(ContactBase.COL_LAST_MESSAGE_DATA));
            Date lastMessageReceivedAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(ContactBase.COL_LAST_MESSAGE_RECEIVED_AT)));
            contactEntityList.add(new ContactEntity(id, name, contactTypeEnum, unreadMessages, lastMessageData, lastMessageReceivedAt));
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
            int unreadMessages = cursor.getInt(cursor.getColumnIndexOrThrow(ContactBase.COL_UNREAD_MESSAGES));
            String lastMessageData = cursor.getString(cursor.getColumnIndexOrThrow(ContactBase.COL_LAST_MESSAGE_DATA));
            Date lastMessageReceivedAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(ContactBase.COL_LAST_MESSAGE_RECEIVED_AT)));
            contactEntity = new ContactEntity(id, name, contactTypeEnum, unreadMessages, lastMessageData, lastMessageReceivedAt);
        }
        cursor.close();
        return contactEntity;
    }

    public ContactEntity updateUnreadMessagesAndLastMessage(MessageEntity messageEntity) throws Exception {
        String id;
        int type;
        if(!messageEntity.getGroupId().isEmpty()) {
            id = messageEntity.getGroupId();
            type = messageEntity.getGroupType().getValue();
        } else {
            id = messageEntity.getDeviceFromId();
            type = messageEntity.getDeviceFromType().getValue();
        }
        ContactEntity contactEntity = findByIdAndType(id, type);
        contactEntity.setUnreadMessages(contactEntity.getUnreadMessages() + 1);
        contactEntity.setLastMessageData(messageEntity.getData());
        contactEntity.setLastMessageReceived(messageEntity.getReceivedAt());
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactBase.COL_UNREAD_MESSAGES, contactEntity.getUnreadMessages());
        contentValues.put(ContactBase.COL_LAST_MESSAGE_DATA, messageEntity.getData());
        contentValues.put(ContactBase.COL_LAST_MESSAGE_RECEIVED_AT, messageEntity.getReceivedAt().getTime());
        repository.update(ContactBase.TABLE_NAME, contentValues, ContactBase._ID + "= ? and " + ContactBase.COL_TYPE_CONTACT + "=?",
                new String[] { id, String.valueOf(type) });
        return contactEntity;
    }

    public void resetUnreadMessages(ContactEntity currentContact) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactBase.COL_UNREAD_MESSAGES, 0);
        repository.update(ContactBase.TABLE_NAME, contentValues, ContactBase._ID + "= ? and " + ContactBase.COL_TYPE_CONTACT + "=?",
                new String[] { currentContact.getId(), String.valueOf(currentContact.getContactType().getValue()) });
    }
}
