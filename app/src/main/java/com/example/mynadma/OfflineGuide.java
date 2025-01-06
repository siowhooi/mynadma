package com.example.mynadma;

import androidx.annotation.NonNull;

public class OfflineGuide {
    private String title;
    private String before;
    private String during;
    private String after;
    private String key;

    // Default constructor (required for Firebase)
    public OfflineGuide() {
    }

    // Constructor
    public OfflineGuide(String title, String before, String during, String after) {
        this.title = title;
        this.before = before;
        this.during = during;
        this.after = after;
        this.key = null;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getDuring() {
        return during;
    }

    public void setDuring(String during) {
        this.during = during;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    @NonNull
    @Override
    public String toString() {
        return "OfflineGuide{" +
                "title='" + title + '\'' +
                ", before='" + before + '\'' +
                ", during='" + during + '\'' +
                ", after='" + after + '\'' +
                '}';
    }
}
