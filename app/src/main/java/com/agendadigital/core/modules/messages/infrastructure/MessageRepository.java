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
import com.agendadigital.core.modules.messages.domain.MultimediaBase;
import com.agendadigital.core.modules.messages.domain.MultimediaEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageRepository {

    private final SQLiteDatabase repository;

    public MessageRepository(Context context) {
        repository = new AdminSQLite(context, "dbReader", null, 1).getReadableDatabase();
    }

    public long insert(MessageEntity messageEntity) {
        long rowsInserted = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(MessageBase._ID, messageEntity.getId());
            values.put(MessageBase.COL_MESSAGE_TYPE, messageEntity.getMessageType().getValue());
            values.put(MessageBase.COL_DEVICE_FROM_ID, messageEntity.getDeviceFromId());
            values.put(MessageBase.COL_DEVICE_FROM_TYPE, messageEntity.getDeviceFromType().getValue());
            values.put(MessageBase.COL_DESTINATION_ID, messageEntity.getDestinationId());
            values.put(MessageBase.COL_DESTINATION_TYPE, messageEntity.getDestinationType().getValue());
            values.put(MessageBase.COL_DATA, messageEntity.getData());
            values.put(MessageBase.COL_GROUP_ID, messageEntity.getGroupId());
            values.put(MessageBase.COL_GROUP_TYPE, messageEntity.getGroupType().getValue());
            values.put(MessageBase.COL_DESTINATION_STATE, messageEntity.getDestinationState().getValue());
            values.put(MessageBase.COL_STATE, messageEntity.getState());
            values.put(MessageBase.COL_CREATED_AT, messageEntity.getCreatedAt().getTime());
            values.put(MessageBase.COL_SENT_AT, messageEntity.getSentAt().getTime());
            values.put(MessageBase.COL_RECEIVED_AT, messageEntity.getReceivedAt() == null ? null : messageEntity.getReceivedAt().getTime());
            rowsInserted = repository.insert(MessageBase.TABLE_NAME, null, values);
            if (messageEntity.getMessageType().getValue() != MessageEntity.MessageType.Text.getValue()) {
                ContentValues mediaValues = new ContentValues();
                mediaValues.put(MultimediaBase._ID, messageEntity.getMultimediaEntity().getId());
                mediaValues.put(MultimediaBase.COL_MESSAGE_ID, messageEntity.getMultimediaEntity().getMessageId());
                mediaValues.put(MultimediaBase.COL_LOCAL_URI, messageEntity.getMultimediaEntity().getLocalUri());
                mediaValues.put(MultimediaBase.COL_FIREBASE_URI, messageEntity.getMultimediaEntity().getFirebaseUri());
                rowsInserted = repository.insert(MultimediaBase.TABLE_NAME, null, mediaValues);
            }
        }catch (Exception e){
            throw e;
        }
        return rowsInserted;
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
            int messageType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_MESSAGE_TYPE));
            String deviceFromId = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DEVICE_FROM_ID));
            int deviceFromType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DEVICE_FROM_TYPE));
            String destinationId = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_ID));
            int destinationType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_TYPE));
            String data = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DATA));
            String groupId = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_GROUP_ID));
            int groupType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_GROUP_ID));
            MessageEntity.DestinationState destionationState = MessageEntity.DestinationState.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_STATE)));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_STATE));
            Date createdAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_CREATED_AT)));
            Date sentAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_SENT_AT)));
            Date receivedAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_RECEIVED_AT)));
            messageEntity = new MessageEntity(id, MessageEntity.MessageType.setValue(messageType), deviceFromId, User.UserType.setValue(deviceFromType), destinationId, ContactEntity.ContactType.setValue(destinationType), data, groupId, ContactEntity.ContactType.setValue(groupType), destionationState, status, createdAt, sentAt, receivedAt);
        }
        cursor.close();
        return messageEntity;
    }

    public List<MessageEntity> findAll(String contactId, int contactType) throws Exception {
        Cursor cursor = repository.query(
                MessageBase.TABLE_NAME,   // The table to query
                MessageBase.SQL_SELECT_ALL,             // The array of columns to return (pass null to get all)
                "(" + MessageBase.COL_DESTINATION_ID + "=? and " + MessageBase.COL_DESTINATION_TYPE + " = ?) or ("
                        + MessageBase.COL_DEVICE_FROM_ID + " = ? and " + MessageBase.COL_DEVICE_FROM_TYPE + " = ? ) and " + MessageBase.COL_GROUP_ID + " = ?",              // The columns for the WHERE clause
                new String[] { contactId, String.valueOf(contactType), contactId, String.valueOf(contactType), ""},          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                MessageBase.sortOrder               // The sort order
        );
        List<MessageEntity> notificationEntities = new ArrayList<>();
        while(cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase._ID));
            int messageType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_MESSAGE_TYPE));
            String deviceFromId = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DEVICE_FROM_ID));
            int deviceFromType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DEVICE_FROM_TYPE));
            String destinationId = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_ID));
            int destinationType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_TYPE));
            String data = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DATA));
            String groupId = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_GROUP_ID));
            int groupType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_GROUP_ID));
            MessageEntity.DestinationState destinationState = MessageEntity.DestinationState.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_STATE)));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_STATE));
            Date createdAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_CREATED_AT)));
            Date sentAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_SENT_AT)));
            Date receivedAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_RECEIVED_AT)));
            MessageEntity messageEntity = new MessageEntity(id, MessageEntity.MessageType.setValue(messageType), deviceFromId, User.UserType.setValue(deviceFromType), destinationId, ContactEntity.ContactType.setValue(destinationType), data, groupId, ContactEntity.ContactType.setValue(groupType), destinationState, status, createdAt, sentAt, receivedAt);
            messageEntity.setMultimediaEntity(getMultimedia(messageEntity.getId()));
            notificationEntities.add(messageEntity);
        }
        cursor.close();
        return notificationEntities;
    }

    public List<MessageEntity> findAllForGroup(String groupIdParam, int groupTypeParam) throws Exception {
        Cursor cursor = repository.query(
                MessageBase.TABLE_NAME,   // The table to query
                MessageBase.SQL_SELECT_ALL,             // The array of columns to return (pass null to get all)
                MessageBase.COL_GROUP_ID + " = ? and " + MessageBase.COL_GROUP_TYPE + " = ? ",              // The columns for the WHERE clause
                new String[] { groupIdParam, String.valueOf(groupTypeParam) },          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                MessageBase.sortOrder               // The sort order
        );
        List<MessageEntity> notificationEntities = new ArrayList<>();
        while(cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase._ID));
            int messageType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_MESSAGE_TYPE));
            String deviceFromId = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DEVICE_FROM_ID));
            int deviceFromType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DEVICE_FROM_TYPE));
            String destinationId = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_ID));
            int destinationType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_TYPE));
            String data = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_DATA));
            String groupId = cursor.getString(cursor.getColumnIndexOrThrow(MessageBase.COL_GROUP_ID));
            int groupType = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_GROUP_ID));
            MessageEntity.DestinationState destinationState = MessageEntity.DestinationState.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_DESTINATION_STATE)));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(MessageBase.COL_STATE));
            Date createdAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_CREATED_AT)));
            Date sentAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_SENT_AT)));
            Date receivedAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MessageBase.COL_RECEIVED_AT)));
            MessageEntity messageEntity = new MessageEntity(id, MessageEntity.MessageType.setValue(messageType), deviceFromId, User.UserType.setValue(deviceFromType), destinationId, ContactEntity.ContactType.setValue(destinationType), data, groupId, ContactEntity.ContactType.setValue(groupType), destinationState, status, createdAt, sentAt, receivedAt);
            messageEntity.setMultimediaEntity(getMultimedia(messageEntity.getId()));
            notificationEntities.add(messageEntity);
        }
        cursor.close();
        return notificationEntities;
    }

    public MultimediaEntity getMultimedia(String messageEntityId) {
        Cursor cursor = repository.query(
                MultimediaBase.TABLE_NAME,   // The table to query
                MultimediaBase.SQL_SELECT_ALL,             // The array of columns to return (pass null to get all)
                MultimediaBase.COL_MESSAGE_ID + "=?",              // The columns for the WHERE clause
                new String[] { messageEntityId },          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        MultimediaEntity multimediaEntity = null;
        if(cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(MultimediaBase._ID));
            String messageId = cursor.getString(cursor.getColumnIndexOrThrow(MultimediaBase.COL_MESSAGE_ID));
            String localUri = cursor.getString(cursor.getColumnIndexOrThrow(MultimediaBase.COL_LOCAL_URI));
            String firebaseUri = cursor.getString(cursor.getColumnIndexOrThrow(MultimediaBase.COL_FIREBASE_URI));
            multimediaEntity = new MultimediaEntity(id, messageId, localUri, firebaseUri);
        }
        cursor.close();
        return multimediaEntity;
    }

    public void update(ContentValues contentValues, String whereClause, String[] whereArgs) {
        repository.update(MessageBase.TABLE_NAME, contentValues, whereClause, whereArgs);
    }

    public void updateMultimedia(ContentValues contentValues, String whereClause, String[] whereArgs) {
        repository.update(MultimediaBase.TABLE_NAME, contentValues, whereClause, whereArgs);
    }
}
