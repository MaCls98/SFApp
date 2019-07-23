package com.android.sfapp.model;

public class Machine {

    private int id;
    private String name;
    private String date;
    private String status;

    public Machine(int id, String name, String date, String status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
