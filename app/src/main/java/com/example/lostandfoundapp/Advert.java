package com.example.lostandfoundapp;

public class Advert {
    private int id;
    private String postType;
    private String name;
    private String phone;
    private String description;
    private String category;
    private String date;
    private String location;
    private String imageUri;
    private String timestamp;
    private double latitude;
    private double longitude;

    public Advert(int id, String postType, String name, String phone, String description, String category, String date, String location, String imageUri, String timestamp, double latitude, double longitude) {
        this.id = id;
        this.postType = postType;
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.category = category;
        this.date = date;
        this.location = location;
        this.imageUri = imageUri;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() { return id; }
    public String getPostType() { return postType; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public String getImageUri() { return imageUri; }
    public String getTimestamp() { return timestamp; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
