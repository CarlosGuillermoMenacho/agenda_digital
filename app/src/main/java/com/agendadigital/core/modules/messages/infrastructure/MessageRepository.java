package com.agendadigital.core.modules.messages.infrastructure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.clases.User;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.messages.domain.MessageBase;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageRepository {

    private final SQLiteDatabase repository;

    public MessageRepository(Context context) {
        repository = new AdminSQLite(context, "dbReader", null, 1).getReadableDatabase();
    }

    public long insert(MessageEntity messageEntity) {
        ContentValues values = new ContentValues();
        values.put(MessageBase._ID, messageEntity.getId());
        values.put(MessageBase.COL_MESSAGE_TYPE, messageEntity.getMessageType());
        values.put(MessageBase.COL_DEVICE_FROM_ID, messageEntity.getDeviceFromId());
        values.put(MessageBase.COL_DEVICE_FROM_TYPE, messageEntity.getDeviceFromType().getValue());
        values.put(MessageBase.COL_DESTINATION_ID, messageEntity.getDestinationId());
        values.put(MessageBase.COL_DESTINATION_TYPE, messageEntity.getDestinationType().getValue());
        values.put(MessageBase.COL_DATA, messageEntity.getData());
        values.put(MessageBase.COL_FOR_GROUP, messageEntity.getForGroup());
        values.put(MessageBase.COL_DESTINATION_STATE, messageEntity.getDestinationState().getValue());
        values.put(MessageBase.COL_STATE, messageEntity.getState());
        values.put(MessageBase.COL_CREATED_AT, messageEntity.getCreatedAt().getTime());
        values.put(MessageBase.COL_SENT_AT, messageEntity.getSentAt().getTime());
        values.put(MessageBase.COL_RECEIVED_AT, messageEntity.getReceivedAt()==null?null:messageEntity.getReceivedAt().getTime());
        return repository.insert(MessageBase.TABLE_NAME, null, values);
    }

    public MessageEntity findLastMessageReceivedByContact(ContactEntity contactEntity) throws Exception {
        Cursor cursor = repository.query(MessageBase.TABLE_NAME,
                                        MessageBase.SQL_SELECT_ALL,
                                        MessageBase.COL_DEVICE_FROM_ID + "=? and " + MessageBase.COL_DEVICE_FROM_TYPE + "=?",
                new String[] {contactEntity.getId(), String.valueOf(contactEntity.getContactType().getValue())},
                null,
                null, MessageBase.sortOrder);
        MessageEntity messageEntity = null;
        if (cursor.moveToFirst()){
            String id = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase._ID));
            int messageTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_MESSAGE_TYPE));
            String deviceFromId = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DEVICE_FROM_ID));
            int deviceFromType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DEVICE_FROM_TYPE));
            String destinationId = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_ID));
            int destinationType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_TYPE));
            String data = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DATA));
            int forGroup = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_FOR_GROUP));
            MessageEntity.DestinationState destionationState = MessageEntity.DestinationState.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_STATE)));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_STATE));
            Date createdAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_CREATED_AT)));
            Date sentAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_SENT_AT)));
            Date receivedAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_RECEIVED_AT)));
            messageEntity = new MessageEntity(id, messageTypeId, deviceFromId, User.UserType.setValue(deviceFromType), destinationId, ContactEntity.ContactType.setValue(destinationType), data, forGroup, destionationState, status, createdAt, sentAt, receivedAt);
        }
        cursor.close();
        return messageEntity;
    }

    public List<MessageEntity> findAll(String contactId) throws Exception {
        Cursor cursor = repository.query(
                MessageBase.TABLE_NAME,   // The table to query
                MessageBase.SQL_SELECT_ALL,             // The array of columns to return (pass null to get all)
                MessageBase.COL_DESTINATION_ID + "=? or " + MessageBase.COL_DEVICE_FROM_ID + " = ? ",              // The columns for the WHERE clause
                new String[] { contactId, contactId },          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                MessageBase.sortOrder               // The sort order
        );
        List<MessageEntity> notificationEntities = new ArrayList<>();
        while(cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase._ID));
            int messageTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_MESSAGE_TYPE));
            String deviceFromId = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DEVICE_FROM_ID));
            int deviceFromType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DEVICE_FROM_TYPE));
            String destinationId = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_ID));
            int destinationType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_TYPE));
            String data = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DATA));
            int forGroup = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_FOR_GROUP));
            MessageEntity.DestinationState destionationState = MessageEntity.DestinationState.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_STATE)));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_STATE));
            Date createdAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_CREATED_AT)));
            Date sentAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_SENT_AT)));
            Date receivedAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_RECEIVED_AT)));
            notificationEntities.add(new MessageEntity(id, messageTypeId, deviceFromId, User.UserType.setValue(deviceFromType), destinationId, ContactEntity.ContactType.setValue(destinationType), data, forGroup, destionationState, status, createdAt, sentAt, receivedAt));
        }
        cursor.close();
        return notificationEntities;
    }

    public void update(ContentValues contentValues, String whereClause, String[] whereArgs) {
        repository.update(MessageBase.TABLE_NAME, contentValues, whereClause, whereArgs);
    }
}
