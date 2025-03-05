package com.example.readingapp.model;

public class Account {
    private int id;
    private String username, email, password, dob;
    private boolean gender; // true = male, false = female
    private int type; // 1 = Normal, 2 = Premium, 3 = Admin

    // Constructor for full account details
    public Account(int id, String username, String email, String password, String dob, boolean gender, int type) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.dob = dob;
        this.gender = gender;
        this.type = type;
    }

    // Constructor for inserting an account without an ID (ID is auto-incremented)
    public Account(String username, String email, String password, String dob, boolean gender, int type) {
        this(0, username, email, password, dob, gender, type);
    }

    // Constructor for login validation
    public Account(int id, int type) {
        this.id = id;
        this.type = type;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getDob() { return dob; }
    public boolean isGender() { return gender; }
    public int getType() { return type; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setDob(String dob) { this.dob = dob; }
    public void setGender(boolean gender) { this.gender = gender; }
    public void setType(int type) { this.type = type; }
}
