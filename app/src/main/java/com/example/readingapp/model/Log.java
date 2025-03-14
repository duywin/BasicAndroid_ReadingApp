package com.example.readingapp.model;

public class Log {
    private int id;
    private int accountId;
    private String description;
    private String recordTime;

    public Log(int id, int accountId, String description, String recordTime) {
        this.id = id;
        this.accountId = accountId;
        this.description = description;
        this.recordTime = recordTime;
    }

    // Getters
    public int getId() { return id; }
    public int getAccountId() { return accountId; }
    public String getDescription() { return description; }
    public String getRecordTime() { return recordTime; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    public void setDescription(String description) { this.description = description; }
    public void setRecordTime(String recordTime) { this.recordTime = recordTime; }
}
