package com.agendadigital.core.modules.login.domain;

public class UserEntity {

    private String id;
    private String name;
    private String token;
    private UserType userType;

    public UserEntity(String id, String name, String token, UserType userType) {
        this.id = id;
        this.name = name;
        this.token = token;
        this.userType = userType;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return name;
    }

    public enum UserType {
        Tutor(1),
        Student(2),
        Teacher(3),
        Director(4),
        Staff(5);

        private final int value;

        UserType(int value) {
            this.value =value;
        }

        public int getValue() {
            return value;
        }
        public static UserType setValue(int value) throws Exception {
            switch (value){
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
                default:
                    throw new Exception("UserType inválido.");
            }
        }
    }
}

