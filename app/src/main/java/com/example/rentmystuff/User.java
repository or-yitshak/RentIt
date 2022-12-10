package com.example.rentmystuff;

public class User {
    private String first_name;
    private String last_name;
    private String image_URL;

    public User() {
        //empty constructor
    }

    public User(String first_name, String last_name, String image_URL) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.image_URL = image_URL;
    }

    public User(String first_name, String last_name) {
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public String getImage_URL() {
        return image_URL;
    }

    public void setImage_URL(String image_URL) {
        this.image_URL = image_URL;
    }



    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}
