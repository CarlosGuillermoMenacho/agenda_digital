package com.agendadigital.core.shared.domain.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.agendadigital.core.modules.login.domain.UserBase;

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 19;
    public static final String DATABASE_NAME = "Chat.db";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FeedReaderContract.FeedMessage.SQL_CREATE_TABLE);
        db.execSQL(UserBase.SQL_CREATE_TABLE);
        db.execSQL(FeedReaderContract.FeedContact.SQL_CREATE_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(FeedReaderContract.FeedMessage.SQL_DROP_TABLE);
        db.execSQL(UserBase.SQL_DROP_TABLE);
        db.execSQL(FeedReaderContract.FeedContact.SQL_DROP_TABLE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
