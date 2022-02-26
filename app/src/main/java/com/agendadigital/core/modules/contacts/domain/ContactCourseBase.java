package com.agendadigital.core.modules.contacts.domain;

import android.provider.BaseColumns;

public class ContactCourseBase implements BaseColumns {
    public static final String TABLE_NAME = "contactCourse";
    public static final String COL_COURSE_ID = "courseId";
    public static final String COL_COURSE_DESCRIPTION = "courseDescription";
    public static final String COL_CONTACT_ID = "contactId";
    public static final String COL_CONTACT_TYPE = "contactType";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL_COURSE_ID + " TEXT," +
                    COL_COURSE_DESCRIPTION + " TEXT," +
                    COL_CONTACT_ID + " TEXT," +
                    COL_CONTACT_TYPE + " INTEGER);"
            ;

    public static final String[] SQL_SELECT_ALL = {
            _ID,
            COL_COURSE_ID,
            COL_COURSE_DESCRIPTION,
            COL_CONTACT_ID,
            COL_CONTACT_TYPE
    };

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
