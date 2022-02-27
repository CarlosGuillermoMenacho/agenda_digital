package com.agendadigital.core.modules.contacts.infrastructure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.agendadigital.clases.AdminSQLite;
import com.agendadigital.core.modules.contacts.domain.ContactBase;
import com.agendadigital.core.modules.contacts.domain.ContactCourseBase;
import com.agendadigital.core.modules.contacts.domain.ContactEntity;
import java.util.ArrayList;
import java.util.List;

public class ContactCourseRepository {

    private final SQLiteDatabase repository;

    public ContactCourseRepository(Context context) {
        repository =new AdminSQLite(context, "dbReader", null, 1).getReadableDatabase();
    }

    public long insert(ContactEntity.ContactCourseEntity contactCourseEntity) {
        ContentValues values = new ContentValues();
        values.put(ContactCourseBase.COL_COURSE_ID, contactCourseEntity.getCourseEntity().getCourseId());
        values.put(ContactCourseBase.COL_COURSE_DESCRIPTION, contactCourseEntity.getCourseEntity().getCourseDescription());
        values.put(ContactCourseBase.COL_CONTACT_ID, contactCourseEntity.getContactId());
        values.put(ContactCourseBase.COL_CONTACT_TYPE, contactCourseEntity.getContactType());
        return repository.insert(ContactCourseBase.TABLE_NAME, null, values);
    }

    public List<ContactEntity.CourseEntity> findAllCoursesByContactType(int contactType) {

        List<ContactEntity.CourseEntity> courseEntityList = new ArrayList<>();
        Cursor cursor = repository.query(ContactCourseBase.TABLE_NAME,
                new String[] { ContactCourseBase.COL_COURSE_ID, ContactCourseBase.COL_COURSE_DESCRIPTION },
                ContactCourseBase.COL_CONTACT_TYPE + " =?",
                new String[] { String.valueOf(contactType) }, ContactCourseBase.COL_COURSE_ID, null, null);
        while (cursor.moveToNext()) {
            String courseId = cursor.getString(cursor.getColumnIndexOrThrow(ContactCourseBase.COL_COURSE_ID));
            String courseDescription = cursor.getString(cursor.getColumnIndexOrThrow(ContactCourseBase.COL_COURSE_DESCRIPTION));
            ContactEntity.CourseEntity courseEntity = new ContactEntity.CourseEntity(courseId, courseDescription);
            courseEntityList.add(courseEntity);
        }
        cursor.close();
        return courseEntityList;
    }

    public List<ContactEntity.ContactCourseEntity> findAllCoursesByCourseId(String _courseId) {
        List<ContactEntity.ContactCourseEntity> contactCourseEntities = new ArrayList<>();
        Cursor cursor = repository.query(ContactCourseBase.TABLE_NAME,
                ContactCourseBase.SQL_SELECT_ALL,
                ContactCourseBase.COL_COURSE_ID + " =?",
                new String[] { String.valueOf(_courseId) }, null, null, null);
        while (cursor.moveToNext()) {
            String courseId = cursor.getString(cursor.getColumnIndexOrThrow(ContactCourseBase.COL_COURSE_ID));
            String courseDescription = cursor.getString(cursor.getColumnIndexOrThrow(ContactCourseBase.COL_COURSE_DESCRIPTION));
            ContactEntity.CourseEntity courseEntity = new ContactEntity.CourseEntity(courseId, courseDescription);
            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactCourseBase.COL_CONTACT_ID));
            int contactType = cursor.getInt(cursor.getColumnIndexOrThrow(ContactCourseBase.COL_CONTACT_TYPE));
            contactCourseEntities.add(new ContactEntity.ContactCourseEntity(0, courseEntity, contactId, contactType));
        }
        cursor.close();
        return contactCourseEntities;
    }

    public ContactEntity.ContactCourseEntity findByContactIdAndCourse(String _contactId, int _contactType, String _courseId) {
        ContactEntity.ContactCourseEntity contactCourseEntity = null;
        Cursor cursor = repository.query(ContactCourseBase.TABLE_NAME,
                ContactCourseBase.SQL_SELECT_ALL,
                ContactCourseBase.COL_CONTACT_ID + " =? and " + ContactCourseBase.COL_CONTACT_TYPE + "=? and " + ContactCourseBase.COL_COURSE_ID + "=?",
                new String[] { _contactId, String.valueOf(_contactType), _courseId }, null, null, null);
        if (cursor.moveToNext()) {
            String courseId = cursor.getString(cursor.getColumnIndexOrThrow(ContactCourseBase.COL_COURSE_ID));
            String courseDescription = cursor.getString(cursor.getColumnIndexOrThrow(ContactCourseBase.COL_COURSE_DESCRIPTION));
            ContactEntity.CourseEntity courseEntity = new ContactEntity.CourseEntity(courseId, courseDescription);
            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactCourseBase.COL_CONTACT_ID));
            int contactType = cursor.getInt(cursor.getColumnIndexOrThrow(ContactCourseBase.COL_CONTACT_TYPE));
            contactCourseEntity = new ContactEntity.ContactCourseEntity(0, courseEntity, contactId, contactType);
        }
        cursor.close();
        return contactCourseEntity;
    }

    public void delete(String contactId, int contactType, String courseId) {
        repository.delete(ContactBase.TABLE_NAME,
                ContactCourseBase.COL_CONTACT_ID + "=? and " + ContactCourseBase.COL_CONTACT_TYPE + "=? and " + ContactCourseBase.COL_COURSE_ID + "=?",
                new String[] { contactId, String.valueOf(contactType), courseId });
    }
    public void deleteAll(String contactId, int contactType) {
        repository.delete(ContactBase.TABLE_NAME,
                ContactCourseBase.COL_CONTACT_ID + "=? and " + ContactCourseBase.COL_CONTACT_TYPE + "=?",
                new String[] { contactId, String.valueOf(contactType) });
    }
    public List<ContactEntity.ContactCourseEntity> findAllCoursesByContactId(String _contactId, int _contactType) {
        List<ContactEntity.ContactCourseEntity> contactCourseEntities = new ArrayList<>();
        Cursor cursor = repository.query(ContactCourseBase.TABLE_NAME,
                ContactCourseBase.SQL_SELECT_ALL,
                ContactCourseBase.COL_CONTACT_ID + " =? and " + ContactCourseBase.COL_CONTACT_TYPE + "=? ",
                new String[] { _contactId, String.valueOf(_contactType) }, null, null, null);
        if (cursor.moveToNext()) {
            String courseId = cursor.getString(cursor.getColumnIndexOrThrow(ContactCourseBase.COL_COURSE_ID));
            String courseDescription = cursor.getString(cursor.getColumnIndexOrThrow(ContactCourseBase.COL_COURSE_DESCRIPTION));
            ContactEntity.CourseEntity courseEntity = new ContactEntity.CourseEntity(courseId, courseDescription);
            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactCourseBase.COL_CONTACT_ID));
            int contactType = cursor.getInt(cursor.getColumnIndexOrThrow(ContactCourseBase.COL_CONTACT_TYPE));
            contactCourseEntities.add(new ContactEntity.ContactCourseEntity(0, courseEntity, contactId, contactType));
        }
        cursor.close();
        return contactCourseEntities;
    }
}
