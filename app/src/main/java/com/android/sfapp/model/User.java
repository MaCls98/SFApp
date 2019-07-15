package com.android.sfapp.model;

public class User {

    private String userDoc;
    private String password;

    public User(String userDoc, String password) {
        this.userDoc = userDoc;
        this.password = password;
    }

    public String getUserDoc() {
        return userDoc;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userDoc='" + userDoc + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
