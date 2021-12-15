package com.agendadigital.core.modules.login.infrastructure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.agendadigital.core.modules.login.domain.UserBase;
import com.agendadigital.core.modules.login.domain.UserEntity;
import com.agendadigital.core.shared.domain.database.FeedReaderDbHelper;

public class UserRepository {
    private final SQLiteDatabase repository;

    public UserRepository(Context context) {
        repository =new FeedReaderDbHelper(context).getReadableDatabase();
    }

    public long insert(UserEntity newUser) {
        ContentValues values = new ContentValues();
        values.put(UserBase._ID, newUser.getId());
        values.put(UserBase.COL_NAME, newUser.getName());
        values.put(UserBase.COL_TOKEN, newUser.getToken());
        values.put(UserBase.COL_USER_TYPE, newUser.getUserType().getValue());
        return repository.insert(UserBase.TABLE_NAME, null, values);
    }

    public int deleteById(String id){
        return repository.delete(UserBase.TABLE_NAME, UserBase._ID + "=?", new String[]{ id });
    }

    public UserEntity findById(String userId) throws Exception {
        Cursor cursor= repository.query(
                UserBase.TABLE_NAME,   // The table to query
                UserBase.SQL_SELECT_ALL,             // The array of columns to return (pass null to get all)
                UserBase._ID + " = ? ",              // The columns for the WHERE clause
                new String[] { userId },          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        UserEntity userEntity = null;
        if(cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(UserBase._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(UserBase.COL_NAME));
            String token = cursor.getString(cursor.getColumnIndexOrThrow(UserBase.COL_TOKEN));
            UserEntity.UserType userType = UserEntity.UserType.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(UserBase.COL_USER_TYPE)));
            userEntity = new UserEntity(id, name, token, userType);
        }
        cursor.close();
        return userEntity;
    }
}
