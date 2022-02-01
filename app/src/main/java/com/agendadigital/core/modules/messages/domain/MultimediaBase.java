package com.agendadigital.core.modules.messages.domain;

import android.provider.BaseColumns;

public class MultimediaBase implements BaseColumns {
    public static final String TABLE_NAME = "media";
    public static final String COL_MESSAGE_ID = "messageId";
    public static final String COL_LOCAL_URI = "localUri";
    public static final String COL_FIREBASE_URI = "firebaseUri";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " TEXT," +
                COL_MESSAGE_ID + " TEXT," +
                COL_LOCAL_URI + " TEXT," +
                COL_FIREBASE_URI + " TEXT, " +
                "PRIMARY KEY (" + _ID + "," + COL_MESSAGE_ID + "));"
            ;

    public static final String SQL_SELECT_ALL[] = {
            _ID,
            COL_MESSAGE_ID,
            COL_LOCAL_URI,
            COL_FIREBASE_URI
    };

    public static final String SQL_DROP_TABLE =
            "DROP TABLE " + TABLE_NAME;
}
