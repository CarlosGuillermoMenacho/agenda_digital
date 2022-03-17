package com.agendadigital.core.modules.contacts.domain;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import androidx.annotation.NonNull;

public class ContactEntity implements Serializable {
    private String id;
    private String name;
    private ContactType contactType;
    private int unreadMessages;
    private String lastMessageData;
    private Date lastMessageReceived;

    public ContactEntity(String id, String name, ContactType contactType, int unreadMessages, String lastMessageData, Date lastMessageReceived) {
        this.id = id;
        this.name = name;
        this.contactType = contactType;
        this.unreadMessages = unreadMessages;
        this.lastMessageData = lastMessageData;
        this.lastMessageReceived = lastMessageReceived;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public String getLastMessageData() {
        return lastMessageData;
    }

    public void setLastMessageData(String lastMessageData) {
        this.lastMessageData = lastMessageData;
    }

    public Date getLastMessageReceived() {
        return lastMessageReceived;
    }

    public void setLastMessageReceived(Date lastMessageReceived) {
        this.lastMessageReceived = lastMessageReceived;
    }

    public static Comparator<ContactEntity> contactUnreadMessagesAndReceivedAt = new Comparator<ContactEntity>() {
        @Override
        public int compare(ContactEntity o1, ContactEntity o2) {
            return (o2.getUnreadMessages() - o1.getUnreadMessages());
        }
    };

    public static Comparator<ContactEntity> ContactLastReceivedMessages = new Comparator<ContactEntity>() {
        @Override
        public int compare(ContactEntity o1, ContactEntity o2) {
            if (o1.getLastMessageReceived() == null) {
                o1.setLastMessageReceived(new Date(Long.MIN_VALUE));
            }
            if (o2.getLastMessageReceived() == null) {
                o2.setLastMessageReceived(new Date(Long.MIN_VALUE));
            }
            return o2.getLastMessageReceived().compareTo(o1.getLastMessageReceived());
        }
    };

    public boolean isGroup() {
        return contactType == ContactEntity.ContactType.Course
                || contactType == ContactEntity.ContactType.CourseWithTutors
                || contactType == ContactEntity.ContactType.TeacherAndDirectorGroup;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public enum ContactType {
        None(0),
        Tutor(1),
        Student(2),
        Teacher(3),
        Director(4),
        Staff(5),
        Course(6),
        CourseWithTutors(7),
        TeacherAndDirectorGroup(8),
        ;

        private final int value;

        ContactType(int value) {
            this.value =value;
        }

        public int getValue() {
            return value;
        }

        public static ContactType setValue(int value) throws Exception {
            switch (value){
                case 0:
                    return None;
                case 1:
                    return Tutor;
                case 2:
                    return Student;
                case 3:
                    return Teacher;
                case 4:
                    return Director;
                case 5:
                    return Staff;
                case 6:
                    return Course;
                case 7:
                    return CourseWithTutors;
                case 8:
                    return TeacherAndDirectorGroup;
                default:
                    throw new Exception("TypeContact inválido." + value);
            }
        }

        public String getForLabel() {
            String toString;
            switch (this) {
                case None:
                    toString = "Ninguno";
                    break;
                case Tutor:
                    toString = "Tutores";
                    break;
                case Student:
                    toString = "Estudiantes";
                    break;
                case Teacher:
                    toString = "Profesores";
                    break;
                case Director:
                    toString = "Directores";
                    break;
                case Staff:
                    toString = "Administrativos";
                    break;
                case Course:
                    toString = "Cursos-Alumnos";
                    break;
                case CourseWithTutors:
                    toString = "Curso-Tutores";
                    break;
                case TeacherAndDirectorGroup:
                    toString = "Profesores-Director";
                    break;
                default:
                    toString = "Indefinido";
                    break;
            }
            return toString;
        }

        @Override
        public String toString() {
            String toString;
            switch (this) {
                case None:
                    toString = "Ninguno";
                    break;
                case Tutor:
                    toString = "Tutor";
                    break;
                case Student:
                    toString = "Estudiante";
                    break;
                case Teacher:
                    toString = "Profesor";
                    break;
                case Director:
                    toString = "Director";
                    break;
                case Staff:
                    toString = "Administrativo";
                    break;
                case Course:
                    toString = "Curso";
                    break;
                case CourseWithTutors:
                    toString = "Curso-Tutores";
                    break;
                case TeacherAndDirectorGroup:
                    toString = "Profesores-Director";
                    break;
                default:
                    toString = "Indefinido";
                    break;
            }
            return toString;
        }
    }

    public static class GroupEntity implements Serializable{
        private String courseId;
        private String courseDescription;
        private GroupType type;

        public GroupEntity(String courseId, String courseDescription, GroupType type) {
            this.courseId = courseId;
            this.courseDescription = courseDescription;
            this.type = type;
        }

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getCourseDescription() {
            return courseDescription;
        }

        public void setCourseDescription(String courseDescription) {
            this.courseDescription = courseDescription;
        }

        public GroupType getType() {
            return type;
        }

        public void setType(GroupType type) {
            this.type = type;
        }
    }

    public static class ContactGroupEntity {
        private int id;
        private GroupEntity groupEntity;
        private String contactId;
        private int contactType;

        public ContactGroupEntity(int id, GroupEntity groupEntity, String contactId, int contactType) {
            this.id = id;
            this.groupEntity = groupEntity;
            this.contactId = contactId;
            this.contactType = contactType;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public GroupEntity getGroupEntity() {
            return groupEntity;
        }

        public String getContactId() {
            return contactId;
        }

        public int getContactType() {
            return contactType;
        }

        public void setContactType(int contactType) {
            this.contactType = contactType;
        }
    }

    public enum GroupType {
        Course(1),
        Occupation(2);

        private final int value;

        GroupType(int value) {
            this.value =value;
        }

        public int getValue() {
            return value;
        }

        public static GroupType setValue(int value) throws Exception {
            switch (value){
                case 1:
                    return Course;
                case 2:
                    return Occupation;
                default:
                    throw new Exception("GroupType inválido." + value);
            }
        }
    }

}
