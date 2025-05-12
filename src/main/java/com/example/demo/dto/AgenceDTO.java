package com.example.demo.dto;

public class AgenceDTO {
    private Long id;
    private String name;
    private String address;
    private String country;
    private Double latitude;
    private Double longitude;
    private int userCount;

    // Default constructor
    public AgenceDTO() {
    }

    // Constructor with all fields
    public AgenceDTO(Long id, String name, String address, String country, Double latitude, Double longitude, int userCount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userCount = userCount;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }
}