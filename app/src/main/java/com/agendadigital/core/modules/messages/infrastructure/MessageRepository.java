package com.agendadigital.core.modules.messages.infrastructure;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.agendadigital.core.modules.messages.domain.MessageEntity;
import com.agendadigital.core.shared.domain.database.FeedReaderContract;
import com.agendadigital.core.shared.domain.database.FeedReaderDbHelper;
import java.util.List;

public class MessageRepository {
    private final SQLiteDatabase repository;

    public MessageRepository(Context context) {
        repository =new FeedReaderDbHelper(context).getReadableDatabase();
    }

    public long insert(MessageEntity messageEntity) {
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedMessage._ID, messageEntity.getId());
        values.put(FeedReaderContract.FeedMessage.COL_MESSAGE_TYPE_ID, messageEntity.getMessagetypeId());
        values.put(FeedReaderContract.FeedMessage.COL_DEVICE_FROM_ID, messageEntity.getDeviceFromId());
        values.put(FeedReaderContract.FeedMessage.COL_DESTINATION_ID, messageEntity.getDestinationId());
        values.put(FeedReaderContract.FeedMessage.COL_DATA, messageEntity.getData());
        values.put(FeedReaderContract.FeedMessage.COL_FOR_GROUP, messageEntity.getForGroup());
        values.put(FeedReaderContract.FeedMessage.COL_DESTINATION_STATUS, messageEntity.getDestinationState().getValue());
        values.put(FeedReaderContract.FeedMessage.COL_STATUS, messageEntity.getStatus());
        values.put(FeedReaderContract.FeedMessage.COL_CREATED_AT, messageEntity.getCreatedAt().getTime());
        values.put(FeedReaderContract.FeedMessage.COL_SENT_AT, messageEntity.getSentAt().getTime());
        values.put(FeedReaderContract.FeedMessage.COL_RECEIVED_AT, messageEntity.getReceivedAt()==null?null:messageEntity.getReceivedAt().getTime());
        return repository.insert(FeedReaderContract.FeedMessage.TABLE_NAME, null, values);
    }

    public List<MessageEntity> findAll(String contactId) throws Exception {
        return FeedReaderContract.FeedMessage.findAll(repository.query(
                FeedReaderContract.FeedMessage.TABLE_NAME,   // The table to query
                FeedReaderContract.FeedMessage.SQL_SELECT_ALL,             // The array of columns to return (pass null to get all)
                FeedReaderContract.FeedMessage.COL_DESTINATION_ID + "=? or " + FeedReaderContract.FeedMessage.COL_DEVICE_FROM_ID + " = ? ",              // The columns for the WHERE clause
                new String[] { contactId, contactId },          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                FeedReaderContract.FeedMessage.sortOrder               // The sort order
        ));
    }

    public void update(ContentValues contentValues, String whereClause, String[] whereArgs) {
        repository.update(FeedReaderContract.FeedMessage.TABLE_NAME, contentValues, whereClause, whereArgs);
    }
}
