package com.example.techtik.cuttoff.Models;

public class CustomRecordingsModel {
    private int id;
    private String customRecordings;

    public CustomRecordingsModel(int id, String customRecordings) {
        this.id = id;
        this.customRecordings = customRecordings;
    }

    public int getId() {
        return id;
    }

    public String getCustomRecordings() {
        return customRecordings;
    }
}
