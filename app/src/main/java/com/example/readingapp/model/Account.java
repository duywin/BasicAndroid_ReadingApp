package com.example.readingapp.model;

public class Account {
    private int id;
    private String username;
    private String password;
    private String dob;
    private boolean gender; // true = male, false = female
    private int type; // 1 = Normal User, 2 = Premium User, 3 = Admin

    public Account(int id, String username, String password, String name, String dob, boolean gender, int type) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.dob = dob;
        this.gender = gender;
        this.type = type;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDob() { return dob; }
    public boolean isGender() { return gender; }
    public int getType() { return type; }
}
