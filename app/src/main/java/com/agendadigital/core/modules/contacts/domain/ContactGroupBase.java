package com.agendadigital.core.modules.contacts.domain;

import android.provider.BaseColumns;

public class ContactGroupBase implements BaseColumns {
    public static final String TABLE_NAME = "contactGroup";
    public static final String COL_GROUP_ID = "groupId";
    public static final String COL_GROUP_DESCRIPTION = "groupDescription";
    public static final String COL_GROUP_TYPE = "groupType";
    public static final String COL_CONTACT_ID = "contactId";
    public static final String COL_CONTACT_TYPE = "contactType";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL_GROUP_ID + " TEXT," +
                    COL_GROUP_DESCRIPTION + " TEXT," +
                    COL_GROUP_TYPE + " INTEGER," +
                    COL_CONTACT_ID + " TEXT," +
                    COL_CONTACT_TYPE + " INTEGER);"
            ;

    public static final String[] SQL_SELECT_ALL = {
            _ID,
            COL_GROUP_ID,
            COL_GROUP_DESCRIPTION,
            COL_GROUP_TYPE,
            COL_CONTACT_ID,
            COL_CONTACT_TYPE
    };

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
