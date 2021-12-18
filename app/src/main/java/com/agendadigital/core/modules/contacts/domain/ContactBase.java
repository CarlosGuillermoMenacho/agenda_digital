package com.agendadigital.core.modules.contacts.domain;

import android.provider.BaseColumns;

public class ContactBase implements BaseColumns {

    public static final String TABLE_NAME = "contacts";
    public static final String COL_NAME = "name";
    public static final String COL_TYPE_CONTACT = "typeContact";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " TEXT," +
                    COL_NAME + " INTEGER," +
                    COL_TYPE_CONTACT + " INTEGER," +
                    "PRIMARY KEY (" + _ID + "," + COL_TYPE_CONTACT + "));"
            ;

    public static final String[] SQL_SELECT_ALL = {
            _ID,
            COL_NAME,
            COL_TYPE_CONTACT
    };

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
