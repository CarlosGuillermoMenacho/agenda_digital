package com.agendadigital.core.modules.messages.domain;

import android.provider.BaseColumns;

public class MessageBase implements BaseColumns {

    public static final String TABLE_NAME = "messages";
    public static final String COL_MESSAGE_TYPE = "messageType";
    public static final String COL_DEVICE_FROM_ID = "deviceFromId";
    public static final String COL_DEVICE_FROM_TYPE = "deviceFromType";
    public static final String COL_DESTINATION_ID = "destinationId";
    public static final String COL_DESTINATION_TYPE = "destinationType";
    public static final String COL_DATA = "data";
    public static final String COL_FOR_GROUP = "forGroup";
    public static final String COL_DESTINATION_STATE = "destinationState";
    public static final String COL_STATE = "state";
    public static final String COL_CREATED_AT = "createdAt";
    public static final String COL_SENT_AT = "sentAt";
    public static final String COL_RECEIVED_AT = "receivedAt";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " TEXT," +
                    COL_MESSAGE_TYPE + " INTEGER," +
                    COL_DEVICE_FROM_ID + " TEXT," +
                    COL_DEVICE_FROM_TYPE + " INTEGER," +
                    COL_DESTINATION_ID + " TEXT," +
                    COL_DESTINATION_TYPE + " INTEGER," +
                    COL_DATA + " TEXT," +
                    COL_FOR_GROUP + " INTEGER," +
                    COL_DESTINATION_STATE + " INTEGER," +
                    COL_STATE + " INTEGER," +
                    COL_CREATED_AT + " DATE," +
                    COL_SENT_AT + " DATE," +
                    COL_RECEIVED_AT + " DATE, " +
            "PRIMARY KEY (" + _ID + "," + COL_CREATED_AT + "));"
            ;

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String[] SQL_SELECT_ALL = {
            _ID,
            COL_MESSAGE_TYPE,
            COL_DEVICE_FROM_ID,
            COL_DEVICE_FROM_TYPE,
            COL_DESTINATION_ID,
            COL_DESTINATION_TYPE,
            COL_DATA,
            COL_FOR_GROUP,
            COL_DESTINATION_STATE,
            COL_STATE,
            COL_CREATED_AT,
            COL_SENT_AT,
            COL_RECEIVED_AT
    };

    public static final String sortOrder =
            COL_SENT_AT + " ASC";
}
