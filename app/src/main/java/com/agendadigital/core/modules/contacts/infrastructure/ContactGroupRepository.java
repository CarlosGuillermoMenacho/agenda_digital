package com.agendadigital.core.modules.contacts.infrastructure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.core.modules.contacts.domain.ContactGroupBase;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import java.util.ArrayList;
import java.util.List;

public class ContactGroupRepository {

    private final SQLiteDatabase repository;

    public ContactGroupRepository(Context context) {
        repository =new AdminSQLite(context, "dbReader", null, 1).getReadableDatabase();
    }

    public long insert(ContactEntity.ContactGroupEntity contactGroupEntity) {
        ContentValues values = new ContentValues();
        values.put(ContactGroupBase.COL_GROUP_ID, contactGroupEntity.getGroupEntity().getCourseId());
        values.put(ContactGroupBase.COL_GROUP_DESCRIPTION, contactGroupEntity.getGroupEntity().getCourseDescription());
        values.put(ContactGroupBase.COL_GROUP_TYPE, contactGroupEntity.getGroupEntity().getType().getValue());
        values.put(ContactGroupBase.COL_CONTACT_ID, contactGroupEntity.getContactId());
        values.put(ContactGroupBase.COL_CONTACT_TYPE, contactGroupEntity.getContactType());
        return repository.insert(ContactGroupBase.TABLE_NAME, null, values);
    }

    public List<ContactEntity.GroupEntity> findAllCoursesByContactType(int contactType) throws Exception {

        List<ContactEntity.GroupEntity> groupEntityList = new ArrayList<>();
        Cursor cursor = repository.query(ContactGroupBase.TABLE_NAME,
                new String[] { ContactGroupBase.COL_GROUP_ID, ContactGroupBase.COL_GROUP_DESCRIPTION, ContactGroupBase.COL_GROUP_TYPE },
                ContactGroupBase.COL_CONTACT_TYPE + " =? and " + ContactGroupBase.COL_GROUP_TYPE + "=?",
                new String[] { String.valueOf(contactType), String.valueOf((contactType == 4 || contactType == 5) ? 2: 1) }, ContactGroupBase.COL_GROUP_ID, null, null);
        while (cursor.moveToNext()) {
            String courseId = cursor.getString(cursor.getColumnIndexOrThrow(ContactGroupBase.COL_GROUP_ID));
            String courseDescription = cursor.getString(cursor.getColumnIndexOrThrow(ContactGroupBase.COL_GROUP_DESCRIPTION));
            int groupType = cursor.getInt(cursor.getColumnIndexOrThrow(ContactGroupBase.COL_GROUP_TYPE));
            ContactEntity.GroupEntity groupEntity = new ContactEntity.GroupEntity(courseId, courseDescription, ContactEntity.GroupType.setValue(groupType));
            groupEntityList.add(groupEntity);
        }
        cursor.close();
        return groupEntityList;
    }

    public List<ContactEntity.ContactGroupEntity> findAllCoursesByCourseId(String _courseId) throws Exception {
        List<ContactEntity.ContactGroupEntity> contactCourseEntities = new ArrayList<>();
        Cursor cursor = repository.query(ContactGroupBase.TABLE_NAME,
                ContactGroupBase.SQL_SELECT_ALL,
                ContactGroupBase.COL_GROUP_ID + " =?",
                new String[] { String.valueOf(_courseId) }, null, null, null);
        while (cursor.moveToNext()) {
            String courseId = cursor.getString(cursor.getColumnIndexOrThrow(ContactGroupBase.COL_GROUP_ID));
            String courseDescription = cursor.getString(cursor.getColumnIndexOrThrow(ContactGroupBase.COL_GROUP_DESCRIPTION));
            int groupType = cursor.getInt(cursor.getColumnIndexOrThrow(ContactGroupBase.COL_GROUP_TYPE));
            ContactEntity.GroupEntity groupEntity = new ContactEntity.GroupEntity(courseId, courseDescription, ContactEntity.GroupType.setValue(groupType));
            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactGroupBase.COL_CONTACT_ID));
            int contactType = cursor.getInt(cursor.getColumnIndexOrThrow(ContactGroupBase.COL_CONTACT_TYPE));
            contactCourseEntities.add(new ContactEntity.ContactGroupEntity(0, groupEntity, contactId, contactType));
        }
        cursor.close();
        return contactCourseEntities;
    }

    public void delete(String contactId, int contactType, String courseId) {
        repository.delete(ContactGroupBase.TABLE_NAME,
                ContactGroupBase.COL_CONTACT_ID + "=? and " + ContactGroupBase.COL_CONTACT_TYPE + "=? and " + ContactGroupBase.COL_GROUP_ID + "=?",
                new String[] { contactId, String.valueOf(contactType), courseId });
    }
    public void deleteAll(String contactId, int contactType) {
        repository.delete(ContactGroupBase.TABLE_NAME,
                ContactGroupBase.COL_CONTACT_ID + "=? and " + ContactGroupBase.COL_CONTACT_TYPE + "=?",
                new String[] { contactId, String.valueOf(contactType) });
    }
    public List<ContactEntity.ContactGroupEntity> findAllCoursesByContactId(String _contactId, int _contactType) throws Exception {
        List<ContactEntity.ContactGroupEntity> contactCourseEntities = new ArrayList<>();
        Cursor cursor = repository.query(ContactGroupBase.TABLE_NAME,
                ContactGroupBase.SQL_SELECT_ALL,
                ContactGroupBase.COL_CONTACT_ID + " =? and " + ContactGroupBase.COL_CONTACT_TYPE + "=? and " + ContactGroupBase.COL_GROUP_TYPE + "=?",
                new String[] { _contactId, String.valueOf(_contactType), String.valueOf((_contactType == 4 || _contactType == 5) ? 2: 1) }, null, null, null);
        if (cursor.moveToNext()) {
            String courseId = cursor.getString(cursor.getColumnIndexOrThrow(ContactGroupBase.COL_GROUP_ID));
            String courseDescription = cursor.getString(cursor.getColumnIndexOrThrow(ContactGroupBase.COL_GROUP_DESCRIPTION));
            int groupType = cursor.getInt(cursor.getColumnIndexOrThrow(ContactGroupBase.COL_GROUP_TYPE));
            ContactEntity.GroupEntity groupEntity = new ContactEntity.GroupEntity(courseId, courseDescription, ContactEntity.GroupType.setValue(groupType));
            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactGroupBase.COL_CONTACT_ID));
            int contactType = cursor.getInt(cursor.getColumnIndexOrThrow(ContactGroupBase.COL_CONTACT_TYPE));
            contactCourseEntities.add(new ContactEntity.ContactGroupEntity(0, groupEntity, contactId, contactType));
        }
        cursor.close();
        return contactCourseEntities;
    }
}
