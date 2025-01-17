package com.example.mynadma;

public class Disaster {
    private String title;
    private String date;
    private String desc;

    public Disaster() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Disaster(String title, String date, String desc) {
        this.title = title;
        this.date = date;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
