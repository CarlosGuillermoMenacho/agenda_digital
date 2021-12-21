package com.agendadigital.core.modules.contacts.domain;

import android.provider.BaseColumns;

public class ContactBase implements BaseColumns {

    public static final String TABLE_NAME = "contacts";
    public static final String COL_NAME = "name";
    public static final String COL_TYPE_CONTACT = "typeContact";
    public static final String COL_UNREAD_MESSAGES = "unreadMessages";
    public static final String COL_LAST_MESSAGE_DATA = "lastMessageData";
    public static final String COL_LAST_MESSAGE_RECEIVED_AT = "lastMessageReceivedAt";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " TEXT," +
                    COL_NAME + " INTEGER," +
                    COL_TYPE_CONTACT + " INTEGER," +
                    COL_UNREAD_MESSAGES + " INTEGER," +
                    COL_LAST_MESSAGE_DATA + " TEXT," +
                    COL_LAST_MESSAGE_RECEIVED_AT + " DATE," +
                    "PRIMARY KEY (" + _ID + "," + COL_TYPE_CONTACT + "));"
            ;

    public static final String[] SQL_SELECT_ALL = {
            _ID,
            COL_NAME,
            COL_TYPE_CONTACT,
            COL_UNREAD_MESSAGES,
            COL_LAST_MESSAGE_DATA,
            COL_LAST_MESSAGE_RECEIVED_AT
    };

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
