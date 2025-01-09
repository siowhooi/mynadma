package com.example.mynadma;

public class OfflineDisasterGuideCard {
    private String title;
    private String databaseKey;

    // Constructor, getters, and setters
    public OfflineDisasterGuideCard(String title) {
        this.title = title;
        this.databaseKey = null;
    }

    public String getTitle() {
        return title;
    }

    public String getDatabaseKey() {
        return this.databaseKey;
    }
    public void setdatabaseKey(String key) {
        this.databaseKey = key;
    }
}
