package com.example.mynadma;

public class OfflineLIneCacheCard {
    private String title;
    private String dateDownloaded;

    // Constructor, getters, and setters
    public OfflineLIneCacheCard(String title, String dateDownloaded) {
        this.title = title;
        this.dateDownloaded = dateDownloaded;
    }

    public String getTitle() {
        return title;
    }

    public String getDateDownloaded() {
        return dateDownloaded;
    }
}
