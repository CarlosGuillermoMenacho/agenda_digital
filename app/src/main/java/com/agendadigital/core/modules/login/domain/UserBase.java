package com.agendadigital.core.modules.login.domain;

import android.provider.BaseColumns;

public class UserBase implements BaseColumns {
    public static final String TABLE_NAME = "users";
    public static final String COL_NAME = "name";
    public static final String COL_TOKEN = "token";
    public static final String COL_USER_TYPE = "userType";

    public static final String[] SQL_SELECT_ALL = {
            _ID,
            COL_NAME,
            COL_TOKEN
    };

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " TEXT PRIMARY KEY," +
                COL_NAME + " INTEGER," +
                COL_TOKEN + " TEXT," +
                COL_USER_TYPE + " INTEGER);"
            ;
    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
