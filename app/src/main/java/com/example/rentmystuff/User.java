package com.example.rentmystuff;

/**
 * This is the User class.
 * It represents a user in the application.
 * It contains the users full name and profile image URL.
 */
public class User {
    private String first_name;
    private String last_name;
    private String image_URL;

    /**
     * Empty Constructor for the User class:
     */
    public User() {
        //empty constructor
    }

    /**
     * Constructor for the User class:
     */
    public User(String first_name, String last_name, String image_URL) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.image_URL = image_URL;
    }

    /**
     * Constructor for the User class:
     */
    public User(String first_name, String last_name) {
        this.first_name = first_name;
        this.last_name = last_name;
    }

    /**
     * Getters:
     */
    public String getImage_URL() {
        return image_URL;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    /**
     * Setters:
     */
    public void setImage_URL(String image_URL) {
        this.image_URL = image_URL;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}
