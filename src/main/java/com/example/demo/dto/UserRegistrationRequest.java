package com.example.demo.dto;

public class UserRegistrationRequest {
    private String username;
    private String password;
    private String nationality;
    private boolean usernameEmail;
    private String skyOrigin;

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public boolean isUsernameEmail() { return usernameEmail; }
    public void setUsernameEmail(boolean usernameEmail) { this.usernameEmail = usernameEmail; }
    public String getSkyOrigin() { return skyOrigin; }
    public void setSkyOrigin(String skyOrigin) { this.skyOrigin = skyOrigin; }
}