package com.example.mynadma;

public class OfflineDisasterGuideCard {
    private String title;
    private String dateDownloaded;
    private String databaseKey;

    // Constructor, getters, and setters
    public OfflineDisasterGuideCard(String title, String dateDownloaded) {
        this.title = title;
        this.dateDownloaded = dateDownloaded;
        this.databaseKey = null;
    }

    public String getTitle() {
        return title;
    }

    public String getDateDownloaded() {
        return dateDownloaded;
    }
    public String getDatabaseKey() {
        return this.databaseKey;
    }
    public void setdatabaseKey(String key) {
        this.databaseKey = key;
    }
}
