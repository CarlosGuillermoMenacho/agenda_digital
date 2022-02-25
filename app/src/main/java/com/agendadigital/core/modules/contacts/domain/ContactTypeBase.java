package com.agendadigital.core.modules.contacts.domain;

import android.provider.BaseColumns;

public class ContactTypeBase implements BaseColumns {
    public static final String TABLE_NAME = "contactType";
    public static final String COL_DESCRIPTION = "description";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER," +
                    COL_DESCRIPTION + " INTEGER," +
                    "PRIMARY KEY (" + _ID + "));"
            ;

    public static final String[] SQL_SELECT_ALL = {
            _ID,
            COL_DESCRIPTION
    };

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
