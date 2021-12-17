package com.agendadigital.core.shared.domain.database;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.modules.login.domain.UserEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedReaderContract {
    public FeedReaderContract() {}

    public static class FeedMessage implements BaseColumns {
        public static final String TABLE_NAME = "messages";
        public static final String COL_MESSAGE_TYPE_ID = "messageTypeId";
        public static final String COL_DEVICE_FROM_ID = "deviceFromId";
        public static final String COL_DESTINATION_ID = "destinationId";
        public static final String COL_DATA = "data";
        public static final String COL_FOR_GROUP = "forGroup";
        public static final String COL_DESTINATION_STATUS = "destinationStatus";
        public static final String COL_STATUS = "status";
        public static final String COL_CREATED_AT = "createdAt";
        public static final String COL_SENT_AT = "sentAt";
        public static final String COL_RECEIVED_AT = "receivedAt";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + FeedMessage.TABLE_NAME + " (" +
                        FeedMessage._ID + " TEXT PRIMARY KEY," +
                        FeedMessage.COL_MESSAGE_TYPE_ID + " INTEGEER," +
                        FeedMessage.COL_DEVICE_FROM_ID + " TEXT," +
                        FeedMessage.COL_DESTINATION_ID + " TEXT," +
                        FeedMessage.COL_DATA + " TEXT," +
                        FeedMessage.COL_FOR_GROUP + " INTEGER," +
                        FeedMessage.COL_DESTINATION_STATUS + " INTEGER," +
                        FeedMessage.COL_STATUS + " INTEGER," +
                        FeedMessage.COL_CREATED_AT + " DATE," +
                        FeedMessage.COL_SENT_AT + " DATE," +
                        FeedMessage.COL_RECEIVED_AT + " DATE)"
                ;

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + FeedMessage.TABLE_NAME;

        public static final String[] SQL_SELECT_ALL = {
                FeedMessage._ID,
                FeedMessage.COL_MESSAGE_TYPE_ID,
                FeedMessage.COL_DEVICE_FROM_ID,
                FeedMessage.COL_DESTINATION_ID,
                FeedMessage.COL_DATA,
                FeedMessage.COL_FOR_GROUP,
                FeedMessage.COL_DESTINATION_STATUS,
                FeedMessage.COL_STATUS,
                FeedMessage.COL_CREATED_AT,
                FeedMessage.COL_SENT_AT,
                FeedMessage.COL_RECEIVED_AT
        };

        public static final String sortOrder =
                FeedMessage.COL_CREATED_AT + " ASC";


        public static List<MessageEntity> findAll (Cursor cursor) throws Exception {
            List<MessageEntity> notificationEntities = new ArrayList<>();
            while(cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(_ID));
                int messageTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MESSAGE_TYPE_ID));
                String deviceFromId = cursor.getString(cursor.getColumnIndexOrThrow(COL_DEVICE_FROM_ID));
                String destinationId = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESTINATION_ID));
                String data = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATA));
                int forGroup = cursor.getInt(cursor.getColumnIndexOrThrow(COL_FOR_GROUP));
                MessageEntity.DestinationState destionationState = MessageEntity.DestinationState.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(COL_DESTINATION_STATUS)));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(COL_STATUS));
                Date createdAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)));
                Date sentAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COL_SENT_AT)));
                Date receivedAt = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COL_RECEIVED_AT)));
                notificationEntities.add(new MessageEntity(id, messageTypeId, deviceFromId, destinationId, data, forGroup, destionationState, status, createdAt, sentAt, receivedAt));
            }
            return notificationEntities;
        }

        public static String SQL_SELECT_LASTS_MESSAGES() {
            return
                    "SELECT a.* " +
                    "FROM " + TABLE_NAME + " as a " +
                    "INNER JOIN (SELECT "
                                + "(SELECT " +_ID + " FROM " + TABLE_NAME + " as x WHERE x.deviceFromId = agrupado.deviceFromId ORDER BY x.createdAt DESC LIMIT 1) as id "
                                +   " FROM (SELECT " + COL_DEVICE_FROM_ID + " FROM " + TABLE_NAME + " GROUP BY " + COL_DEVICE_FROM_ID + ") as agrupado) as b " +
                        " ON (b.id = a." + _ID + ") ";
        }

        public static String SQL_SELECT_FIND_BY_ID(String id) {
            return "SELECT * FROM " + TABLE_NAME + " WHERE " + _ID + " = '" + id + "'";
        }
    }

    public static class FeedUser implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COL_NAME = "name";
        public static final String COL_TOKEN = "token";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + FeedUser.TABLE_NAME + " (" +
                        FeedUser._ID + " TEXT PRIMARY KEY," +
                        FeedUser.COL_NAME + " INTEGER," +
                        FeedUser.COL_TOKEN + " TEXT );"
                ;

        public static final String[] SQL_SELECT_ALL = {
                FeedUser._ID,
                FeedUser.COL_NAME,
                FeedUser.COL_TOKEN
        };

        public static UserEntity findById (Cursor cursor) {
            UserEntity userEntity = null;
            if(cursor.moveToNext()) {
                String  id = cursor.getString(cursor.getColumnIndexOrThrow(_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                String token = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOKEN));
                userEntity = new UserEntity(id, name, token, null);
            }
            return userEntity;
        }
        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + FeedUser.TABLE_NAME;

        public static String SQL_SELECT_FIND_BY_ID(String id) {
            return "SELECT * FROM " + FeedUser.TABLE_NAME + " WHERE " + _ID + " = '" + id + "'";
        }
    }

    public static class FeedContact implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String COL_NAME = "name";
        public static final String COL_TYPE_CONTACT = "typeContact";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + FeedContact.TABLE_NAME + " (" +
                        FeedContact._ID + " TEXT," +
                        FeedContact.COL_NAME + " INTEGER," +
                        FeedContact.COL_TYPE_CONTACT + " INTEGER," +
                        "PRIMARY KEY (" + FeedContact._ID + "," + FeedContact.COL_TYPE_CONTACT + "));"
                ;

        public static final String[] SQL_SELECT_ALL = {
                FeedContact._ID,
                FeedContact.COL_NAME,
                FeedContact.COL_TYPE_CONTACT
        };

        public static ContactEntity findById (Cursor cursor) throws Exception {
            ContactEntity contactEntity = null;
            if(cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                int typeContact = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TYPE_CONTACT));
                ContactEntity.ContactType contactTypeEnum = ContactEntity.ContactType.setValue(typeContact);
                contactEntity = new ContactEntity(id, name, contactTypeEnum);
            }
            return contactEntity;
        }

        public static List<ContactEntity> findAll (Cursor cursor) throws Exception {
            List<ContactEntity> contactEntityList = new ArrayList<>();
            while(cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                int typeContact = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TYPE_CONTACT));
                ContactEntity.ContactType contactTypeEnum = ContactEntity.ContactType.setValue(typeContact);
                contactEntityList.add(new ContactEntity(id, name, contactTypeEnum));
            }
            return contactEntityList;
        }

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + FeedContact.TABLE_NAME;

        public static String SQL_SELECT_FIND_BY_ID(String id) {
            return "SELECT * FROM " + FeedContact.TABLE_NAME + " WHERE " + _ID + " = '" + id + "'";
        }
    }
}
